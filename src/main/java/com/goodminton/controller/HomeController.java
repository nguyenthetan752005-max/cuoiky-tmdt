package com.goodminton.controller;

import com.goodminton.entity.Category;
import com.goodminton.entity.Product;
import com.goodminton.service.CategoryService;
import com.goodminton.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) {
        
        List<Category> categories = categoryService.findAll();
        List<Product> products;

        // Xử lý tìm kiếm và lọc cơ bản
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.findByNameContainingIgnoreCase(keyword);
        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
        } else {
            products = productService.findAll();
        }

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("keyword", keyword);
        
        return "shop/index"; // maps to src/main/resources/templates/shop/index.html
    }
}
