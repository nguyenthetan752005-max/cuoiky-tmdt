package com.goodminton.service;

import com.goodminton.dto.CartItemDto;
import com.goodminton.dto.CheckoutRequest;
import com.goodminton.entity.Order;
import com.goodminton.entity.OrderDetail;
import com.goodminton.entity.Product;
import com.goodminton.repository.OrderRepository;
import com.goodminton.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private SystemConfigService systemConfigService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findAllByOrderByCreatedAtDesc() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Order> findByPhoneNumber(String phoneNumber) {
        return orderRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber);
    }

    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> findByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode);
    }

    public List<Order> findTop5ByOrderByCreatedAtDesc() {
        return orderRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public long count() {
        return orderRepository.count();
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrder(CheckoutRequest request) {
        if (request.getCart() == null || request.getCart().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống");
        }

        Order order = new Order();
        String orderCode = "GM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        order.setOrderCode(orderCode);
        order.setCustomerName(request.getCustomerName());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setEmail(request.getEmail());
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("PENDING");
        order.setCreatedAt(new Date());

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItemDto itemDto : request.getCart()) {
            if (itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ (phải > 0)");
            }
            
            Optional<Product> productOpt = productRepository.findById(itemDto.getId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                
                // Kiểm tra tồn kho
                if (product.getStockQuantity() < itemDto.getQuantity()) {
                    throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' chỉ còn " + product.getStockQuantity() + " sản phẩm trong kho.");
                }
                
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProduct(product);
                detail.setQuantity(itemDto.getQuantity());
                detail.setUnitPrice(product.getPrice());
                
                orderDetails.add(detail);
                subTotal = subTotal.add(product.getPrice().multiply(new BigDecimal(itemDto.getQuantity())));
                
                // Trừ kho sản phẩm
                product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
                productRepository.save(product);
            } else {
                throw new IllegalArgumentException("Sản phẩm không tồn tại trong hệ thống");
            }
        }

        // ================= CẬP NHẬT LOGIC PHÍ SHIP =================
        // Lấy cấu hình phí ship mặc định toàn quốc từ DB
        com.goodminton.entity.SystemConfig config = systemConfigService.getConfig();
        BigDecimal shippingFee = config.getBaseShippingFee() != null ? config.getBaseShippingFee() : new BigDecimal("30000");
        
        // Kiểm tra logic freeship
        if (config.getFreeshipThreshold() != null && subTotal.compareTo(config.getFreeshipThreshold()) >= 0) {
            shippingFee = BigDecimal.ZERO;
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            Optional<com.goodminton.entity.Voucher> voucherOpt = voucherService.findByCode(request.getVoucherCode());
            if (voucherOpt.isPresent()) {
                com.goodminton.entity.Voucher v = voucherOpt.get();
                
                // Kiểm tra chặt chẽ để chống Race Condition (nhiều người mua cùng lúc khi mã sắp hết)
                if (!v.isActive()) {
                    throw new IllegalArgumentException("Mã giảm giá đã bị vô hiệu hoá");
                }
                if (v.getExpiryDate() != null && v.getExpiryDate().before(new Date())) {
                    throw new IllegalArgumentException("Mã giảm giá đã hết hạn sử dụng");
                }
                if (v.getUsageLimit() != null && v.getUsedCount() >= v.getUsageLimit()) {
                    throw new IllegalArgumentException("Mã giảm giá này vừa mới hết lượt sử dụng, vui lòng chọn mã khác!");
                }
                if (v.getMinOrderValue() != null && subTotal.compareTo(v.getMinOrderValue()) < 0) {
                    throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu để dùng mã này");
                }

                discountAmount = voucherService.calculateDiscount(v, subTotal);
                order.setVoucherCode(v.getCode());
            } else {
                throw new IllegalArgumentException("Mã giảm giá không hợp lệ");
            }
        }

        BigDecimal finalTotal = subTotal.add(shippingFee).subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(finalTotal);
        order.setOrderDetails(orderDetails);

        Order savedOrder = orderRepository.save(order);
        
        if (savedOrder.getVoucherCode() != null) {
            voucherService.incrementUsedCount(savedOrder.getVoucherCode());
        }

        return savedOrder;
    }
}
