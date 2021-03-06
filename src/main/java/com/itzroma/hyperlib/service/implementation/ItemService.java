package com.itzroma.hyperlib.service.implementation;

import com.itzroma.hyperlib.entity.Category;
import com.itzroma.hyperlib.entity.Item;
import com.itzroma.hyperlib.entity.ItemType;
import com.itzroma.hyperlib.exception.NotFoundException;
import com.itzroma.hyperlib.repository.ItemRepository;
import com.itzroma.hyperlib.service.CommonServiceContract;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService implements CommonServiceContract<Item> {
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public void save(Item entity) {
        itemRepository.save(entity);
    }

    @Override
    public void delete(Item entity) {
        itemRepository.delete(entity);
    }

    @Override
    public void update(Item source, Item target) {
        BeanUtils.copyProperties(source, target,
                "id", "code", "addingDate", "imageUrl", "itemType");

        if (!source.getCode().equals(target.getCode()))
            target.setCode(source.getCode());

        itemRepository.save(target);
    }

    public Item findByCode(String code) {
        return itemRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException(String.format("Item with code '%s' not found!", code)));
    }

    public List<Item> findByItemType(ItemType itemType) {
        return new ArrayList<>(itemRepository.findByItemType(itemType));
    }

    public List<Item> findByTitlePartially(String title) {
        if (title.equals("all")) return itemRepository.findAll();
        return itemRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Item> findByCategory(Category category) {
        return itemRepository.findByCategoryId(category.getId());
    }

    public List<Item> findByCategories(String[] categoriesNames) {
        if (categoriesNames[0].equals("all")) return findAll();

        List<Item> items = new ArrayList<>();
        Arrays.stream(categoriesNames)
                .map(categoryService::findByName)
                .forEach(category -> items.addAll(findByCategory(category)));
        return items;
    }

    public List<Item> findLastAddedItems(Integer amount, ItemType itemType) {
        return new ArrayList<>(itemRepository.findLastAddedItems(amount, itemType.name()));
    }

    public boolean checkIfCodeIsAvailable(String code) {
        return itemRepository.findByCode(code).isEmpty();
    }

    public boolean isInFavorite(String username, Item item) {
        try {
            return userService.findByUsername(username).getFavorites().contains(item);
        } catch (NotFoundException ex) {
            return false;
        }
    }
}
