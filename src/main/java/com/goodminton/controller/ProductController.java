package com.goodminton.controller;

import com.goodminton.entity.Product;
import com.goodminton.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        
        if (product == null) {
            return "redirect:/"; // Quay về trang chủ nếu sản phẩm không tồn tại
        }

        // Lấy các sản phẩm cùng danh mục để gợi ý (Có thể bạn cũng thích)
        List<Product> relatedProducts = productRepository.findByCategoryId(product.getCategory().getId());
        
        // Loại bỏ sản phẩm hiện tại khỏi danh sách gợi ý và chỉ lấy tối đa 4 sản phẩm
        relatedProducts.removeIf(p -> p.getId().equals(product.getId()));
        if (relatedProducts.size() > 4) {
            relatedProducts = relatedProducts.subList(0, 4);
        }

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        
        return "shop/detail"; // maps to src/main/resources/templates/shop/detail.html
    }
}
