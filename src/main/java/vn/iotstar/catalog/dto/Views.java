package vn.iotstar.catalog.dto;
import java.math.BigDecimal;
import java.util.List;
public final class Views {
    private Views() {}
    public record UserView(Long id, String fullname, String email, String phone) {}
    public record CategoryView(Long id, String name, String images, List<UserView> users) {}
    public record ProductView(Long id, String title, Integer quantity, String desc, BigDecimal price,
                              Long userid, UserView user, CategoryView category) {}
}
