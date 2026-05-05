import pandas as pd
import numpy as np
import threading
import time
import re
import logging
import unicodedata
from flask import Flask, request, jsonify
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy import create_engine

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = Flask(__name__)


# ══════════════════════════════════════════════
#  TIỆN ÍCH CHUẨN HÓA VĂN BẢN
# ══════════════════════════════════════════════

def normalize_text(text: str) -> str:
    """NFC + lowercase + dấu phẩy số → chấm + rút gọn khoảng trắng."""
    text = unicodedata.normalize("NFC", text).lower().strip()
    text = re.sub(r'(\d),(\d)', r'\1.\2', text)
    text = re.sub(r'\s+', ' ', text)
    return text


def remove_accents(text: str) -> str:
    """Bỏ dấu để so sánh fuzzy: 'gia dinh' khớp 'gia đình'."""
    return ''.join(
        c for c in unicodedata.normalize('NFKD', text)
        if not unicodedata.combining(c)
    )


# ══════════════════════════════════════════════
#  TỪ ĐIỂN INTENT
# ══════════════════════════════════════════════

TRAIT_KEYWORDS: dict[str, list[str]] = {
    "performance": [
        "mạnh", "manh", "tăng tốc", "tang toc", "thể thao", "the thao",
        "power", "bốc", "boc", "nhanh", "hiệu suất", "hieu suat",
        "turbo", "sport", "động cơ mạnh", "dong co manh",
        "lực kéo", "luc keo", "0-100", "acceleration",
        "mã lực", "ma luc", "moment", "mô men xoắn",
    ],
    "comfort": [
        "êm", "em ái", "rộng", "rong", "vững", "vung", "đầm", "dam",
        "gia đình", "gia dinh", "chắc", "chac", "thoải mái", "thoai mai",
        "ghế da", "ghe da", "yên tĩnh", "yen tinh", "cách âm", "cach am",
        "giảm xóc", "giam xoc", "hàng ghế sau", "hang ghe sau",
        "chỗ ngồi rộng", "cho ngoi rong", "legroom", "headroom",
        "di chuyển thoải mái", "di chuyen thoai mai",
    ],
    "tech": [
        "thông minh", "thong minh", "hiện đại", "hien dai",
        "công nghệ", "cong nghe", "màn hình", "man hinh",
        "adas", "autopilot", "tự lái", "tu lai", "camera 360",
        "kết nối", "ket noi", "carplay", "android auto",
        "hud", "đèn led", "den led", "cảm biến", "cam bien",
        "wifi", "bluetooth", "sạc không dây", "sac khong day",
        "smart", "ota", "digital", "số hóa", "so hoa",
        "màn hình lớn", "man hinh lon",
    ],
    "economy": [
        "rẻ", "re", "tiết kiệm", "tiet kiem", "tối ưu", "toi uu",
        "đi xa", "di xa", "pin tốt", "pin tot", "tầm hoạt động", "tam hoat dong",
        "giá thấp", "gia thap", "phổ thông", "pho thong",
        "range", "kwh", "sạc nhanh", "sac nhanh",
        "chi phí thấp", "chi phi thap", "affordable", "budget", "entry",
        "cơ bản", "co ban", "phù hợp túi tiền", "phu hop tui tien",
        "ít xăng", "it xang", "tiêu thụ thấp", "tieu thu thap",
        "uống ít", "uong it", "l/100", "lít/100", "pin trâu", "pin trau",
    ],
    "luxury": [
        "sang", "sang trọng", "sang trong", "cao cấp", "cao cap",
        "hạng sang", "hang sang", "luxury", "premium", "vip",
        "da thật", "da that", "gỗ thật", "go that",
        "nội thất xịn", "noi that xin", "đẳng cấp", "dang cap",
        "panoramic", "ghế massage", "ghe massage",
        "executive", "prestige", "xe xịn", "xe xin",
    ],
    "offroad": [
        "địa hình", "dia hinh", "off-road", "offroad", "việt dã", "viet da",
        "gầm cao", "gam cao", "4x4", "awd", "4wd",
        "leo núi", "leo nui", "lội suối", "loi suoi",
        "bùn", "bun", "đường xấu", "duong xau", "đường đất", "duong dat",
        "đường rừng", "duong rung", "terrain", "phượt", "phuot",
        "chạy đường xấu", "chay duong xau",
    ],
}

BODY_TYPE_KEYWORDS: dict[str, list[str]] = {
    "SUV":      ["suv", "crossover", "gầm cao", "gam cao", "xe gầm cao", "xe suv"],
    "Sedan":    ["sedan", "xe con", "4 cửa", "4 cua", "saloon"],
    "Hatchback":["hatchback", "hatch", "5 cửa", "5 cua", "xe nhỏ gọn", "xe nho gon"],
    "MPV":      ["mpv", "minivan", "van", "7 chỗ gia đình", "xe 7 cho", "xe van"],
    "Pickup":   ["pickup", "bán tải", "ban tai", "xe bán tải"],
    "Coupe":    ["coupe", "2 cửa", "2 cua", "mui trần", "mui tran"],
}

FUEL_KEYWORDS: dict[str, list[str]] = {
    "electric": ["điện", "dien", "ev", "electric", "xe điện", "xe dien", "thuần điện"],
    "gasoline": ["xăng", "xang", "petrol", "gasoline", "xe xăng", "xe xang"],
    "hybrid":   ["hybrid", "xăng điện", "xang dien", "hev", "phev", "mhev"],
}


# ══════════════════════════════════════════════
#  PARSER GIÁ
# ══════════════════════════════════════════════

_ABOVE_KW  = r'(?:trên|tren|hơn|hon|ít nhất|it nhat|above|over|trở lên|tro len|tối thiểu|toi thieu)'
_BELOW_KW  = r'(?:dưới|duoi|không quá|khong qua|tối đa|toi da|under|max|below)'
_APPROX_KW = r'(?:khoảng|khoa?ng|tầm|tam|xấp xỉ|xap xi|around|cỡ|tầm giá|tam gia|khoảng tầm|khoang tam)'
_NUM  = r'(\d+(?:\.\d+)?)'
_UNIT = r'(tỷ|ty|tỉ|ti|triệu|trieu|tr)'
_B, _M = 1_000_000_000, 1_000_000


def _to_vnd(val: float, unit: str) -> float:
    return val * _B if unit.strip() in ('tỷ', 'ty', 'tỉ', 'ti') else val * _M


def _classify(text: str, val: float):
    """Xác định (min, max) dựa vào keyword trong toàn bộ câu."""
    if re.search(_APPROX_KW, text): return round(val * 0.85), round(val * 1.15)
    if re.search(_BELOW_KW,  text): return None, val
    if re.search(_ABOVE_KW,  text): return val, None
    return None, val


def _extract_price(text: str) -> tuple[float | None, float | None]:
    # 1. Khoảng hai đầu "từ X đến Y"
    m = re.search(
        r'(?:từ|tu)?\s*' + _NUM + r'\s*' + _UNIT +
        r'\s*(?:đến|den|tới|toi|đến khoảng|-|~)\s*' + _NUM + r'\s*' + _UNIT,
        text
    )
    if m:
        lo = _to_vnd(float(m.group(1)), m.group(2))
        hi = _to_vnd(float(m.group(3)), m.group(4))
        return min(lo, hi), max(lo, hi)

    # 2. "X tỷ/triệu rưỡi" (+0.5)
    m = re.search(_NUM + r'\s*' + _UNIT + r'\s+(?:rưỡi|ruoi|rưoi|ruỡi)', text)
    if m:
        mult = _B if m.group(2).strip() in ('tỷ','ty','tỉ','ti') else _M
        return _classify(text, (float(m.group(1)) + 0.5) * mult)

    # 3. "X tỷ Y" (Y = 1-9, không theo sau đơn vị khác) → X.Y tỷ
    m = re.search(
        _NUM + r'\s*(?:tỷ|ty|tỉ|ti)\s+([1-9])\b'
        r'(?!\s*(?:tỷ|ty|tỉ|ti|triệu|trieu|tr))', text
    )
    if m:
        return _classify(text, float(m.group(1)) * _B + float(m.group(2)) * 100_000_000)

    # 4. Số đơn + keyword ngữ cảnh
    m = re.search(_APPROX_KW + r'\s*' + _NUM + r'\s*' + _UNIT, text)
    if m:
        v = _to_vnd(float(m.group(1)), m.group(2))
        return round(v * 0.85), round(v * 1.15)

    m = re.search(_BELOW_KW + r'\s*' + _NUM + r'\s*' + _UNIT, text)
    if m: return None, _to_vnd(float(m.group(1)), m.group(2))

    m = re.search(_ABOVE_KW + r'\s*' + _NUM + r'\s*' + _UNIT, text)
    if m: return _to_vnd(float(m.group(1)), m.group(2)), None

    # 5. Fallback: số đơn → tối đa
    m = re.search(_NUM + r'\s*' + _UNIT, text)
    if m: return None, _to_vnd(float(m.group(1)), m.group(2))

    return None, None


# ══════════════════════════════════════════════
#  PARSER INTENT ĐẦY ĐỦ
# ══════════════════════════════════════════════

def _extract_seats(text: str) -> int | None:
    for pat in [
        r'(\d+)\s*(?:chỗ|cho|ghế|ghe|seat|người|nguoi|chỗ ngồi)',
        r'(?:xe|loại|dòng|chở)\s*(\d+)\s*(?:chỗ|cho|người|nguoi)',
    ]:
        m = re.search(pat, text)
        if m:
            s = int(m.group(1))
            if 2 <= s <= 9: return s
    return None


def _extract_traits(text: str, na: str) -> list[str]:
    found = []
    for trait, kws in TRAIT_KEYWORDS.items():
        for kw in kws:
            if kw in text or remove_accents(kw) in na:
                found.append(trait); break
    return found


def _extract_body_types(text: str, na: str) -> list[str]:
    found = []
    for body, kws in BODY_TYPE_KEYWORDS.items():
        for kw in kws:
            if kw in text or remove_accents(kw) in na:
                found.append(body); break
    return found


def _extract_fuel_type(text: str, na: str) -> str | None:
    for fuel, kws in FUEL_KEYWORDS.items():
        for kw in kws:
            if kw in text or remove_accents(kw) in na:
                return fuel
    return None


def parse_intent(raw_text: str) -> dict:
    text = normalize_text(raw_text)
    na   = remove_accents(text)
    mn, mx = _extract_price(text)
    intent = {
        "min_price":     mn,
        "max_price":     mx,
        "seat_capacity": _extract_seats(text),
        "traits":        _extract_traits(text, na),
        "body_types":    _extract_body_types(text, na),
        "fuel_type":     _extract_fuel_type(text, na),
    }
    logger.info(f"Intent parsed: {intent}")
    return intent


# ══════════════════════════════════════════════
#  RECOMMENDER
# ══════════════════════════════════════════════

class VinFastRecommender:
    def __init__(self):
        self.engine      = create_engine(
            "mysql+mysqlconnector://root:root@127.0.0.1:3306/Vinfast_db"
        )
        self.df_raw      = None
        self.df_features = None
        self.scaler      = MinMaxScaler()
        self.is_ready    = False
        self.lock        = threading.Lock()

    def load_data(self):
        try:
            query = """
                SELECT
                    cv.id, cv.name, cv.price, cv.discounted_price,
                    cv.seat_capacity, cv.length, cv.width, cv.height,
                    cv.wheelbase, cv.ground_clearance,
                    cv.max_power, cv.max_torque,
                    cv.drivetrain, cv.drive_modes,
                    cv.battery, cv.range_nedc,
                    cv.wheel_size, cv.entertainment_screen,
                    cm.body_type,
                    cc.name AS category_name,
                    (SELECT ci.image_url FROM car_images ci
                     WHERE ci.variant_id = cv.id LIMIT 1) AS image_url
                FROM  car_variants   cv
                JOIN  car_models     cm ON cv.model_id    = cm.id
                JOIN  car_categories cc ON cm.category_id = cc.id
                WHERE cv.is_active = 1 AND cm.is_active = 1
            """
            df = pd.read_sql(query, self.engine)
            if df.empty:
                logger.warning("Database trống hoặc không có xe active.")
                return

            num_cols = [
                'price', 'discounted_price', 'seat_capacity',
                'length', 'width', 'height', 'wheelbase', 'ground_clearance',
                'max_power', 'max_torque', 'battery', 'range_nedc',
                'wheel_size', 'entertainment_screen',
            ]
            cat_cols = ['category_name', 'drivetrain', 'body_type']

            df[num_cols] = df[num_cols].apply(pd.to_numeric, errors='coerce').fillna(0)
            df[cat_cols] = df[cat_cols].fillna('Unknown')

            df['effective_price'] = df.apply(
                lambda r: r['discounted_price'] if r['discounted_price'] > 0 else r['price'],
                axis=1
            )

            scale_cols = [
                'effective_price', 'seat_capacity',
                'length', 'width', 'height', 'wheelbase', 'ground_clearance',
                'max_power', 'max_torque', 'battery', 'range_nedc',
                'wheel_size', 'entertainment_screen',
            ]
            df_cat        = pd.get_dummies(df[cat_cols])
            df_num_scaled = pd.DataFrame(
                self.scaler.fit_transform(df[scale_cols]),
                columns=scale_cols, index=df.index
            )

            with self.lock:
                self.df_features = pd.concat([df_num_scaled, df_cat], axis=1)
                self.df_raw      = df
                self.is_ready    = True

            logger.info(f"Đã nạp {len(df)} biến thể xe vào RAM.")
        except Exception as e:
            logger.error(f"Lỗi nạp dữ liệu: {e}")

    def recommend(self, user_text: str, top_k: int = 10) -> list | None:
        if not self.is_ready:
            return None

        intent = parse_intent(user_text)

        with self.lock:
            df_work   = self.df_raw.copy()
            feat_work = self.df_features.copy()

        # ── Hard filtering ────────────────────────────────────────
        mask = pd.Series(True, index=df_work.index)

        if intent["min_price"] is not None:
            mask &= (df_work['effective_price'] >= intent["min_price"])
        if intent["max_price"] is not None:
            mask &= (df_work['effective_price'] <= intent["max_price"])
        if intent["seat_capacity"] is not None:
            mask &= (df_work['seat_capacity'] == intent["seat_capacity"])

        if intent["body_types"]:
            bm = pd.Series(False, index=df_work.index)
            for bt in intent["body_types"]:
                bm |= (df_work['body_type'].str.lower() == bt.lower())
            mask &= bm

        # Lọc loại nhiên liệu qua category_name (do CarCategory phân loại)
        if intent["fuel_type"]:
            FUEL_CAT_MAP = {
                "electric": ["điện", "dien", "ev"],
                "gasoline": ["xăng", "xang", "petrol"],
                "hybrid":   ["hybrid", "xăng điện"],
            }
            fuel_kws = FUEL_CAT_MAP.get(intent["fuel_type"], [])
            if fuel_kws:
                fm = pd.Series(False, index=df_work.index)
                cat_lower = df_work['category_name'].str.lower()
                for kw in fuel_kws:
                    fm |= cat_lower.str.contains(kw, regex=False)
                mask &= fm

        filtered_indices = df_work[mask].index
        if len(filtered_indices) == 0:
            logger.info("Không có xe nào phù hợp sau hard filtering.")
            return []

        # ── Feature weighting ─────────────────────────────────────
        weights      = pd.Series(1.0, index=feat_work.columns)
        ideal_vector = pd.Series(0.5, index=feat_work.columns)

        WEIGHT_MAP = {
            "performance": {
                "cols":   ['max_power', 'max_torque'],
                "w":      3.0,
                "ideal":  {'max_power': 1.0, 'max_torque': 1.0},
                "extras": {'drivetrain_AWD': 3.0, 'drivetrain_4WD': 2.5},
            },
            "comfort": {
                "cols":  ['wheelbase', 'width', 'height', 'ground_clearance'],
                "w":     2.5,
                "ideal": {'wheelbase': 1.0, 'width': 1.0, 'height': 1.0},
            },
            "tech": {
                "cols":  ['entertainment_screen'],
                "w":     2.0,
                "ideal": {'entertainment_screen': 1.0},
            },
            "economy": {
                "cols":  ['range_nedc', 'battery'],
                "w":      2.5,
                "ideal": {'range_nedc': 1.0, 'battery': 0.8},
            },
            "luxury": {
                "cols":  ['effective_price', 'entertainment_screen', 'width'],
                "w":      2.5,
                "ideal": {'effective_price': 1.0, 'entertainment_screen': 1.0},
            },
            "offroad": {
                "cols":  ['ground_clearance', 'max_power', 'max_torque', 'wheel_size'],
                "w":      3.0,
                "ideal": {'ground_clearance': 1.0, 'wheel_size': 1.0},
                "extras": {'drivetrain_AWD': 3.5, 'drivetrain_4WD': 3.5},
            },
        }

        for trait in intent["traits"]:
            cfg = WEIGHT_MAP.get(trait)
            if not cfg: continue
            for col in cfg.get("cols", []):
                if col in weights.index: weights[col] = cfg["w"]
            for col, val in cfg.get("ideal", {}).items():
                if col in ideal_vector.index: ideal_vector[col] = val
            for col, val in cfg.get("extras", {}).items():
                if col in weights.index: weights[col] = val

        # ── Cosine similarity ─────────────────────────────────────
        sub_feat       = feat_work.loc[filtered_indices]
        weighted_sub   = sub_feat * weights
        weighted_ideal = (ideal_vector * weights).values.reshape(1, -1)
        scores         = cosine_similarity(weighted_ideal, weighted_sub)[0]

        results = []
        for i, idx in enumerate(filtered_indices):
            car = df_work.loc[idx]
            results.append({
                "car_id":           int(car['id']),
                "name":             car['name'],
                "image_url":        car['image_url'],
                "price":            float(car['price']),
                "discounted_price": float(car['discounted_price']) if car['discounted_price'] > 0 else None,
                "seat_capacity":    int(car['seat_capacity']),
                "body_type":        car['body_type'],
                "category_name":    car['category_name'],
                "match_score":      round(float(scores[i]) * 100, 1),
            })

        results.sort(key=lambda x: x['match_score'], reverse=True)
        return results[:top_k]


# ══════════════════════════════════════════════
#  FLASK APP
# ══════════════════════════════════════════════

recommender = VinFastRecommender()


def _bg_update():
    while True:
        recommender.load_data()
        time.sleep(600)


threading.Thread(target=_bg_update, daemon=True).start()


@app.route('/api/ai/recommend', methods=['POST'])
def recommend_api():
    data    = request.json or {}
    message = data.get('message', '').strip()
    if not message:
        return jsonify({"status": "error", "message": "Thiếu trường 'message'"}), 400
    logger.info(f"Yêu cầu tư vấn: {message}")
    results = recommender.recommend(message)
    if results is None:
        return jsonify({"status": "error", "message": "Hệ thống đang khởi động, vui lòng thử lại sau"}), 503
    return jsonify({"status": "success", "data": results})


if __name__ == '__main__':
    app.run(port=5000, debug=False)