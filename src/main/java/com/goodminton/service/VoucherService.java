package com.goodminton.service;

import com.goodminton.entity.Voucher;
import com.goodminton.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public Optional<Voucher> findByCode(String code) {
        return voucherRepository.findByCode(code.toUpperCase().trim());
    }

    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderTotal) {
        if (!voucher.isActive()) {
            return BigDecimal.ZERO;
        }
        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().before(new Date())) {
            return BigDecimal.ZERO;
        }
        if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            return BigDecimal.ZERO;
        }
        if (voucher.getMinOrderValue() != null && orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        if ("AMOUNT".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = voucher.getDiscountValue();
        } else if ("PERCENT".equalsIgnoreCase(voucher.getDiscountType())) {
            discount = orderTotal.multiply(voucher.getDiscountValue()).divide(new BigDecimal(100));
            if (voucher.getMaxDiscountValue() != null && discount.compareTo(voucher.getMaxDiscountValue()) > 0) {
                discount = voucher.getMaxDiscountValue();
            }
        }
        
        // Không thể giảm quá giá trị đơn hàng
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }
        
        return discount;
    }

    public void incrementUsedCount(String code) {
        Optional<Voucher> voucherOpt = findByCode(code);
        if (voucherOpt.isPresent()) {
            Voucher voucher = voucherOpt.get();
            voucher.setUsedCount(voucher.getUsedCount() + 1);
            voucherRepository.save(voucher);
        }
    }

    public Optional<Voucher> findById(Long id) {
        return voucherRepository.findById(id);
    }

    public Voucher save(Voucher voucher) {
        // Luôn lưu code dưới dạng in hoa
        if (voucher.getCode() != null) {
            voucher.setCode(voucher.getCode().toUpperCase().trim());
        }
        return voucherRepository.save(voucher);
    }

    public void deleteById(Long id) {
        voucherRepository.deleteById(id);
    }
}
