package com.itzroma.hyperlib.repository;

import com.itzroma.hyperlib.entity.Item;
import com.itzroma.hyperlib.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByCode(String code);

    List<Item> findByItemType(ItemType itemType);

    List<Item> findByTitleContainingIgnoreCase(String title);

    @Query(value = "SELECT * FROM items INNER JOIN items_categories ON items.id = items_categories.item_id WHERE category_id = :category_id", nativeQuery = true)
    List<Item> findByCategoryId(@Param("category_id") Long categoryId);

    @Query(value = "SELECT * FROM items WHERE item_type = :item_type ORDER BY adding_date DESC LIMIT :amount", nativeQuery = true)
    List<Item> findLastAddedItems(@Param("amount") Integer amount, @Param("item_type") String itemType);
}
