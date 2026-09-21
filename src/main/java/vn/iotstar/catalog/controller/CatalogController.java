package vn.iotstar.catalog.controller;
import vn.iotstar.catalog.dto.Inputs.*;
import vn.iotstar.catalog.dto.Views.*;
import vn.iotstar.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;
import java.util.List;
@Controller
public class CatalogController {
    private final CatalogService service;
    public CatalogController(CatalogService service) { this.service = service; }
    @QueryMapping public List<ProductView> products() { return service.products(); }
    @QueryMapping public ProductView productById(@Argument Long id) { return service.product(id); }
    @QueryMapping public List<ProductView> productsByCategory(@Argument Long categoryId) { return service.productsByCategory(categoryId); }
    @QueryMapping public List<CategoryView> categories() { return service.categories(); }
    @QueryMapping public CategoryView categoryById(@Argument Long id) { return service.category(id); }
    @QueryMapping public List<UserView> users() { return service.users(); }
    @MutationMapping public ProductView createProduct(@Argument @Valid ProductInput input) { return service.createProduct(input); }
    @MutationMapping public ProductView updateProduct(@Argument Long id, @Argument @Valid ProductInput input) { return service.updateProduct(id, input); }
    @MutationMapping public boolean deleteProduct(@Argument Long id) { return service.deleteProduct(id); }
    @MutationMapping public CategoryView createCategory(@Argument @Valid CategoryInput input) { return service.createCategory(input); }
    @MutationMapping public CategoryView updateCategory(@Argument Long id, @Argument @Valid CategoryInput input) { return service.updateCategory(id, input); }
    @MutationMapping public boolean deleteCategory(@Argument Long id) { return service.deleteCategory(id); }
    @SchemaMapping(typeName = "Category", field = "products")
    public List<ProductView> categoryProducts(CategoryView category) { return service.productsByCategory(category.id()); }
    @SchemaMapping(typeName = "User", field = "categories")
    public List<CategoryView> userCategories(UserView user) { return service.categoriesByUser(user.id()); }
}
