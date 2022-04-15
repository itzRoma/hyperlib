package my.projects.hyperlib.repository;

import my.projects.hyperlib.entity.Category;
import my.projects.hyperlib.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query(value = "SELECT * FROM categories INNER JOIN categories_item_types ON categories.id = categories_item_types.category_id WHERE categories_item_types.item_types = :item_type", nativeQuery = true)
    Collection<Category> findByItemType(@Param("item_type") ItemType itemType);
}
