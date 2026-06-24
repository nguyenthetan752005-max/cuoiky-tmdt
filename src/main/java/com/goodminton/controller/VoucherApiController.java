package com.goodminton.controller;

import com.goodminton.entity.Voucher;
import com.goodminton.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherApiController {

    @Autowired
    private VoucherService voucherService;

    @PostMapping("/check")
    public ResponseEntity<?> checkVoucher(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        BigDecimal orderTotal = new BigDecimal(request.get("orderTotal").toString());

        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vui lòng nhập mã giảm giá"));
        }

        Optional<Voucher> voucherOpt = voucherService.findByCode(code);
        if (!voucherOpt.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mã giảm giá không tồn tại"));
        }

        Voucher voucher = voucherOpt.get();
        BigDecimal discount = voucherService.calculateDiscount(voucher, orderTotal);

        if (discount.compareTo(BigDecimal.ZERO) == 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Mã giảm giá không hợp lệ hoặc đơn hàng chưa đạt giá trị tối thiểu"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "discount", discount,
                "code", voucher.getCode(),
                "message", "Áp dụng mã giảm giá thành công!"
            ));
    }
}
