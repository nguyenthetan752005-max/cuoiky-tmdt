package com.goodminton.controller;

import com.goodminton.dto.CartItemDto;
import com.goodminton.dto.CheckoutRequest;
import com.goodminton.entity.Order;
import com.goodminton.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "shop/checkout"; // maps to templates/shop/checkout.html
    }

    @PostMapping("/api/orders/checkout")
    @ResponseBody
    public ResponseEntity<?> processCheckout(@Valid @RequestBody CheckoutRequest request) {
        try {
            Order order = orderService.createOrder(request);
            return ResponseEntity.ok(Map.of("success", true, "orderCode", order.getOrderCode()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
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
        Optional<Order> orderOpt = orderService.findByOrderCode(orderCode.trim());
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
        } else {
            model.addAttribute("error", "Không tìm thấy đơn hàng với mã " + orderCode);
        }
        return "shop/tracking";
    }
}
