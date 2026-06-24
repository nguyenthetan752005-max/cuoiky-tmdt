package com.goodminton.controller;

import com.goodminton.dto.CartItemDto;
import com.goodminton.dto.CheckoutRequest;
import com.goodminton.entity.Order;
import com.goodminton.entity.OrderDetail;
import com.goodminton.entity.Product;
import com.goodminton.repository.OrderDetailRepository;
import com.goodminton.repository.OrderRepository;
import com.goodminton.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "shop/checkout"; // maps to templates/shop/checkout.html
    }

    @PostMapping("/api/orders/checkout")
    @ResponseBody
    public ResponseEntity<?> processCheckout(@RequestBody CheckoutRequest request) {
        try {
            if (request.getCart() == null || request.getCart().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Giỏ hàng trống"));
            }

            Order order = new Order();
            // Sinh mã đơn hàng ngẫu nhiên, ví dụ: GM-A1B2C
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

            // Lưu vào DB
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("success", true, "orderCode", orderCode));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage()));
        }
    }

    @GetMapping("/tracking")
    public String trackingPage() {
        return "shop/tracking"; // maps to templates/shop/tracking.html
    }

    @GetMapping("/tracking/result")
    public String trackingResult(@RequestParam("orderCode") String orderCode, Model model) {
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode.trim());
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
        } else {
            model.addAttribute("error", "Không tìm thấy đơn hàng với mã " + orderCode);
        }
        return "shop/tracking";
    }
}
