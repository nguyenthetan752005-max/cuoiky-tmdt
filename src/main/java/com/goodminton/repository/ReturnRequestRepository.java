package com.goodminton.repository;

import com.goodminton.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    List<ReturnRequest> findByStatus(String status);

    List<ReturnRequest> findByOrderId(Long orderId);

    List<ReturnRequest> findAllByOrderByRequestDateDesc();
}
