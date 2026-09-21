package vn.iotstar.catalog.repository;
import vn.iotstar.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> { List<Product> findAllByOrderByPriceAscIdAsc();
    List<Product> findByCategoryIdOrderByPriceAscIdAsc(Long categoryId);
    boolean existsByCategoryId(Long categoryId); }
