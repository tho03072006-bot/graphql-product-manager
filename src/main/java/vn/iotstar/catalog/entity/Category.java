package vn.iotstar.catalog.entity;
import jakarta.persistence.*;
import java.util.*;
import java.math.BigDecimal;

@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(length = 1000)
    private String images;
    @ManyToMany
    @JoinTable(name = "category_users", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new LinkedHashSet<>();
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
