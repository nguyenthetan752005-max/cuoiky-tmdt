package com.goodminton.service;

import com.goodminton.dto.ReviewRequest;
import com.goodminton.entity.Product;
import com.goodminton.entity.Review;
import com.goodminton.repository.ProductRepository;
import com.goodminton.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    /**
     * Tạo đánh giá mới và tự động cập nhật điểm trung bình (averageRating)
     * cùng số lượng đánh giá (reviewCount) trên sản phẩm tương ứng.
     */
    @Transactional
    public Review createReview(ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + request.getProductId()));

        Review review = new Review();
        review.setProduct(product);
        review.setCustomerName(request.getCustomerName());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setUpvotes(0);
        review.setDownvotes(0);
        review.setCreatedAt(new Date());

        Review saved = reviewRepository.save(review);

        // Cập nhật lại averageRating và reviewCount trên Product
        recalculateProductRating(product.getId());

        return saved;
    }

    /**
     * Upvote một đánh giá (đánh dấu "Hữu ích").
     * Mô hình phổ biến: mỗi lần bấm sẽ tăng 1 upvote.
     */
    @Transactional
    public Review upvote(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá"));
        review.setUpvotes(review.getUpvotes() + 1);
        return reviewRepository.save(review);
    }

    /**
     * Downvote một đánh giá (đánh dấu "Không hữu ích").
     */
    @Transactional
    public Review downvote(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá"));
        review.setDownvotes(review.getDownvotes() + 1);
        return reviewRepository.save(review);
    }

    /**
     * Xóa đánh giá (Admin moderation) và cập nhật lại rating sản phẩm.
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá"));
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        recalculateProductRating(productId);
    }

    /**
     * Tính lại điểm trung bình sao dựa trên tất cả review hiện có của sản phẩm.
     */
    private void recalculateProductRating(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        if (reviews.isEmpty()) {
            product.setAverageRating(0.0);
            product.setReviewCount(0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            product.setAverageRating(Math.round(avg * 10.0) / 10.0); // Làm tròn 1 chữ số thập phân
            product.setReviewCount(reviews.size());
        }
        productRepository.save(product);
    }
}
