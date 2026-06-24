package com.goodminton.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
    
    @NotBlank(message = "Vui lòng nhập họ tên")
    private String customerName;
    
    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Vui lòng nhập địa chỉ giao hàng")
    private String shippingAddress;
    
    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    private String paymentMethod;
    
    @NotEmpty(message = "Giỏ hàng không được để trống")
    private List<CartItemDto> cart;
    
    private String voucherCode;
}
