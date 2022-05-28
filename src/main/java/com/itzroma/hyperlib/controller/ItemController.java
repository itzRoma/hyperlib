package com.itzroma.hyperlib.controller;

import com.itzroma.hyperlib.entity.Item;
import com.itzroma.hyperlib.entity.ItemType;
import com.itzroma.hyperlib.entity.User;
import com.itzroma.hyperlib.service.implementation.CategoryService;
import com.itzroma.hyperlib.service.implementation.ItemService;
import com.itzroma.hyperlib.service.implementation.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping("/catalog")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/{itemType}")
    public String findAllItems(
            @PathVariable String itemType,
            @RequestParam(name = "title", required = false, defaultValue = "all") String title,
            @RequestParam(name = "category", required = false, defaultValue = "all") String[] categories,
            Model model
    ) {
        findAll(
                model,
                ItemType.valueOf(itemType.substring(0, itemType.length() - 1).toUpperCase()),
                categories,
                title
        );
        model.addAttribute("itemService", itemService);
        return "item/items";
    }

    @GetMapping("/item/{code}")
    public String findOneItem(@PathVariable String code, Model model, Principal principal) {
        Item item = itemService.findByCode(code);

        boolean isInFavorites = false;
        if (principal != null) {
            isInFavorites = itemService.isInFavorite(principal.getName(), item);
        }

        model.addAttribute("item", item);
        model.addAttribute("isInFavorites", isInFavorites);
        return "item/item";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/item/{code}/addToFavorites")
    public String addToFavorites(
            @PathVariable String code,
            @RequestParam(name = "calledFrom", defaultValue = "catalog") String calledFrom,
            Principal principal
    ) {
        Item item = itemService.findByCode(code);
        User currentUser = userService.findByUsername(principal.getName());
        userService.addToFavorites(currentUser, item);

        if (calledFrom.equals("home")) return "redirect:/";
        if (calledFrom.equals("item")) return "redirect:/catalog/item/%s".formatted(code);
        return "redirect:/catalog/%s".formatted(item.getItemType().name().concat("s").toLowerCase());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/item/{code}/removeFromFavorites")
    public String removeFromFavorites(
            @PathVariable String code,
            @RequestParam(name = "calledFrom", defaultValue = "catalog") String calledFrom,
            Principal principal
    ) {
        Item item = itemService.findByCode(code);
        User currentUser = userService.findByUsername(principal.getName());
        userService.removeFromFavorites(currentUser, item);

        if (calledFrom.equals("home")) return "redirect:/";
        if (calledFrom.equals("item")) return "redirect:/catalog/item/%s".formatted(code);
        if (calledFrom.equals("profile")) return "redirect:/users/%s".formatted(principal.getName());
        return "redirect:/catalog/%s".formatted(item.getItemType().name().concat("s").toLowerCase());
    }

    private void findAll(Model model, ItemType itemType, String[] categories, String title) {
        List<Item> items = itemService.findByItemType(itemType).stream()
                .distinct()
                .filter(itemService.findByCategories(categories)::contains)
                .filter(itemService.findByTitlePartially(title)::contains)
                .collect(Collectors.toList());

        model.addAttribute("items", items);
        model.addAttribute("categories", categoryService.findByItemType(itemType));
        model.addAttribute("itemType", itemType.name().concat("s").toLowerCase());
    }
}
