package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopcommon.service.SendMailService;
import com.example.greenshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final UserService userService;

    @Value("${site.url}")
    private String siteUrl;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CreateUserRequestDto createUserRequestDto) {
        Optional<User> userFromDB = userRepository.findByEmail(createUserRequestDto.getEmail());
        if (userFromDB.isEmpty()) {
            User user = userMapper.map(createUserRequestDto);
            String password = user.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            user.setRole(Role.CUSTOMER);
            user.setEnabled(false);
            UUID token = UUID.randomUUID();
            user.setToken(token.toString());
            userRepository.save(user);
            sendMailService.sendMail(user.getEmail(), "Welcome Green-Shop ",
                    "Hi " + user.getName() + "\n "
                            + " Please verify your email by clicking on this url: "
                            + siteUrl + "/user/verify?email=" + user.getEmail() + "&token=" + token);
        }
        return "redirect:/";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("email") String email,
                             @RequestParam("token") String token) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            return "redirect:/";
        }
        if (byEmail.get().isEnabled()) {
            return "redirect:/";
        }
        if (byEmail.get().getToken().equals(token)) {
            User user = byEmail.get();
            user.setEnabled(true);
            user.setToken(null);
            userRepository.save(user);
        }
        return "redirect:/";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/password")
    public String passwordReset() {
        return "password";
    }

    @PostMapping("/password-reset")
    public String requestPasswordReset(@RequestParam("email") String email, ModelMap model) {
        boolean success = userService.requestPasswordReset(email);
        if (success) {
            model.addAttribute("message", "A password recovery request has been sent to your email");
            return "reset-request-success";
        } else {
            model.addAttribute("error", "User with provided email not found");
            return "reset-request-failure";
        }
    }

    @GetMapping("/newPassword")
    public String newPassword(@RequestParam("email") String email,
                              @RequestParam("token") String token, ModelMap modelMap) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            modelMap.addAttribute("user", byEmail.get());
        }
        modelMap.addAttribute("token", token);
        return "newPassword";
    }

    @PostMapping("/password-reset/confirm")
    public String confirmPasswordReset(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword, Model model) {
        boolean success = userService.confirmPasswordReset(token, newPassword);
        if (success) {
            model.addAttribute("message", "Password has been reset successfully");
            return "reset-confirmation-success";
        } else {
            model.addAttribute("error", "Invalid or expired token");
            return "reset-confirmation-failure";
        }
    }
}