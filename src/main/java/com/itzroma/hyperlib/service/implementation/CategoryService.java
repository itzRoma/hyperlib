package com.itzroma.hyperlib.service.implementation;

import com.itzroma.hyperlib.exception.NotFoundException;
import com.itzroma.hyperlib.repository.CategoryRepository;
import com.itzroma.hyperlib.entity.Category;
import com.itzroma.hyperlib.entity.ItemType;
import com.itzroma.hyperlib.service.CommonServiceContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService implements CommonServiceContract<Category> {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void save(Category entity) {
        categoryRepository.save(entity);
    }

    @Override
    public void delete(Category entity) {
        categoryRepository.delete(entity);
    }

    @Override
    public void update(Category source, Category target) {
        target.setItemTypes(source.getItemTypes());

        if (!source.getName().equals(target.getName()))
            target.setName(source.getName());

        categoryRepository.save(target);
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException(String.format("Category '%s' not found!", name)));
    }

    public List<Category> findByItemType(ItemType itemType) {
        return new ArrayList<>(categoryRepository.findByItemType(itemType.name()));
    }

    public boolean checkIfNameIsAvailable(String name) {
        return categoryRepository.findByName(name).isEmpty();
    }
}
