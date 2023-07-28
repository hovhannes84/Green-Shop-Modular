package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopweb.service.CategoryService;
import com.example.greenshopweb.service.ProductService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // Method to display the category page
    @GetMapping
    public String categoryPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Displaying the category page");
        modelMap.addAttribute("categories", categoryService.findCategories());
        return "categories";
    }

    // Method to display the add category page
    @GetMapping("/add")
    public String categoriesAddPage() {
        log.info("Displaying the add category page");
        return "addCategories";
    }

    // Method to handle adding a new category
    @PostMapping("/add")
    public String categoriesAdd(@ModelAttribute CreateCategoryRequestDto createCategoryRequestDto) throws IOException {
        log.info("Adding a new category: {}", createCategoryRequestDto.getName());
        categoryService.addCategory(createCategoryRequestDto);
        return "redirect:/categories";
    }

    // Method to remove a category by its ID
    @GetMapping("/remove")
    public String removeCategory(@RequestParam("id") int id) {
        log.info("Removing category with ID: {}", id);
        categoryService.deleteById(id);
        return "redirect:/admin";
    }

    // Method to display a single category page
    @GetMapping("/{id}")
    public String singleCategoryPagePost(@PathVariable("id") int id, ModelMap modelMap) {
        log.info("Displaying single category page for category with ID: {}", id);
        modelMap.addAttribute("category", categoryService.singleCategoryPage(id));
        modelMap.addAttribute("products", productService.findProducts());
        return "singleCategory";
    }
}