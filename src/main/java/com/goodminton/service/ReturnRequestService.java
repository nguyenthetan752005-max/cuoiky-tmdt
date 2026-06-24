package com.goodminton.service;

import com.goodminton.dto.ReturnRequestDto;
import com.goodminton.entity.Order;
import com.goodminton.entity.ReturnRequest;
import com.goodminton.repository.OrderRepository;
import com.goodminton.repository.ReturnRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnRequestService {

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<ReturnRequest> findAll() {
        return returnRequestRepository.findAllByOrderByRequestDateDesc();
    }

    public List<ReturnRequest> findByStatus(String status) {
        return returnRequestRepository.findByStatus(status);
    }

    public Optional<ReturnRequest> findById(Long id) {
        return returnRequestRepository.findById(id);
    }

    /**
     * Luồng nghiệp vụ Đổi/Trả hàng (Reverse Logistics):
     * 1. Khách hàng nhập Mã đơn hàng + Lý do + (tùy chọn) URL video/ảnh bằng chứng.
     * 2. Hệ thống kiểm tra đơn hàng phải ở trạng thái DELIVERED mới cho phép gửi yêu cầu.
     * 3. Tạo ReturnRequest với trạng thái PENDING.
     * 4. Admin vào Dashboard duyệt: APPROVED → hoàn tiền/gửi hàng mới, hoặc REJECTED.
     */
    @Transactional
    public ReturnRequest createReturnRequest(ReturnRequestDto dto) {
        // Tìm đơn hàng theo mã
        Order order = orderRepository.findByOrderCode(dto.getOrderCode().trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy đơn hàng với mã: " + dto.getOrderCode()));

        // Chỉ cho phép đổi/trả với đơn đã giao thành công
        if (!"DELIVERED".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Chỉ có thể yêu cầu đổi/trả với đơn hàng đã giao thành công (DELIVERED). " +
                    "Trạng thái hiện tại: " + order.getStatus());
        }

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setOrder(order);
        returnRequest.setReason(dto.getReason());
        returnRequest.setVideoProofUrl(dto.getVideoProofUrl());
        returnRequest.setStatus("PENDING");
        returnRequest.setRequestDate(new Date());

        return returnRequestRepository.save(returnRequest);
    }

    /**
     * Admin cập nhật trạng thái yêu cầu đổi/trả:
     * - APPROVED: Chấp nhận, tiến hành hoàn tiền/đổi hàng
     * - REJECTED: Từ chối (ví dụ: bằng chứng không hợp lệ)
     * - COMPLETED: Đã xử lý xong (hàng đã được đổi/hoàn tiền)
     */
    @Transactional
    public ReturnRequest updateStatus(Long id, String newStatus) {
        ReturnRequest request = returnRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu đổi/trả"));

        request.setStatus(newStatus);
        return returnRequestRepository.save(request);
    }
}
