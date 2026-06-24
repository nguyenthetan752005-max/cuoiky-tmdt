package com.goodminton.controller;

import com.goodminton.dto.ReturnRequestDto;
import com.goodminton.entity.ReturnRequest;
import com.goodminton.service.ReturnRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API cho tính năng Đổi/Trả hàng - Reverse Logistics (Mục 3.4.2 trong báo cáo).
 * Luồng nghiệp vụ chuẩn:
 * 1. Khách hàng vào trang Tra cứu đơn hàng, thấy nút "Yêu cầu đổi/trả".
 * 2. Khách nhập Mã đơn hàng + Lý do + URL video mở hộp (nếu có).
 * 3. Hệ thống kiểm tra đơn đã DELIVERED mới cho phép gửi.
 * 4. Admin vào Dashboard → Đổi/Trả → Duyệt hoặc Từ chối.
 */
@RestController
@RequestMapping("/api/returns")
public class ReturnApiController {

    @Autowired
    private ReturnRequestService returnRequestService;

    /**
     * POST /api/returns
     * Khách gửi yêu cầu đổi/trả hàng.
     * Body: { "orderCode": "GM-A1B2C3", "reason": "Giao sai hàng", "videoProofUrl": "https://..." }
     */
    @PostMapping
    public ResponseEntity<?> createReturnRequest(@Valid @RequestBody ReturnRequestDto dto) {
        try {
            ReturnRequest saved = returnRequestService.createReturnRequest(dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Yêu cầu đổi/trả đã được gửi thành công! Mã yêu cầu: #" + saved.getId(),
                    "requestId", saved.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
