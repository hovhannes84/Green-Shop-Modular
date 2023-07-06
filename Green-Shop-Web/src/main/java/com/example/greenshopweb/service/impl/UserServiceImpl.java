package com.example.greenshopweb.service.impl;

import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopcommon.service.SendMailService;
import com.example.greenshopweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl  implements UserService {

    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final PasswordEncoder passwordEncoder;
    @Value("${site.url}")
    private String siteUrl;

    @Override
    public Boolean requestPasswordReset(String email) {
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
            return true;
        }
        return false;
    }

    @Override
    public Boolean confirmPasswordReset(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
