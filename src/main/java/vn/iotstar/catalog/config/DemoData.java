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
        User an = user("Nguyễn Minh An", "an.nguyen@catalog.vn", "0901234567");
        User linh = user("Trần Hà Linh", "linh.tran@catalog.vn", "0907654321");
        User bao = user("Phạm Quốc Bảo", "bao.pham@catalog.vn", "0912345678");

        Category laptop = category("Laptop", "/images/laptop.svg", an, bao);
        Category phone = category("Điện thoại", "/images/phone.svg", linh, bao);
        Category accessory = category("Phụ kiện", "/images/accessory.svg", an, linh, bao);
        Category monitor = category("Màn hình", "/images/monitor.svg", an, linh);
        Category audio = category("Âm thanh", "/images/audio.svg", linh, bao);

        product("Laptop Asus Vivobook 15 OLED A1505VA", 10, "Ryzen 5 7530U, RAM 16GB, SSD 512GB, màn OLED Full HD.", "15490000", laptop, an);
        product("Laptop Dell Inspiron 15 3520", 8, "Core i5-1235U, RAM 8GB, SSD 512GB, phù hợp văn phòng và học tập.", "14990000", laptop, an);
        product("MacBook Air M2 13 inch 8GB/256GB", 5, "Chip Apple M2, pin cả ngày, thiết kế mỏng nhẹ 1.24kg.", "22990000", laptop, bao);
        product("Samsung Galaxy A55 5G 8/128GB", 15, "Màn Super AMOLED 120Hz, chống nước IP67, sạc nhanh 25W.", "8490000", phone, linh);
        product("iPhone 13 128GB", 6, "Chip A15 Bionic, camera kép 12MP, hỗ trợ 5G.", "13990000", phone, bao);
        product("Chuột không dây Logitech M331 Silent", 40, "Kết nối 2.4GHz, click êm, thời lượng pin 24 tháng.", "349000", accessory, linh);
        product("Bàn phím cơ AKKO 3068B Plus", 20, "68 phím hotswap, kết nối Bluetooth/USB-C, hộp switch thay được.", "990000", accessory, an);
        product("Củ sạc nhanh Anker 313 Charger 20W", 35, "Sạc nhanh PD 20W, tương thích iPhone và Android.", "349000", accessory, bao);
        product("Màn hình LG 24MP400 24 inch IPS Full HD", 12, "Tần số quét 75Hz, viền mỏng 3 cạnh, cổng HDMI/VGA.", "2690000", monitor, an);
        product("Màn hình Dell S2721QS 27 inch 2K IPS", 6, "Độ phân giải 2560x1440, hỗ trợ AMD FreeSync.", "6990000", monitor, linh);
        product("Tai nghe Bluetooth Sony WH-CH520", 18, "Pin 50 giờ, trọng lượng 147g, đàm thoại rõ nét.", "990000", audio, bao);
        product("Loa Bluetooth JBL Go 3", 25, "Chống nước IP67, âm bass mạnh, pin 5 giờ liên tục.", "590000", audio, linh);
    }
    private User user(String name, String email, String phone) {
        User u = new User(); u.setFullname(name); u.setEmail(email); u.setPhone(phone);
        u.setPassword("!disabled-demo-account"); return users.save(u);
    }
    private Category category(String name, String image, User... members) {
        Category c = new Category(); c.setName(name); c.setImages(image); c.setUsers(new LinkedHashSet<>(Arrays.asList(members))); return categories.save(c);
    }
    private void product(String title, int quantity, String desc, String price, Category category, User user) {
        Product p = new Product(); p.setTitle(title); p.setQuantity(quantity); p.setDesc(desc); p.setPrice(new BigDecimal(price)); p.setCategory(category); p.setUser(user); products.save(p);
    }
}
