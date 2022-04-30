package com.itzroma.hyperlib.controller;

import lombok.AllArgsConstructor;
import com.itzroma.hyperlib.entity.Item;
import com.itzroma.hyperlib.entity.ItemType;
import com.itzroma.hyperlib.service.implementation.CategoryService;
import com.itzroma.hyperlib.service.implementation.ItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/catalog")
public class CatalogController {
    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping
    public String findAllItems(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "item/catalog";
    }

    @GetMapping("/create")
    public String showItemCreationForm(
            @RequestParam("itemType") String itemType,
            Model model
    ) {
        Item createdItem = new Item();
        createdItem.setItemType(ItemType.valueOf(itemType.toUpperCase()));

        model.addAttribute("createdItem", createdItem);
        model.addAttribute("categories", categoryService.findByItemType(createdItem.getItemType()));
        return "item/itemCreationForm";
    }

    @PostMapping("/create")
    public String createItem(
            @ModelAttribute("createdItem") @Valid Item createdItem,
            BindingResult bindingResult,
            Model model
    ) {
        createdItem.setCode(createdItem.getCode().trim());
        createdItem.setTitle(createdItem.getTitle().trim());
        createdItem.setAuthor(createdItem.getAuthor().trim());
        createdItem.setDescription(createdItem.getDescription().trim());

        if (!itemService.checkIfCodeIsAvailable(createdItem.getCode())) {
            model.addAttribute("codeError", String.format("Code '%s' is already taken!", createdItem.getCode()));
            model.addAttribute("categories", categoryService.findByItemType(createdItem.getItemType()));
            return "item/itemCreationForm";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findByItemType(createdItem.getItemType()));
            return "item/itemCreationForm";
        }

        itemService.save(createdItem);
        return "redirect:/catalog";
    }

    @GetMapping("/item/{code}/edit")
    public String showItemEditForm(@PathVariable String code, Model model) {
        Item editedItem = itemService.findByCode(code);

        model.addAttribute("editedItem", editedItem);
        model.addAttribute("categories", categoryService.findByItemType(editedItem.getItemType()));
        return "item/itemEditForm";
    }

    @PostMapping("/item/{code}/edit")
    public String editItem(
            @PathVariable String code,
            @ModelAttribute("editedItem") @Valid Item editedItem,
            BindingResult bindingResult,
            Model model
    ) {
        editedItem.setCode(editedItem.getCode().trim());
        editedItem.setTitle(editedItem.getTitle().trim());
        editedItem.setAuthor(editedItem.getAuthor().trim());
        editedItem.setDescription(editedItem.getDescription().trim());

        if (!code.equals(editedItem.getCode()) && !itemService.checkIfCodeIsAvailable(editedItem.getCode())) {
            model.addAttribute("editedItem", itemService.findByCode(code));
            model.addAttribute("categories", categoryService.findByItemType(editedItem.getItemType()));
            model.addAttribute("codeError", String.format("Code '%s' is already taken!", editedItem.getCode()));
            return "item/itemEditForm";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findByItemType(editedItem.getItemType()));
            return "item/itemEditForm";
        }

        Item itemToEdit = itemService.findByCode(code);
        itemService.update(editedItem, itemToEdit);
        return "redirect:/catalog";
    }

    @PostMapping("/item/{code}/delete")
    public String deleteItem(@PathVariable String code) {
        itemService.delete(itemService.findByCode(code));
        return "redirect:/catalog";
    }
}
