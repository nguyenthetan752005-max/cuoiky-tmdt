package com.goodminton.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "Vui lòng chọn sản phẩm để đánh giá")
    private Long productId;

    @NotBlank(message = "Vui lòng nhập tên của bạn")
    private String customerName;

    @Min(value = 1, message = "Đánh giá tối thiểu 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa 5 sao")
    private int rating;

    private String content;
}
