package com.goodminton.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Ví dụ: FREESHIP, GIAM50K

    @Column(nullable = false)
    private String discountType; // AMOUNT hoặc PERCENT

    @Column(nullable = false)
    private BigDecimal discountValue;

    private BigDecimal minOrderValue; // Đơn hàng tối thiểu để áp dụng

    private BigDecimal maxDiscountValue; // Giảm tối đa (Dành cho loại PERCENT)

    private Integer usageLimit; // Giới hạn số lượt dùng

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer usedCount = 0; // Số lượt đã dùng

    private boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;
}
