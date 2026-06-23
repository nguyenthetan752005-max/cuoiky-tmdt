package com.goodminton.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    // Các thông số đặc thù của cầu lông (được nhắc đến trong báo cáo)
    private String weight; // 3U, 4U, 5U...
    private String tension; // Sức căng tối đa (ví dụ: 28-30 Lbs)
    private String balancePoint; // Điểm cân bằng (ví dụ: Nặng đầu 295mm)
    private String stiffness; // Độ cứng (Cứng, dẻo, trung bình)

    @Column(columnDefinition = "TEXT")
    private String description;

    private int stockQuantity; // Tồn kho

    // Dữ liệu đánh giá (Tính toán dựa trên Bảng Review)
    private Double averageRating = 0.0;
    private int reviewCount = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
