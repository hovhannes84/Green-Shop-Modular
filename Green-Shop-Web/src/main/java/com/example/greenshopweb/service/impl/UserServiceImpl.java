package com.example.greenshopweb.service.impl;

import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopcommon.service.SendMailService;
import com.example.greenshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final PasswordEncoder passwordEncoder;
    @Value("${site.url}")
    private String siteUrl;


    // Method to request a password reset for a user based on their email
    @Override
    public Boolean requestPasswordReset(@NotBlank @Email String email) {
        try {
            log.info("Password reset requested for email: {}", email);
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                UUID resetToken = UUID.randomUUID();
                user.setToken(resetToken.toString());
                userRepository.save(user);
                sendMailService.sendMail(user.getEmail(), "Change Password Green Shop ",
                        "Hi " + user.getName() + "\n "
                                + " Please click here to change your password. " + "\n url: "
                                + siteUrl + "/user/newPassword?email=" + user.getEmail() + "&token=" + resetToken);
                log.info("Password reset email sent to: {}", user.getEmail());
                return true;
            } else {
                log.warn("Password reset requested for non-existent email: {}", email);
                return false;
            }
        } catch (Exception ex) {
            log.error("Error while requesting password reset for email: {}", email, ex);
            throw new RuntimeException("Error while requesting password reset. Please try again later.", ex);
        }
    }

    // Method to confirm the password reset for a user using the provided token and new password
    @Override
    public Boolean confirmPasswordReset(@NotBlank String token, @NotBlank String newPassword) {
        try {
            log.info("Confirming password reset for token: {}", token);
            Optional<User> userOptional = userRepository.findByToken(token);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setToken(null);
                userRepository.save(user);
                log.info("Password reset confirmed for user: {}", user.getEmail());
                return true;
            } else {
                log.warn("Invalid token for password reset: {}", token);
                return false;
            }
        } catch (Exception ex) {
            log.error("Error while confirming password reset for token: {}", token, ex);
            throw new RuntimeException("Error while confirming password reset. Please try again later.", ex);
        }
    }
}
