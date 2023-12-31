package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthResponseDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshoprest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserEndpoint {

    private final UserService userService;


    @PostMapping("/auth")
    public ResponseEntity<UserAuthResponseDto> auth(@Valid @RequestBody UserAuthRequestDto userAuthRequestDto) {
        log.info("User authentication request received");
        return ResponseEntity.ok(userService.auth(userAuthRequestDto).getBody());
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        log.info("User registration request received");
        return ResponseEntity.ok(userService.register(createUserRequestDto).getBody());
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("email") @Email(message = "Invalid email format") String email,
                                        @RequestParam("token") @NotBlank(message = "Token must not be blank") String token) {
        log.info("Verification request received for email: {}", email);
        return userService.verifyUser(email, token);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") @Email(message = "Invalid email format") String email) {
        log.info("Password reset request received for email: {}", email);
        boolean success = userService.requestPasswordReset(email);
        if (success) {
            return ResponseEntity.ok("Password reset request has been sent");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with provided email not found");
        }
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestParam("token") @NotBlank(message = "Token must not be blank") String token,
                                                       @RequestParam("password") @Size(min = 6, message = "Password must be at least 6 characters long") String newPassword) {
        log.info("Password reset confirmation request received");
        boolean success = userService.confirmPasswordReset(token, newPassword);
        if (success) {
            return ResponseEntity.ok("Password has been reset successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
    }
}