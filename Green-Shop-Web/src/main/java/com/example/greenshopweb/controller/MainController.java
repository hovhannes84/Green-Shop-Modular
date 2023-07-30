package com.example.greenshopweb.controller;


import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopweb.service.CategoryService;
import com.example.greenshopweb.service.ProductService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @Value("${green_shop.upload.image.path}")
    private String imageUploadPath;


    // Method to display the main index page
    @GetMapping("/")
    public String main() {
        log.info("Displaying the main index page");
        return "index";
    }

    // Method to display the custom login page
    @GetMapping("/customLogin")
    public String customLogin() {
        log.info("Displaying the custom login page");
        return "customLoginPage";
    }

    // Method to handle custom success login and redirect based on user role
    @GetMapping("/customSuccessLogin")
    public String customSuccessLogin(@AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Handling custom success login");
        if (currentUser != null && currentUser.getUser() != null) {
            User user = currentUser.getUser();
            if (user.getRole() == Role.ADMIN) {
                log.info("User with role ADMIN logged in");
                return "redirect:/user/admin";
            } else if (user.getRole() == Role.CUSTOMER) {
                log.info("User with role CUSTOMER logged in");
                return "redirect:/";
            }
        }
        log.warn("No user or role found, redirecting to main page");
        return "redirect:/";
    }

    // Method to get the user's profile picture as a byte array
    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("profilePic") String profilePic) throws IOException {
        File file = new File(imageUploadPath + profilePic);
        if (file.exists()) {
            log.info("Loading profile picture: {}", profilePic);
            FileInputStream fis = new FileInputStream(file);
            return IOUtils.toByteArray(fis);
        }
        log.warn("Profile picture not found: {}", profilePic);
        return null;
    }
}
