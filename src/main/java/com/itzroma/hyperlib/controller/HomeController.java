package com.itzroma.hyperlib.controller;

import lombok.AllArgsConstructor;
import com.itzroma.hyperlib.entity.ItemType;
import com.itzroma.hyperlib.service.implementation.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {
    private final ItemService itemService;

    private final static Integer AMOUNT_OF_ITEMS_FOR_HOME_PAGE = 4;

    @GetMapping
    public String findLastAddedItemsForHomePage(Model model) {
        model.addAttribute("books", itemService.findLastAddedItems(AMOUNT_OF_ITEMS_FOR_HOME_PAGE, ItemType.BOOK));
        model.addAttribute("movies", itemService.findLastAddedItems(AMOUNT_OF_ITEMS_FOR_HOME_PAGE, ItemType.MOVIE));
        model.addAttribute("songs", itemService.findLastAddedItems(AMOUNT_OF_ITEMS_FOR_HOME_PAGE, ItemType.SONG));
        return "home";
    }
}
