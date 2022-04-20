package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.Category;
import my.projects.hyperlib.entity.ItemType;
import my.projects.hyperlib.exception.NotFoundException;
import my.projects.hyperlib.service.implementation.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

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

    @GetMapping("/{name}/edit")
    public String showCategoryEditForm(@PathVariable String name, Model model) {
        model.addAttribute("editedCategory", categoryService.findByName(name));
        model.addAttribute("itemTypes", ItemType.values());
        return "category/categoryEditForm";
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
    public String deleteCategory(@PathVariable String name) {
        categoryService.delete(categoryService.findByName(name));
        return "redirect:/categories";
    }
}
