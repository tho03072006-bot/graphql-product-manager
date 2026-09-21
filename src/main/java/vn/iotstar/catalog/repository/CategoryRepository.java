package vn.iotstar.catalog.repository;
import vn.iotstar.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoryRepository extends JpaRepository<Category, Long> { List<Category> findByUsersIdOrderByIdAsc(Long userId); }
