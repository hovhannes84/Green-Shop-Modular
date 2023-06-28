package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopweb.service.CartService;
import com.example.greenshopweb.service.CategoryService;
import com.example.greenshopweb.service.RatingsreviewService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class MyControllerAdvice {

    private final CartService cartService;
    private final CategoryService categoryService;
    private final RatingsreviewService ratingsreviewService;


    @ModelAttribute("currentUser")
    public User currentUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            return currentUser.getUser();
        }
        return null;
    }

    @ModelAttribute("carts")
    public List<CartDto> cartUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            User user = currentUser.getUser();
            List<CartDto> carts = cartService.findCartsByUser(user);
            if (carts != null) {
                return carts;
            }
        }
        return null;
    }

    @ModelAttribute("subtotal")
    public Double subtotal(@AuthenticationPrincipal CurrentUser currentUser) {
        return (currentUser != null) ? cartService.calculateCurrentUserCartTotal(currentUser) : null;
    }

    @ModelAttribute("categories")
    public List<CategoryDto> allCategories() {
        return categoryService.findCategories();
    }

//    @ModelAttribute("products")
//    public List<ProductDto> allProducts() {
//        return ratingsreviewService.allProductsRating();
//    }

}
