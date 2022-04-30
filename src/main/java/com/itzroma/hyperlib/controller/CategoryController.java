package com.itzroma.hyperlib.controller;

import lombok.AllArgsConstructor;
import com.itzroma.hyperlib.entity.Category;
import com.itzroma.hyperlib.entity.ItemType;
import com.itzroma.hyperlib.exception.NotFoundException;
import com.itzroma.hyperlib.service.implementation.CategoryService;
import com.itzroma.hyperlib.service.implementation.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final ItemService itemService;

    @GetMapping
    public String findAllCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "category/categories";
    }

    @GetMapping("/create")
    public String showCategoryCreationForm(@ModelAttribute("createdCategory") Category createdCategory, Model model) {
        model.addAttribute("itemTypes", ItemType.values());
        return "category/categoryCreationForm";
    }

    @PostMapping("/create")
    public String createCategory(
            @ModelAttribute("createdCategory") @Valid Category createdCategory,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("itemTypes", ItemType.values());
            return "category/categoryCreationForm";
        }

        createdCategory.setName(createdCategory.getName().trim());

        try {
            categoryService.findByName(createdCategory.getName());
            model.addAttribute("nameError", String.format("Category '%s' is already exists!", createdCategory.getName()));
            model.addAttribute("itemTypes", ItemType.values());
            return "category/categoryCreationForm";
        } catch (NotFoundException ex) {
            categoryService.save(createdCategory);
            return "redirect:/categories";
        }
    }

    @GetMapping("/{name}/edit")
    public String showCategoryEditForm(@PathVariable String name, Model model) {
        Category editedCategory = categoryService.findByName(name);
        int itemsWithThisCategory = itemService.findByCategory(editedCategory).size();

        if (itemsWithThisCategory > 0) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorMessage", String.format("Cannot edit the category '%s' (%d items use it)!",
                    editedCategory.getName(), itemsWithThisCategory
            ));
            return "category/categories";
        }

        model.addAttribute("editedCategory", editedCategory);
        model.addAttribute("itemTypes", ItemType.values());
        return "category/categoryEditForm";
    }

    @PostMapping("/{name}/edit")
    public String editCategory(
            @PathVariable String name,
            @ModelAttribute("editedCategory") @Valid Category editedCategory,
            BindingResult bindingResult,
            Model model
    ) {
        editedCategory.setName(editedCategory.getName().trim());

        if (!name.equals(editedCategory.getName()) && !categoryService.checkIfNameIsAvailable(editedCategory.getName())) {
            model.addAttribute("editedCategory", categoryService.findByName(name));
            model.addAttribute("itemTypes", ItemType.values());
            model.addAttribute("nameError", String.format("Category '%s' is already exists!", editedCategory.getName()));
            return "category/categoryEditForm";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("itemTypes", ItemType.values());
            return "category/categoryEditForm";
        }

        Category categoryToEdit = categoryService.findByName(name);
        categoryService.update(editedCategory, categoryToEdit);
        return "redirect:/categories";
    }

    @PostMapping("/{name}/delete")
    public String deleteCategory(@PathVariable String name, Model model) {
        Category category = categoryService.findByName(name);
        int itemsWithThisCategory = itemService.findByCategory(category).size();

        if (itemsWithThisCategory > 0) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorMessage", String.format("Cannot delete the category '%s' (%d items use it)!",
                    category.getName(), itemsWithThisCategory
            ));
            return "category/categories";
        }

        categoryService.delete(category);
        return "redirect:/categories";
    }
}
