package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopweb.service.CartService;
import com.example.greenshopweb.service.CategoryService;
import com.example.greenshopweb.service.ProductService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final CategoryService categoryService;

    // Method to add a product to the cart
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId,
                            @RequestParam("quantity") int quantity,
                            @AuthenticationPrincipal CurrentUser currentUser) throws IOException {
        log.info("Adding product with ID {} to the cart for user: {}", productId, currentUser.getUser().getName());
        cartService.addCart(productId, currentUser.getUser(), quantity);
        return "redirect:/cart";
    }

    // Method to remove a product from the cart
    @GetMapping("/remove")
    public String removeCart(@RequestParam("id") int id) {
        log.info("Removing cart item with ID: {}", id);
        cartService.deleteById(id);
        return "redirect:/cart";
    }

    // Method to update the quantity of a product in the cart
    @PostMapping("/update")
    public String updateToCart(@RequestParam("cartId") int cartId,
                               @RequestParam("quantity") int quantity,
                               @AuthenticationPrincipal CurrentUser currentUser) throws IOException {
        log.info("Updating cart item with ID {} to quantity {} for user: {}", cartId, quantity, currentUser.getUser().getName());
        cartService.updateCartByCurrentUser(currentUser.getUser().getId(), quantity, cartId);
        return "redirect:/cart";
    }

    // Method to display the cart page
    @GetMapping
    public String cartPage(ModelMap modelMap,
                           @AuthenticationPrincipal CurrentUser currentUser) {
        User user = currentUser.getUser();
        log.info("Displaying cart page for user: {}", user.getName());
        List<CartDto> carts = cartService.findCartsByUser(user);
        double subtotal = cartService.calculateCurrentUserCartTotal(currentUser);
        modelMap.addAttribute("carts", carts);
        modelMap.addAttribute("subtotal", subtotal);
        return "shoppingCart";
    }
}
