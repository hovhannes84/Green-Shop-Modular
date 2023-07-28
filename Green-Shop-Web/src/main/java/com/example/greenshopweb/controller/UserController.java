package com.example.greenshopweb.controller;

import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopcommon.service.SendMailService;
import com.example.greenshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final UserService userService;

    @Value("${site.url}")
    private String siteUrl;

    // Method to show the registration page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("createUserRequestDto", new CreateUserRequestDto());
        return "register";
    }

    // Method to handle user registration
    @PostMapping("/register")
    public String register(@ModelAttribute("createUserRequestDto") @Validated CreateUserRequestDto createUserRequestDto,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.error("Validation errors occurred during user registration.");
            return "register";
        }
        try {
            Optional<User> userFromDB = userRepository.findByEmail(createUserRequestDto.getEmail());
            if (userFromDB.isPresent()) {
                log.error("User with email address already exists: " + createUserRequestDto.getEmail());
                model.addAttribute("error", "Email address already in use");
                return "register";
            }
            User user = userMapper.map(createUserRequestDto);
            String password = user.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            user.setRole(Role.CUSTOMER);
            user.setEnabled(false);
            UUID token = UUID.randomUUID();
            user.setToken(token.toString());
            userRepository.save(user);

            sendMailService.sendMail(user.getEmail(), "Welcome Green-Shop",
                    "Hi " + user.getName() + "\n "
                            + " Please verify your email by clicking on this url: "
                            + siteUrl + "/user/verify?email=" + user.getEmail() + "&token=" + token);
        } catch (Exception e) {
            log.error("An error occurred during user registration: " + e.getMessage());
            model.addAttribute("error", "An error occurred during user registration");
            return "register";
        }
        return "redirect:/";
    }

    // Method to handle user verification
    @GetMapping("/verify")
    public String verifyUser(@RequestParam("email") String email,
                             @RequestParam("token") String token) {
        try {
            Optional<User> byEmail = userRepository.findByEmail(email);
            if (byEmail.isEmpty()) {
                log.error("User not found for email: " + email);
                return "redirect:/";
            }
            User user = byEmail.get();

            if (user.isEnabled()) {
                log.error("User already verified: " + email);
                return "redirect:/";
            }
            if (!user.getToken().equals(token)) {
                log.error("Invalid verification token for email: " + email);
                return "redirect:/";
            }
            user.setEnabled(true);
            user.setToken(null);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("An error occurred during user verification: " + e.getMessage());
        }
        return "redirect:/";
    }

    // Method to show the admin page
    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    // Method to show the password reset page
    @GetMapping("/password")
    public String passwordReset() {
        return "password";
    }

    // Method to handle the password reset request
    @PostMapping("/password-reset")
    public String requestPasswordReset(@RequestParam("email") @Valid String email, BindingResult bindingResult, ModelMap model) {
        if (bindingResult.hasErrors()) {
            log.error("Validation errors occurred during password reset request.");
            return "password";
        }
        try {
            boolean success = userService.requestPasswordReset(email);
            if (success) {
                model.addAttribute("message", "A password recovery request has been sent to your email");
                return "reset-request-success";
            } else {
                model.addAttribute("error", "User with provided email not found");
                return "reset-request-failure";
            }
        } catch (Exception e) {
            log.error("An error occurred during password reset request: " + e.getMessage());
            model.addAttribute("error", "An error occurred during password reset request");
            return "reset-request-failure";
        }
    }

    // Method to show the new password page
    @GetMapping("/newPassword")
    public String newPassword(@RequestParam("email") String email,
                              @RequestParam("token") String token, ModelMap modelMap) {
        try {
            Optional<User> byEmail = userRepository.findByEmail(email);
            byEmail.ifPresent(user -> modelMap.addAttribute("user", user));
            modelMap.addAttribute("token", token);
            return "newPassword";
        } catch (Exception e) {
            log.error("An error occurred while processing the new password page: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Method to handle the password reset confirmation
    @PostMapping("/password-reset/confirm")
    public String confirmPasswordReset(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword, Model model) {
        try {
            boolean success = userService.confirmPasswordReset(token, newPassword);
            if (success) {
                model.addAttribute("message", "Password has been reset successfully");
                return "reset-confirmation-success";
            } else {
                model.addAttribute("error", "Invalid or expired token");
                return "reset-confirmation-failure";
            }
        } catch (Exception e) {
            log.error("An error occurred during password reset confirmation: " + e.getMessage());
            return "reset-confirmation-failure";
        }
    }
}