package com.itzroma.hyperlib.controller;

import lombok.AllArgsConstructor;
import com.itzroma.hyperlib.entity.ItemType;
import com.itzroma.hyperlib.exception.NotFoundException;
import com.itzroma.hyperlib.service.implementation.CategoryService;
import com.itzroma.hyperlib.service.implementation.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/catalog")
public class ItemController {
    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping("/{itemType}")
    public String findAllItems(@PathVariable String itemType, Model model) {
        switch (itemType) {
            case "books" -> {
                findAllByItemType(model, ItemType.BOOK);
                return "item/items";
            }
            case "movies" -> {
                findAllByItemType(model, ItemType.MOVIE);
                return "item/items";
            }
            case "songs" -> {
                findAllByItemType(model, ItemType.SONG);
                return "item/items";
            }
            default -> throw new NotFoundException("Page not found");
        }
    }

    @GetMapping("/item/{code}")
    public String findOneItem(@PathVariable String code, Model model) {
        model.addAttribute("item", itemService.findByCode(code));
        return "item/item";
    }

    private void findAllByItemType(Model model, ItemType itemType) {
        model.addAttribute("itemType", itemType.name().toLowerCase().concat("s"));
        model.addAttribute("items", itemService.findByItemType(itemType));
        model.addAttribute("categories", categoryService.findByItemType(itemType));
    }
}
