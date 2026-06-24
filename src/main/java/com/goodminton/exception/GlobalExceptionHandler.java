package com.goodminton.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Xử lý lỗi toàn cục cho ứng dụng.
 * Thay vì hiển thị trang "Whitelabel Error Page" xấu xí của Spring,
 * controller advice này sẽ bắt lỗi và trả về thông báo dễ hiểu.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi Validation (khi @Valid thất bại trên @RequestBody).
     * Trả về danh sách các trường bị lỗi kèm message tiếng Việt.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Dữ liệu không hợp lệ: " + errors
        ));
    }

    /**
     * Bắt IllegalArgumentException (ví dụ: không tìm thấy sản phẩm, giỏ hàng trống).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", ex.getMessage()
        ));
    }

    /**
     * Bắt IllegalStateException (ví dụ: đơn hàng chưa DELIVERED thì không được đổi trả).
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "success", false,
                "message", ex.getMessage()
        ));
    }
}
