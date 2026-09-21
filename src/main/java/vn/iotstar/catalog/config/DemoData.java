package vn.iotstar.catalog.config;
import vn.iotstar.catalog.entity.*;
import vn.iotstar.catalog.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DemoData implements CommandLineRunner {
    private final UserRepository users; private final CategoryRepository categories; private final ProductRepository products;
    public DemoData(UserRepository users, CategoryRepository categories, ProductRepository products) {
        this.users = users; this.categories = categories; this.products = products;
    }
    @Override @Transactional public void run(String... args) {
        if (users.count() > 0 || categories.count() > 0 || products.count() > 0) return;
        User an = user("Nguyễn Minh An", "an@example.com", "0901234567");
        User linh = user("Trần Hà Linh", "linh@example.com", "0907654321");
        Category laptop = category("Laptop", "/images/laptop.svg", an, linh);
        Category accessory = category("Phụ kiện", "/images/accessory.svg", an);
        Category screen = category("Màn hình", "/images/monitor.svg", linh);
        product("Laptop học tập 14 inch", 8, "Laptop gọn nhẹ cho học tập và lập trình.", "15990000", laptop, an);
        product("Chuột không dây", 40, "Kết nối Bluetooth, thiết kế nhỏ gọn.", "290000", accessory, linh);
        product("Màn hình IPS 24 inch", 12, "Màn hình Full HD, tấm nền IPS.", "2490000", screen, linh);
        product("Bàn phím cơ", 25, "Bàn phím cơ 87 phím, kết nối USB-C.", "890000", accessory, an);
        product("Laptop hiệu năng 15 inch", 5, "Phù hợp lập trình và thiết kế đồ họa.", "22490000", laptop, an);
    }
    private User user(String name, String email, String phone) {
        User u = new User(); u.setFullname(name); u.setEmail(email); u.setPhone(phone);
        // Demo records have no usable login password; authentication is outside this assignment.
        u.setPassword("!disabled-demo-account"); return users.save(u);
    }
    private Category category(String name, String image, User... members) {
        Category c = new Category(); c.setName(name); c.setImages(image); c.setUsers(new LinkedHashSet<>(Arrays.asList(members))); return categories.save(c);
    }
    private void product(String title, int quantity, String desc, String price, Category category, User user) {
        Product p = new Product(); p.setTitle(title); p.setQuantity(quantity); p.setDesc(desc); p.setPrice(new BigDecimal(price)); p.setCategory(category); p.setUser(user); products.save(p);
    }
}
