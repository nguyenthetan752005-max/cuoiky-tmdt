package com.goodminton.controller;

import com.goodminton.entity.Voucher;
import com.goodminton.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/vouchers")
public class AdminVoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public String listVouchers(Model model) {
        List<Voucher> vouchers = voucherService.findAll();
        model.addAttribute("vouchers", vouchers);
        return "admin/vouchers";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "admin/voucher-form";
    }

    @PostMapping("/save")
    public String saveVoucher(@ModelAttribute Voucher voucher, 
                              @RequestParam(required = false) String expiryDateStr,
                              RedirectAttributes redirectAttributes) {
        try {
            if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date date = sdf.parse(expiryDateStr);
                voucher.setExpiryDate(date);
            }
            
            // Nếu là tạo mới, set usedCount = 0
            if (voucher.getId() == null) {
                voucher.setUsedCount(0);
            } else {
                // Giữ nguyên usedCount cũ
                Voucher existing = voucherService.findById(voucher.getId()).orElse(null);
                if (existing != null) {
                    voucher.setUsedCount(existing.getUsedCount());
                } else {
                    voucher.setUsedCount(0);
                }
            }
            
            voucherService.save(voucher);
            redirectAttributes.addFlashAttribute("success", "Đã lưu Voucher thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/vouchers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Voucher voucher = voucherService.findById(id).orElse(null);
        if (voucher == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy Voucher!");
            return "redirect:/admin/vouchers";
        }
        model.addAttribute("voucher", voucher);
        return "admin/voucher-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa Voucher thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa Voucher này (có thể đã được sử dụng trong đơn hàng).");
        }
        return "redirect:/admin/vouchers";
    }
}
