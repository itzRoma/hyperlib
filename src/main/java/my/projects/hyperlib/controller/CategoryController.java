package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.Category;
import my.projects.hyperlib.entity.ItemType;
import my.projects.hyperlib.service.implementation.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "category/categories";
    }

    @GetMapping("/create")
    public String showCategoryCreatingForm(@ModelAttribute("newCategory") Category category, Model model) {
        model.addAttribute("itemTypes", ItemType.values());
        return "category/categoryCreatingForm";
    }

    @PostMapping("/create")
    public String createCategory(
            @ModelAttribute("newCategory") @Valid Category newCategory,
            BindingResult bindingResult,
            @RequestParam Map<String, String> form,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "category/categoryCreatingForm";
        }

        Set<String> itemTypes = Stream.of(ItemType.values())
                .map(ItemType::name)
                .collect(Collectors.toSet());

        for (String key : form.keySet()) {
            if (itemTypes.contains(key)) {
                newCategory.getItemTypes().add(ItemType.valueOf(key));
            }
        }

        if (newCategory.getItemTypes().isEmpty()) {
            model.addAttribute("error", "Item types were not specified!");
            model.addAttribute("itemTypes", ItemType.values());
            return "category/categoryCreatingForm";
        }

        categoryService.save(newCategory);

        return "redirect:/categories";
    }
}
