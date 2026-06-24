package com.goodminton.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "system_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {
    
    @Id
    private Long id = 1L; // Cấu hình hệ thống chỉ có 1 dòng duy nhất (Singleton pattern in DB)

    // === CẤU HÌNH VẬN CHUYỂN ===
    @Column(nullable = false)
    private BigDecimal baseShippingFee = new BigDecimal("30000");
    
    @Column(nullable = false)
    private BigDecimal freeshipThreshold = new BigDecimal("1000000"); // Đơn trên 1 triệu thì freeship

    // === CẤU HÌNH CỬA HÀNG ===
    @Column(nullable = false)
    private String storeName = "GOOD MINTON";
    
    @Column(nullable = false)
    private String hotline = "0987.654.321";
    
    @Column(nullable = false)
    private String contactEmail = "cskh@goodminton.vn";
    
    @Column(nullable = false)
    private String storeAddress = "123 Đường Cầu Lông, Quận Thể Thao, TP.HCM";
    
    // === CẤU HÌNH THÔNG BÁO ===
    @Column(nullable = false)
    private String globalAnnouncement = "Chào mừng đến với GOOD MINTON - Hệ thống đồ cầu lông chính hãng!";
}
