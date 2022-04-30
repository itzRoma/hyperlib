package com.itzroma.hyperlib.repository;

import com.itzroma.hyperlib.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Override
    @Query(value = "SELECT * FROM categories ORDER BY id", nativeQuery = true)
    List<Category> findAll();

    @Query(value = "SELECT * FROM categories INNER JOIN categories_item_types ON categories.id = categories_item_types.category_id WHERE categories_item_types.item_types = :item_type", nativeQuery = true)
    List<Category> findByItemType(@Param("item_type") String itemType);
}
