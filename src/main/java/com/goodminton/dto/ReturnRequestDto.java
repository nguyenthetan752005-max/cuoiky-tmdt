package com.goodminton.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnRequestDto {

    @NotNull(message = "Vui lòng cung cấp mã đơn hàng")
    private String orderCode;

    @NotBlank(message = "Vui lòng mô tả lý do đổi/trả hàng")
    private String reason;

    // URL video/ảnh mở hộp làm bằng chứng (theo Mục 3.4.2 báo cáo)
    private String videoProofUrl;
}
