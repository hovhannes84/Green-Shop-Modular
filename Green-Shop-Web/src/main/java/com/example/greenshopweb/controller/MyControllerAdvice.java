package com.example.greenshopweb.controller;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopweb.service.CartService;
import com.example.greenshopweb.service.CategoryService;
import com.example.greenshopweb.service.RatingsreviewService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class MyControllerAdvice {

    private final CartService cartService;
    private final CategoryService categoryService;
    private final RatingsreviewService ratingsreviewService;

    // Method to add "currentUser" attribute to the model
    @ModelAttribute("currentUser")
    public User currentUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            return currentUser.getUser();
        }
        return null;
    }

    // Method to add "carts" attribute to the model
    @ModelAttribute("carts")
    public List<CartDto> cartUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            User user = currentUser.getUser();
            List<CartDto> carts = cartService.findCartsByUser(user);
            if (carts != null) {
                log.debug("Found {} carts for user: {}", carts.size(), user.getName());
                return carts;
            }
        }
        log.debug("No carts found for the current user.");
        return null;
    }

    // Method to add "subtotal" attribute to the model
    @ModelAttribute("subtotal")
    public Double subtotal(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            Double subtotal = cartService.calculateCurrentUserCartTotal(currentUser);
            log.debug("Calculated subtotal for user {}: {}", currentUser.getUser().getName(), subtotal);
            return subtotal;
        }
        log.debug("No current user found, returning subtotal as null.");
        return null;
    }

    // Method to add "categories" attribute to the model
    @ModelAttribute("categories")
    public List<CategoryDto> allCategories() {
        List<CategoryDto> categories = categoryService.findCategories();
        log.debug("Fetching all categories: Found {} categories.", categories.size());
        return categories;
    }

    // Method to add "products" attribute to the model
    @ModelAttribute("products")
    public List<ProductDto> allProducts() {
        List<ProductDto> products = ratingsreviewService.allProductsRating();
        log.debug("Fetching all products with ratings: Found {} products.", products.size());
        return products;
    }
}
