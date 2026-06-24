package com.goodminton.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
    private String customerName;
    private String phoneNumber;
    private String email;
    private String shippingAddress;
    private String paymentMethod;
    private List<CartItemDto> cart;
}
