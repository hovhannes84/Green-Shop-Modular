package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopweb.service.CartService;
import com.example.greenshopweb.service.CategoryService;
import com.example.greenshopweb.service.ProductService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
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
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId,
                            @RequestParam("quantity") int quantity,
                            @AuthenticationPrincipal CurrentUser currentUser) throws IOException {
        cartService.addCart(productId, currentUser.getUser(), quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove")
    public String removeCart(@RequestParam("id") int id) {
        cartService.deleteById(id);
        return "redirect:/cart";
    }

    @PostMapping("/update")

    public String updateToCart(@RequestParam("cartId") int cartId,
                               @RequestParam("quantity") int quantity,
                               @AuthenticationPrincipal CurrentUser currentUser) throws IOException {
        cartService.updateCartByCurrentUser(currentUser.getUser().getId(), quantity,cartId);
        return "redirect:/cart";
    }

    @GetMapping
    public String cartPage(ModelMap modelMap,
                           @AuthenticationPrincipal CurrentUser currentUser) {
        User user = currentUser.getUser();
        List<CartDto> carts = cartService.findCartsByUser(user);
        double subtotal = cartService.calculateCurrentUserCartTotal(currentUser);
        modelMap.addAttribute("carts", carts);
        modelMap.addAttribute("subtotal", subtotal);

        return "shoppingCart";
    }
}