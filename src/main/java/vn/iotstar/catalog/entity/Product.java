package vn.iotstar.catalog.entity;
import jakarta.persistence.*;
import java.util.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "description", length = 2000)
    private String desc;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(optional = false) @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
