package com.goodminton.repository;

import com.goodminton.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);
    Optional<Order> findByOrderCodeOrPhoneNumber(String orderCode, String phoneNumber);
    List<Order> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
    List<Order> findByStatus(String status);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findTop5ByOrderByCreatedAtDesc();
}
