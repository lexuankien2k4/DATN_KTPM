package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.dto.request.ColorRequest;
import com.Nhom7.DACN_KTPM.dto.response.ColorResponse;
import com.Nhom7.DACN_KTPM.entity.Color;
import com.Nhom7.DACN_KTPM.mapper.ColorMapper;
import com.Nhom7.DACN_KTPM.repository.ColorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class ColorService {

    ColorRepository colorRepository;
    ColorMapper colorMapper;

    public List<ColorResponse> getAllColors() {
        return colorRepository.findAll().stream()
                .map(colorMapper::toColorResponse)
                .collect(Collectors.toList());
    }

    public ColorResponse getColorById(Long id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + id));
        return colorMapper.toColorResponse(color);
    }

    @Transactional
    public ColorResponse createColor(ColorRequest request) {
        if (colorRepository.existsByColorCode(request.getColorCode())) {
            throw new RuntimeException("Mã màu " + request.getColorCode() + " đã tồn tại trong hệ thống.");
        }

        Color color = colorMapper.toColor(request);
        return colorMapper.toColorResponse(colorRepository.save(color));
    }

    @Transactional
    public ColorResponse updateColor(Long id, ColorRequest request) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + id));

        // Kiểm tra nếu đổi mã màu mà mã mới đã bị trùng với màu khác
        if (!color.getColorCode().equals(request.getColorCode()) &&
                colorRepository.existsByColorCode(request.getColorCode())) {
            throw new RuntimeException("Mã màu " + request.getColorCode() + " đã tồn tại trong hệ thống.");
        }

        colorMapper.updateColorFromRequest(request, color);
        return colorMapper.toColorResponse(colorRepository.save(color));
    }

    @Transactional
    public void deleteColor(Long id) {
        if (!colorRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy màu sắc để xóa.");
        }
        // Lưu ý: Trong thực tế, bạn nên check xem Color này đã được dùng trong CarStock hay chưa trước khi xóa
        colorRepository.deleteById(id);
    }
}