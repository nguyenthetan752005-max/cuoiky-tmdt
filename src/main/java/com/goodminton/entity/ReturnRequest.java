package com.goodminton.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với Đơn hàng nào đang bị khiếu nại
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Lý do đổi/trả (ví dụ: Gãy vợt, xước sơn, giao sai hàng)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    // Bằng chứng video mở hộp (URL lưu trữ video/ảnh như báo cáo yêu cầu ở Mục 3.4.2)
    private String videoProofUrl;

    // Trạng thái xử lý (PENDING, APPROVED, REJECTED, COMPLETED)
    @Column(nullable = false)
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;
}
