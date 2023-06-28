package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopweb.service.CategoryService;
import com.example.greenshopweb.service.ProductService;
import com.example.greenshopweb.service.RatingsreviewService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final RatingsreviewService ratingsreviewService;

    @GetMapping("/{id}")
    public String singleProductPage(@PathVariable("id") int id, ModelMap modelMap) {
        List<RatingsreviewDto> allByProductId = ratingsreviewService.getAllByProductId(id);
        modelMap.addAttribute("reviews",allByProductId);
        modelMap.addAttribute("ratings",ratingsreviewService.calculateProductRating(allByProductId));
        modelMap.addAttribute("product", productService.singleProduct(id));
        return "singleProduct";
    }

    @GetMapping
    public String productPage(ModelMap modelMap,
                              @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("products", ratingsreviewService.allProductsRating());
        modelMap.addAttribute("categories", categoryService.findCategories());
        return "products";
    }

    @GetMapping("/add")
    public String productsAddPage(ModelMap modelMap) {
        modelMap.addAttribute("categories", categoryService.findCategories());
        return "addProducts";
    }

    @PostMapping("/add")
    public String productsAdd(@ModelAttribute CreateProductRequestDto createProductRequestDto,
                              @RequestParam("image") MultipartFile multipartFile,
                              @AuthenticationPrincipal CurrentUser currentUser) throws IOException {
        productService.addProduct(currentUser.getUser(), multipartFile, createProductRequestDto);
        return "redirect:/products";
    }

    @GetMapping("/remove")
    public String removeProduct(@RequestParam("id") int id) {
        productService.deleteById(id);
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String productsUpdate(@ModelAttribute UpdateProductRequestDto updateProductRequestDto,
                                 @RequestParam("image") MultipartFile multipartFile,
                                 @AuthenticationPrincipal CurrentUser currentUser) throws IOException {
        productService.updateProduct(currentUser.getUser(), multipartFile, updateProductRequestDto);
        return "redirect:/products";
    }

}

