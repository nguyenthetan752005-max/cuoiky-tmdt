package com.goodminton.config;

import com.goodminton.entity.Category;
import com.goodminton.entity.Product;
import com.goodminton.entity.User;
import com.goodminton.repository.CategoryRepository;
import com.goodminton.repository.ProductRepository;
import com.goodminton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Tạo tài khoản Admin mặc định nếu chưa có
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User(null, "admin", "admin", "ADMIN");
            userRepository.save(admin);
            System.out.println(">>> Đã tạo tài khoản Admin mặc định: admin/admin");
        }

        // Chỉ seed dữ liệu mẫu khi database chưa có sản phẩm/danh mục.
        if (productRepository.count() > 0 || categoryRepository.count() > 0) {
            System.out.println(">>> Sample data already exists. Skip seeding products and categories.");
            return;
        }
            
        // 1. Tạo 5 danh mục tương ứng với các folder ảnh của bạn
        Category vot = new Category(null, "Vợt cầu lông", "Các dòng vợt từ Yonex, Lining, Victor", null);
        Category giay = new Category(null, "Giày cầu lông", "Giày bám sân tốt, chống lật cổ chân", null);
        Category cau = new Category(null, "Quả cầu lông", "Cầu thi đấu chất lượng cao", null);
        Category phukien = new Category(null, "Phụ kiện", "Quấn cán, phấn hút mồ hôi, phụ kiện khác", null);
        Category quanao = new Category(null, "Quần áo thể thao", "Trang phục thi đấu cầu lông chính hãng", null);
        
        categoryRepository.saveAll(Arrays.asList(vot, giay, cau, phukien, quanao));

        // 2. Tạo sản phẩm mẫu (Ánh xạ 100% chính xác với các file bạn vừa thả vào folder)
        
        // --- 2.1 VỢT CẦU LÔNG ---
        productRepository.save(new Product(
                null, "Yonex Astrox 100ZZ Cổ Điển", new BigDecimal("4250000"),
                "/images/vot/yonex-astrox-100zz.jpg", "4U", "28 Lbs", "Nặng đầu (301mm)", "Siêu cứng",
                "Dòng vợt cao cấp nhất của Yonex chuyên dành cho lối chơi tấn công uy lực.", 15, 0.0, 0, vot
        ));
        productRepository.save(new Product(
                null, "Li-Ning Aeronaut 9000C", new BigDecimal("3900000"),
                "/images/vot/lining-aeronaut-9000c.webp", "3U", "30 Lbs", "Thiên công (298mm)", "Cứng trung bình",
                "Thiết kế khe thoát khí độc đáo giảm tối đa lực cản gió, tăng tốc độ vung vợt.", 8, 0.0, 0, vot
        ));
        productRepository.save(new Product(
                null, "Victor Thruster Ryuga II TD", new BigDecimal("3600000"),
                "/images/vot/victor-thruster-ryuga-ii-td.webp", "4U", "31 Lbs", "Siêu nặng đầu (307mm)", "Cứng",
                "Vũ khí chiến đấu của siêu sao Lee Zii Jia, nổi tiếng với khả năng tấn công dồn dập.", 12, 0.0, 0, vot
        ));
        productRepository.save(new Product(
                null, "Yonex Nanoflare 1000Z", new BigDecimal("4100000"),
                "/images/vot/yonex-nanoflare-1000z.webp", "4U", "28 Lbs", "Nhẹ đầu", "Rất cứng",
                "Dòng vợt lập kỷ lục thế giới về tốc độ đập cầu, chuyên cho lối chơi phản tạt.", 10, 0.0, 0, vot
        ));
        productRepository.save(new Product(
                null, "Li-Ning Halbertec 9000", new BigDecimal("3850000"),
                "/images/vot/lining-halbertec-9000.webp", "4U", "30 Lbs", "Cân bằng", "Cứng",
                "Dòng vợt công thủ toàn diện cực kỳ ổn định, kiểm soát đường cầu chính xác.", 14, 0.0, 0, vot
        ));

        // --- 2.2 GIÀY CẦU LÔNG ---
        productRepository.save(new Product(
                null, "Giày Yonex Power Cushion 65Z3 Men", new BigDecimal("2890000"),
                "/images/giay/yonex-shb-65z3-men.webp", "N/A", "N/A", "N/A", "N/A",
                "Công nghệ Power Cushion+ giảm chấn tối đa và phản hồi năng lượng cực tốt.", 20, 0.0, 0, giay
        ));
        productRepository.save(new Product(
                null, "Giày Yonex Aerus Z Wide Flash Green", new BigDecimal("3100000"),
                "/images/giay/yonex-aerus-z-wide-flash-green.webp", "N/A", "N/A", "N/A", "N/A",
                "Dòng giày siêu nhẹ cao cấp giúp di chuyển linh hoạt và thanh thoát trên sân.", 15, 0.0, 0, giay
        ));
        productRepository.save(new Product(
                null, "Giày Li-Ning AYZW007", new BigDecimal("2450000"),
                "/images/giay/lining-ayzw007.webp", "N/A", "N/A", "N/A", "N/A",
                "Thiết kế ôm chân, đệm lót êm ái chống chấn thương cổ chân hiệu quả.", 18, 0.0, 0, giay
        ));
        productRepository.save(new Product(
                null, "Giày Asics Upcourt 6 Men", new BigDecimal("1690000"),
                "/images/giay/Asics Upcourt 6 Men.webp", "N/A", "N/A", "N/A", "N/A",
                "Dòng giày bền bỉ có phần đế chống mài mòn cực tốt, độ bám sân vượt trội.", 10, 0.0, 0, giay
        ));
        productRepository.save(new Product(
                null, "Giày Taro TR024-2 Trắng Đen", new BigDecimal("590000"),
                "/images/giay/taro-tr024-2-trang-den.webp", "N/A", "N/A", "N/A", "N/A",
                "Giày cầu lông giá rẻ, thiết kế thời trang, phù hợp cho người mới tập chơi.", 30, 0.0, 0, giay
        ));

        // --- 2.3 QUẢ CẦU LÔNG ---
        productRepository.save(new Product(
                null, "Hộp Cầu Lông Hải Yến S90", new BigDecimal("245000"),
                "/images/cau long/hai-yen-s90.webp", "N/A", "N/A", "N/A", "N/A",
                "Cầu có độ bền cao, đường bay ổn định, chuẩn thi đấu các giải phong trào.", 100, 0.0, 0, cau
        ));
        productRepository.save(new Product(
                null, "Hộp Cầu Lông Hyfa H88", new BigDecimal("210000"),
                "/images/cau long/hyfa-h88.webp", "N/A", "N/A", "N/A", "N/A",
                "Được làm từ lông gia cầm chất lượng cao, đường bay thẳng và độ bền tốt.", 40, 0.0, 0, cau
        ));
        productRepository.save(new Product(
                null, "Hộp Cầu Lông VS NL100", new BigDecimal("220000"),
                "/images/cau long/vs-nl100.webp", "N/A", "N/A", "N/A", "N/A",
                "Đường bay ổn định, ít hư tổn, phù hợp sử dụng cho tập luyện phong trào.", 50, 0.0, 0, cau
        ));
        productRepository.save(new Product(
                null, "Hộp Cầu Lông Victor NCS", new BigDecimal("350000"),
                "/images/cau long/victor-ncs.webp", "N/A", "N/A", "N/A", "N/A",
                "Dòng cầu lông chất lượng của Victor, được ưa chuộng nhiều ở các CLB.", 120, 0.0, 0, cau
        ));
        productRepository.save(new Product(
                null, "Hộp Cầu Lông Taro C01 New Xanh", new BigDecimal("190000"),
                "/images/cau long/taro-c01-new-xanh.webp", "N/A", "N/A", "N/A", "N/A",
                "Lông cầu tương đối dai, tốc độ bay vừa phải, giá thành rẻ cực tiết kiệm.", 60, 0.0, 0, cau
        ));

        // --- 2.4 PHỤ KIỆN ---
        productRepository.save(new Product(
                null, "Quấn Cán Yonex AC102-5 (Gói 5 Cái)", new BigDecimal("185000"),
                "/images/phu kien/yonex-ac102-5.webp", "N/A", "N/A", "N/A", "N/A",
                "Chất liệu cao su bám tay cực tốt, thấm hút mồ hôi siêu hiệu quả.", 150, 0.0, 0, phukien
        ));
        productRepository.save(new Product(
                null, "Phấn Hút Mồ Hôi Tay Victor Grip Powder", new BigDecimal("120000"),
                "/images/phu kien/phan-victor-hut-mo-hoi-tay-grip-powder.webp", "N/A", "N/A", "N/A", "N/A",
                "Giúp giảm trơn trượt tối đa cho những người có nhiều mồ hôi tay khi thi đấu.", 80, 0.0, 0, phukien
        ));
        productRepository.save(new Product(
                null, "Băng Trán Cầu Lông Yonex Indigo", new BigDecimal("145000"),
                "/images/phu kien/bang-tran-cau-long-yonex-phb001zhb1zz-indigo-chinh-hang.webp", "N/A", "N/A", "N/A", "N/A",
                "Hỗ trợ chặn mồ hôi chảy xuống mắt, mang lại cảm giác thoải mái khi đánh cầu.", 200, 0.0, 0, phukien
        ));
        productRepository.save(new Product(
                null, "Kéo Cắt Lưới Vợt Yonex", new BigDecimal("160000"),
                "/images/phu kien/keo-cat-luoi-yonex-2.webp", "N/A", "N/A", "N/A", "N/A",
                "Dụng cụ cắt lưới chuyên nghiệp khi vợt bị đứt cước để tránh móp méo khung.", 30, 0.0, 0, phukien
        ));
        productRepository.save(new Product(
                null, "Bao Cán Vợt Victor GC520DRM Vàng", new BigDecimal("60000"),
                "/images/phu kien/bao-can-vot-victor-gc520drm-vang.webp", "N/A", "N/A", "N/A", "N/A",
                "Đầu bọc cán vợt chống bụi bẩn và ẩm mốc khi cất giữ vợt trong balo.", 15, 0.0, 0, phukien
        ));

        // --- 2.5 QUẦN ÁO THỂ THAO ---
        productRepository.save(new Product(
                null, "Áo Cầu Lông Yonex TRM3281 Nile Green", new BigDecimal("350000"),
                "/images/quan ao/ao-cau-long-yonex-trm3281-nile-green.webp", "N/A", "N/A", "N/A", "N/A",
                "Chất liệu mè thể thao siêu thoáng mát, thấm hút mồ hôi và co giãn tốt.", 100, 0.0, 0, quanao
        ));
        productRepository.save(new Product(
                null, "Áo Cầu Lông Taro TR025 Nam Đỏ", new BigDecimal("280000"),
                "/images/quan ao/ao-cau-long-taro-tr025-a37-nam-do.webp", "N/A", "N/A", "N/A", "N/A",
                "Họa tiết in nhiệt sắc nét, bền màu, chất vải co giãn đa chiều thoải mái.", 90, 0.0, 0, quanao
        ));
        productRepository.save(new Product(
                null, "Quần Cầu Lông Yonex TSM3117 Lion", new BigDecimal("240000"),
                "/images/quan ao/quan-cau-long-yonex-tsm3117-lion.webp", "N/A", "N/A", "N/A", "N/A",
                "Quần đùi thun lạnh co giãn thoải mái, cạp chun dày dặn chắc chắn.", 70, 0.0, 0, quanao
        ));
        productRepository.save(new Product(
                null, "Quần Cầu Lông Taro Basic Nam Xanh Đen", new BigDecimal("210000"),
                "/images/quan ao/quan-cau-long-taro-basic-qs02-260107-nam-xanh-den.webp", "N/A", "N/A", "N/A", "N/A",
                "Kiểu dáng basic dễ mặc, phù hợp cho cả nam vận động cường độ cao.", 120, 0.0, 0, quanao
        ));
        productRepository.save(new Product(
                null, "Quần Dài Cầu Lông Victec Đen", new BigDecimal("380000"),
                "/images/quan ao/quan-dai-cau-long-victec-qdv-2301-den.webp", "N/A", "N/A", "N/A", "N/A",
                "Quần gió dài giữ ấm tốt khi khởi động và chống nắng khi di chuyển ngoài trời.", 50, 0.0, 0, quanao
        ));

        System.out.println(">>> Đã đồng bộ thành công dữ liệu 25 sản phẩm thực tế từ máy khách hàng!");
    }
}
