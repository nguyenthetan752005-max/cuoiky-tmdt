package com.goodminton.controller;

import com.goodminton.entity.*;
import com.goodminton.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    // ==================== ĐĂNG NHẬP ====================

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Nếu đã đăng nhập rồi thì chuyển thẳng vào dashboard
        if (session.getAttribute("adminUser") != null) {
            return "redirect:/admin";
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            if ("ADMIN".equals(user.getRole())) {
                session.setAttribute("adminUser", user);
                return "redirect:/admin";
            }
        }
        redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    @GetMapping("/debug-categories")
    @ResponseBody
    public java.util.List<com.goodminton.entity.Category> debugCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/setup-data")
    @ResponseBody
    public String setupData() {
        try {
            com.goodminton.entity.Category vot = new com.goodminton.entity.Category(null, "Vợt cầu lông (Fix)", "Các dòng vợt từ Yonex, Lining, Victor", null);
            com.goodminton.entity.Category giay = new com.goodminton.entity.Category(null, "Giày cầu lông (Fix)", "Giày bám sân tốt, chống lật cổ chân", null);
            com.goodminton.entity.Category cau = new com.goodminton.entity.Category(null, "Quả cầu lông (Fix)", "Cầu thi đấu chất lượng cao", null);
            com.goodminton.entity.Category phukien = new com.goodminton.entity.Category(null, "Phụ kiện (Fix)", "Quấn cán, phấn hút mồ hôi, phụ kiện khác", null);
            com.goodminton.entity.Category quanao = new com.goodminton.entity.Category(null, "Quần áo thể thao (Fix)", "Trang phục thi đấu cầu lông chính hãng", null);
            categoryService.save(vot);
            categoryService.save(giay);
            categoryService.save(cau);
            categoryService.save(phukien);
            categoryService.save(quanao);
            return "Đã ép buộc thêm 5 danh mục! Vui lòng quay lại trang danh mục.";
        } catch(Exception e) {
            return "Lỗi khi ép buộc thêm: " + e.getMessage();
        }
    }

    // ==================== DASHBOARD ====================

    @GetMapping("")
    public String dashboard(Model model) {
        long totalProducts = productService.count();
        long totalCategories = categoryService.count();
        long totalOrders = orderService.count();

        // Tính tổng doanh thu
        List<Order> allOrders = orderService.findAll();
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Đếm đơn theo trạng thái
        long pendingOrders = allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long shippingOrders = allOrders.stream().filter(o -> "SHIPPING".equals(o.getStatus())).count();
        long deliveredOrders = allOrders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();
        long cancelledOrders = allOrders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();

        // 5 đơn hàng mới nhất
        List<Order> recentOrders = orderService.findTop5ByOrderByCreatedAtDesc();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("shippingOrders", shippingOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("cancelledOrders", cancelledOrders);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/index";
    }

    // ==================== QUẢN LÝ DANH MỤC ====================

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@RequestParam(required = false) Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String description,
                               RedirectAttributes redirectAttributes) {
        Category category;
        if (id != null) {
            category = categoryService.findById(id).orElse(new Category());
        } else {
            category = new Category();
        }
        category.setName(name);
        category.setDescription(description);
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Đã lưu danh mục \"" + name + "\" thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục này vì vẫn còn sản phẩm thuộc danh mục!");
        }
        return "redirect:/admin/categories";
    }

    // ==================== QUẢN LÝ SẢN PHẨM ====================

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", productOpt.get());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@RequestParam(required = false) Long id,
                              @RequestParam String name,
                              @RequestParam BigDecimal price,
                              @RequestParam(required = false) String imageUrl,
                              @RequestParam(required = false) String weight,
                              @RequestParam(required = false) String tension,
                              @RequestParam(required = false) String balancePoint,
                              @RequestParam(required = false) String stiffness,
                              @RequestParam(required = false) String description,
                              @RequestParam int stockQuantity,
                              @RequestParam Long categoryId,
                              RedirectAttributes redirectAttributes) {
        Product product;
        if (id != null) {
            product = productService.findById(id).orElse(new Product());
        } else {
            product = new Product();
        }
        product.setName(name);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setWeight(weight != null && !weight.isEmpty() ? weight : "N/A");
        product.setTension(tension != null && !tension.isEmpty() ? tension : "N/A");
        product.setBalancePoint(balancePoint != null && !balancePoint.isEmpty() ? balancePoint : "N/A");
        product.setStiffness(stiffness != null && !stiffness.isEmpty() ? stiffness : "N/A");
        product.setDescription(description);
        product.setStockQuantity(stockQuantity);

        Category category = categoryService.findById(categoryId).orElse(null);
        product.setCategory(category);

        productService.save(product);
        redirectAttributes.addFlashAttribute("success", "Đã lưu sản phẩm \"" + name + "\" thành công!");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm. Sản phẩm có thể đang nằm trong đơn hàng!");
        }
        return "redirect:/admin/products";
    }

    // ==================== QUẢN LÝ ĐƠN HÀNG ====================

    @GetMapping("/orders")
    public String listOrders(@RequestParam(required = false) String status, Model model) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderService.findByStatus(status);
        } else {
            orders = orderService.findAllByOrderByCreatedAtDesc();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Optional<Order> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", orderOpt.get());
        return "admin/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        Optional<Order> orderOpt = orderService.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            orderService.save(order);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái đơn hàng " + order.getOrderCode() + " thành " + status);
        }
        return "redirect:/admin/orders/" + id;
    }

    // ==================== KIỂM DUYỆT ĐÁNH GIÁ ====================

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/reviews")
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.findAll());
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa đánh giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa đánh giá: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    // ==================== QUẢN LÝ ĐỔI/TRẢ HÀNG ====================

    @Autowired
    private ReturnRequestService returnRequestService;

    @GetMapping("/returns")
    public String listReturns(@RequestParam(required = false) String status, Model model) {
        List<ReturnRequest> returns;
        if (status != null && !status.isEmpty()) {
            returns = returnRequestService.findByStatus(status);
        } else {
            returns = returnRequestService.findAll();
        }
        model.addAttribute("returns", returns);
        model.addAttribute("selectedStatus", status);
        return "admin/returns";
    }

    @PostMapping("/returns/{id}/status")
    public String updateReturnStatus(@PathVariable Long id,
                                     @RequestParam String status,
                                     RedirectAttributes redirectAttributes) {
        try {
            ReturnRequest updated = returnRequestService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("success",
                    "Đã cập nhật trạng thái yêu cầu #" + id + " thành " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/returns";
    }
}
