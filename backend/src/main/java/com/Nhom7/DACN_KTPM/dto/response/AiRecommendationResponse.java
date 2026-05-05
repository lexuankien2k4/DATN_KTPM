package com.Nhom7.DACN_KTPM.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendationResponse {
    @JsonProperty("car_id")
    private Long carId;

    private String name;

    @JsonProperty("image_url")
    private String imageUrl;

    private String category;
    private Double price;
    private Double matchScore;
}