package com.goodminton.controller;

import com.goodminton.dto.ReviewRequest;
import com.goodminton.entity.Review;
import com.goodminton.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API cho tính năng Đánh giá sản phẩm (Mục 3.1.3 trong báo cáo).
 * Frontend gọi các endpoint này bằng fetch() / AJAX.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

    @Autowired
    private ReviewService reviewService;

    /**
     * GET /api/reviews/product/{productId}
     * Lấy danh sách tất cả đánh giá của 1 sản phẩm (sắp xếp mới nhất trước).
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProductId(productId));
    }

    /**
     * POST /api/reviews
     * Khách hàng gửi đánh giá mới (không cần đăng nhập, chỉ cần nhập tên).
     * Body: { "productId": 1, "customerName": "Nguyễn Văn A", "rating": 5, "content": "Vợt rất tốt!" }
     */
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.createReview(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cảm ơn bạn đã đánh giá!",
                    "reviewId", review.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * POST /api/reviews/{id}/upvote
     * Bấm nút "Hữu ích" cho một đánh giá.
     */
    @PostMapping("/{id}/upvote")
    public ResponseEntity<?> upvote(@PathVariable Long id) {
        try {
            Review review = reviewService.upvote(id);
            return ResponseEntity.ok(Map.of("upvotes", review.getUpvotes(), "downvotes", review.getDownvotes()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/reviews/{id}/downvote
     * Bấm nút "Không hữu ích" cho một đánh giá.
     */
    @PostMapping("/{id}/downvote")
    public ResponseEntity<?> downvote(@PathVariable Long id) {
        try {
            Review review = reviewService.downvote(id);
            return ResponseEntity.ok(Map.of("upvotes", review.getUpvotes(), "downvotes", review.getDownvotes()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
