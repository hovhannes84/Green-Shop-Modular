package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthResponseDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopcommon.service.SendMailService;
import com.example.greenshoprest.service.UserService;
import com.example.greenshoprest.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil tokenUtil;
    private final UserMapper userMapper;
    private final SendMailService sendMailService;
    @Value("${site.url}")
    private String siteUrl;

    @Override
    public ResponseEntity<UserAuthResponseDto> auth(UserAuthRequestDto userAuthRequestDto) {
        Optional<User> byEmail = userRepository.findByEmail(userAuthRequestDto.getEmail());
        if (byEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = byEmail.get();
        if (!passwordEncoder.matches(userAuthRequestDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = tokenUtil.generateToken(userAuthRequestDto.getEmail());
        UserAuthResponseDto userAuthResponseDto = new UserAuthResponseDto(token);

        return ResponseEntity.ok(userAuthResponseDto);
    }

    @Override
    public ResponseEntity<UserDto> register(@Valid CreateUserRequestDto createUserRequestDto) {
        Optional<User> byEmail = userRepository.findByEmail(createUserRequestDto.getEmail());
        if (byEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User user = userMapper.map(createUserRequestDto);
        user.setPassword(passwordEncoder.encode(createUserRequestDto.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(false);
        UUID token = UUID.randomUUID();
        user.setToken(token.toString());
        userRepository.save(user);
        sendMailService.sendMail(user.getEmail(), "Welcome Green-Shop ",
                "Hi " + user.getName() + "\n "
                        + " Please verify your email by clicking on this url: "
                        + siteUrl + "/user/verify?email=" + user.getEmail() + "&token=" + token);
        return ResponseEntity.ok(userMapper.mapToDto(user));
    }

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
                            + siteUrl + "/user/password-reset/confirm?email=" + user.getEmail() + "&token=" + resetToken);
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

    public ResponseEntity<?> verifyUser(String email, String token) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email");
        }
        if (byEmail.get().isEnabled()) {
            return ResponseEntity.badRequest().body("User already verified");
        }
        if (byEmail.get().getToken().equals(token)) {
            User user = byEmail.get();
            user.setEnabled(true);
            user.setToken(null);
            userRepository.save(user);
            return ResponseEntity.ok("User verified");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}
