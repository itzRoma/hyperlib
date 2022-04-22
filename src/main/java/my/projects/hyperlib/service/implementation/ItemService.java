package my.projects.hyperlib.service.implementation;

import lombok.AllArgsConstructor;
import my.projects.hyperlib.entity.Category;
import my.projects.hyperlib.entity.Item;
import my.projects.hyperlib.entity.ItemType;
import my.projects.hyperlib.exception.NotFoundException;
import my.projects.hyperlib.repository.ItemRepository;
import my.projects.hyperlib.service.CommonServiceContract;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ItemService implements CommonServiceContract<Item> {
    private final ItemRepository itemRepository;

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

    public List<Item> findByCategory(Category category) {
        return itemRepository.findByCategoryId(category.getId());
    }

    public List<Item> findLastAddedItems(Integer amount, ItemType itemType) {
        return new ArrayList<>(itemRepository.findLastAddedItems(amount, itemType.name()));
    }

    public boolean checkIfCodeIsAvailable(String code) {
        return itemRepository.findByCode(code).isEmpty();
    }
}
