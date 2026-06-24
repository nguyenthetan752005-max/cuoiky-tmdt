package com.goodminton.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;
}
