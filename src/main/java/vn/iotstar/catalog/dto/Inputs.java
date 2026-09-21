package vn.iotstar.catalog.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
public final class Inputs {
    private Inputs() {}
    public record ProductInput(
        @NotBlank(message = "Tên sản phẩm không được để trống") @Size(max = 200) String title,
        @NotNull @Min(value = 0, message = "Số lượng phải không âm") Integer quantity,
        @Size(max = 2000) String desc,
        @NotNull @DecimalMin(value = "0", message = "Giá phải không âm") @Digits(integer = 13, fraction = 2) BigDecimal price,
        @NotNull Long userId, @NotNull Long categoryId) {}
    public record CategoryInput(
        @NotBlank(message = "Tên danh mục không được để trống") @Size(max = 120) String name,
        @Size(max = 1000) String images, @NotNull List<@NotNull Long> userIds) {}
}
