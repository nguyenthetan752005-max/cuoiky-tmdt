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

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findAllByOrderByCreatedAtDesc() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
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

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItemDto itemDto : request.getCart()) {
            Optional<Product> productOpt = productRepository.findById(itemDto.getId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProduct(product);
                detail.setQuantity(itemDto.getQuantity());
                detail.setUnitPrice(product.getPrice());
                
                orderDetails.add(detail);
                totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(itemDto.getQuantity())));
            }
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);

        return orderRepository.save(order);
    }
}
