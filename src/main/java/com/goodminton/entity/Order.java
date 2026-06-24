package com.goodminton.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mã đơn hàng độc nhất (ví dụ: GM-123456)
    @Column(unique = true, nullable = false)
    private String orderCode;

    // Thông tin giao hàng (Vì khách không cần đăng nhập)
    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String phoneNumber;

    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    // Tài chính
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // Trạng thái và Thanh toán
    private String paymentMethod; // COD, MOMO, VNPAY
    private String status; // PENDING, SHIPPING, DELIVERED, CANCELLED

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
}
