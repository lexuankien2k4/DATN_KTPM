import mysql.connector
import pandas as pd
import numpy as np
# Cần cài đặt thêm thư viện này: pip install scikit-learn
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics.pairwise import cosine_similarity

# --- CẤU HÌNH & KẾT NỐI (Giữ nguyên của bạn) ---
config = {
    'user': 'root',
    'password': 'root', # Thay bằng mật khẩu thực tế nếu có
    'host': '127.0.0.1',
    'port': '3306',
    'database': 'Vinfast_db'
}

conn = None
try:
    conn = mysql.connector.connect(**config)
    query = "SELECT * FROM car_variants"
    df_sanpham = pd.read_sql(query, conn)
    print("Đã tải xong dữ liệu từ MySQL. Số lượng xe:", len(df_sanpham))

except mysql.connector.Error as e:
    print(f"Lỗi kết nối MySQL: {e}")
finally:
    if conn and conn.is_connected():
        conn.close()

# Đảm bảo có dữ liệu trước khi chạy tiếp
if df_sanpham is not None and not df_sanpham.empty:

    # BƯỚC 1: TIỀN XỬ LÝ DỮ LIỆU (DATA PREPROCESSING)

    
    # 1.1 Lựa chọn các đặc trưng (features) 
    num_cols = ['price', 'seat_capacity', 'ground_clearance', 'max_power', 'battery', 'range_nedc']
    cat_cols = ['fuel_type', 'drivetrain', 'body_type'] 

    # Xử lý các cột bị thiếu (NaN) trong database
    # Điền số 0 cho các thông số kỹ thuật trống và chữ 'Unknown' cho phân loại trống
    for col in num_cols:
        if col in df_sanpham.columns:
            df_sanpham[col] = pd.to_numeric(df_sanpham[col], errors='coerce').fillna(0)
            
    for col in cat_cols:
        if col in df_sanpham.columns:
            df_sanpham[col] = df_sanpham[col].fillna('Unknown')

    # 1.2 Biến đổi dữ liệu chữ thành số (One-Hot Encoding)
    available_cat_cols = [c for c in cat_cols if c in df_sanpham.columns]
    df_cat_encoded = pd.get_dummies(df_sanpham[available_cat_cols])

    # 1.3 Chuẩn hóa dữ liệu số (Min-Max Scaling)
    # Đưa tất cả các số về thang đo từ 0 đến 1 để Giá tiền không lấn át Số chỗ ngồi
    scaler = MinMaxScaler()
    available_num_cols = [c for c in num_cols if c in df_sanpham.columns]
    
    df_num_scaled = pd.DataFrame(scaler.fit_transform(df_sanpham[available_num_cols]), columns=available_num_cols)

    # 1.4 Ghép nối thành Ma trận đặc trưng (Feature Matrix) hoàn chỉnh
    df_features = pd.concat([df_num_scaled, df_cat_encoded], axis=1)


    # BƯỚC 2: XÂY DỰNG HÀM GỢI Ý (RECOMMENDATION ENGINE)

    
    def get_recommendations_from_ai_criteria(user_json, df_raw, df_feat, top_k=5):
        """
        Hàm xử lý JSON do Spring AI gửi sang, áp dụng Hybrid Approach
        (Kết hợp lọc Rule-based cứng và Học máy Cosine Similarity)
        """
        # --- BƯỚC 2.1: LỌC CỨNG (Rule-based) ---
        filtered_indices = df_raw.index.tolist()
        
        # Nếu người dùng có ngân sách tối đa, loại bỏ ngay các xe vượt giá
        max_price = user_json.get('max_price')
        if max_price:
            filtered_indices = df_raw[df_raw['price'] <= max_price].index.tolist()

        if not filtered_indices:
            return {"message": "Không có xe nào phù hợp với ngân sách của bạn", "data": []}

        # --- BƯỚC 2.2: TẠO VECTOR LÝ TƯỞNG (Ideal Car Vector) ---
        # Khởi tạo một chiếc xe "trong mơ" với các giá trị trung bình ban đầu
        ideal_vector = pd.DataFrame(np.zeros((1, df_feat.shape[1])), columns=df_feat.columns)
        
        # Áp các tiêu chí của người dùng vào chiếc xe lý tưởng này
        # (Lưu ý: Phải Scale các số liệu này giống hệt quy trình ở Bước 1.3)
        seat_req = user_json.get('seat_capacity')
        if seat_req and 'seat_capacity' in available_num_cols:
            # Transform giá trị user nhập vào qua scaler đã huấn luyện
            temp_df = pd.DataFrame(np.zeros((1, len(available_num_cols))), columns=available_num_cols)
            temp_df['seat_capacity'] = seat_req
            scaled_seat = scaler.transform(temp_df)[0][available_num_cols.index('seat_capacity')]
            ideal_vector.at[0, 'seat_capacity'] = scaled_seat

        fuel_req = user_json.get('fuel_type')
        if fuel_req:
            encoded_col_name = f'fuel_type_{fuel_req}'
            if encoded_col_name in ideal_vector.columns:
                ideal_vector.at[0, encoded_col_name] = 1.0 # Đánh trọng số cao cho loại nhiên liệu mong muốn

        # --- BƯỚC 2.3: TÍNH TOÁN HỌC MÁY (Cosine Similarity) ---
        # Chỉ tính toán với các xe đã lọt qua vòng lọc cứng
        df_filtered_features = df_feat.iloc[filtered_indices]
        
        # Tính khoảng cách giữa Xe lý tưởng và các Xe trong Database
        similarity_scores = cosine_similarity(ideal_vector, df_filtered_features)[0]

        # Ghép điểm số vào danh sách index để sắp xếp
        scored_cars = list(zip(filtered_indices, similarity_scores))
        scored_cars.sort(key=lambda x: x[1], reverse=True) # Sắp xếp giảm dần theo độ tương đồng

        # --- BƯỚC 2.4: ĐÓNG GÓI KẾT QUẢ ---
        results = []
        for idx, score in scored_cars[:top_k]:
            car = df_raw.iloc[idx]
            results.append({
                "car_id": int(car['id']),
                "name": str(car['name']),
                "price": float(car['price']),
                "similarity_score": round(float(score), 4)
            })

        return {"status": "success", "data": results}

    # ==========================================================
    # CHẠY THỬ NGHIỆM
    # ==========================================================
    # Giả lập JSON do Spring Boot (Spring AI) bắn sang
    mock_spring_ai_request = {
        "max_price": 900000000,   # Tối đa 900 triệu
        "seat_capacity": 7,       # Cần xe 7 chỗ
        "fuel_type": "Xăng"       # Thích máy xăng
    }

    print("\n[AI PYTHON] Đang xử lý yêu cầu từ Spring Boot...")
    ket_qua = get_recommendations_from_ai_criteria(mock_spring_ai_request, df_sanpham, df_features)
    print(ket_qua)