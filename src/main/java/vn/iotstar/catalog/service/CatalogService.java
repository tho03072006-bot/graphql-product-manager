package vn.iotstar.catalog.service;
import vn.iotstar.catalog.entity.*;
import vn.iotstar.catalog.repository.*;
import vn.iotstar.catalog.dto.Inputs.*;
import vn.iotstar.catalog.dto.Views.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final UserRepository users;
    public CatalogService(ProductRepository products, CategoryRepository categories, UserRepository users) {
        this.products = products; this.categories = categories; this.users = users;
    }
    private Product requireProduct(Long id) { return products.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy sản phẩm #" + id)); }
    private Category requireCategory(Long id) { return categories.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy danh mục #" + id)); }
    private User requireUser(Long id) { return users.findById(id).orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng #" + id)); }
    private UserView userView(User u) { return new UserView(u.getId(), u.getFullname(), u.getEmail(), u.getPhone()); }
    private CategoryView categoryView(Category c) {
        return new CategoryView(c.getId(), c.getName(), c.getImages(), c.getUsers().stream().sorted(Comparator.comparing(User::getId)).map(this::userView).toList());
    }
    private ProductView productView(Product p) {
        return new ProductView(p.getId(), p.getTitle(), p.getQuantity(), p.getDesc(), p.getPrice(), p.getUser().getId(), userView(p.getUser()), categoryView(p.getCategory()));
    }
    public List<ProductView> products() { return products.findAllByOrderByPriceAscIdAsc().stream().map(this::productView).toList(); }
    public ProductView product(Long id) { return productView(requireProduct(id)); }
    public List<ProductView> productsByCategory(Long id) {
        requireCategory(id);
        return products.findByCategoryIdOrderByPriceAscIdAsc(id).stream().map(this::productView).toList();
    }
    public List<CategoryView> categories() { return categories.findAll(Sort.by("id")).stream().map(this::categoryView).toList(); }
    public CategoryView category(Long id) { return categoryView(requireCategory(id)); }
    public List<UserView> users() { return users.findAll(Sort.by("id")).stream().map(this::userView).toList(); }
    public List<CategoryView> categoriesByUser(Long id) { return categories.findByUsersIdOrderByIdAsc(id).stream().map(this::categoryView).toList(); }
    @Transactional
    public ProductView createProduct(ProductInput input) { return saveProduct(new Product(), input); }
    @Transactional
    public ProductView updateProduct(Long id, ProductInput input) { return saveProduct(requireProduct(id), input); }
    private ProductView saveProduct(Product p, ProductInput i) {
        // Resolve references before writing; a failed mutation rolls back as one transaction.
        User user = requireUser(i.userId()); Category category = requireCategory(i.categoryId());
        p.setTitle(i.title().trim()); p.setQuantity(i.quantity()); p.setDesc(i.desc()); p.setPrice(i.price());
        p.setUser(user); p.setCategory(category);
        return productView(products.saveAndFlush(p));
    }
    @Transactional
    public boolean deleteProduct(Long id) { products.delete(requireProduct(id)); products.flush(); return true; }
    @Transactional
    public CategoryView createCategory(CategoryInput input) { return saveCategory(new Category(), input); }
    @Transactional
    public CategoryView updateCategory(Long id, CategoryInput input) { return saveCategory(requireCategory(id), input); }
    private CategoryView saveCategory(Category c, CategoryInput i) {
        Set<User> members = new LinkedHashSet<>();
        for (Long userId : i.userIds()) members.add(requireUser(userId));
        c.setName(i.name().trim()); c.setImages(i.images()); c.setUsers(members);
        return categoryView(categories.saveAndFlush(c));
    }
    @Transactional
    public boolean deleteCategory(Long id) {
        Category category = requireCategory(id);
        if (products.existsByCategoryId(id)) throw new IllegalArgumentException("Danh mục còn sản phẩm. Hãy chuyển hoặc xóa sản phẩm trước.");
        // Only remove join rows; never cascade deletion to users.
        category.getUsers().clear(); categories.delete(category); categories.flush(); return true;
    }
}
