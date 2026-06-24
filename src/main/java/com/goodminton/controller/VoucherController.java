package com.goodminton.controller;

import com.goodminton.entity.Voucher;
import com.goodminton.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping("/vouchers")
    public String showVoucherPage(Model model) {
        List<Voucher> vouchers = voucherService.findAll();
        model.addAttribute("vouchers", vouchers);
        return "shop/vouchers"; // Trả về templates/shop/vouchers.html
    }
}
