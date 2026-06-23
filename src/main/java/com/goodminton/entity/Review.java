package com.goodminton.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Do khách hàng mua không cần tạo tài khoản, ta lưu tên người đánh giá
    @Column(nullable = false)
    private String customerName;

    // Số sao (1 đến 5)
    @Column(nullable = false)
    private int rating;

    // Nội dung bình luận thực tế (đập cầu, độ cứng thân vợt...)
    @Column(columnDefinition = "TEXT")
    private String content;

    // Cơ chế Upvote/Downvote như trong báo cáo yêu cầu (Mục 3.1.3)
    private int upvotes = 0;
    private int downvotes = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
