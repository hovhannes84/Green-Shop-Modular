package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthResponseDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshoprest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;


    @PostMapping("/auth")
    public ResponseEntity<UserAuthResponseDto> auth(@RequestBody UserAuthRequestDto userAuthRequestDto) {
        return ResponseEntity.ok(userService.auth(userAuthRequestDto).getBody());
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody CreateUserRequestDto createUserRequestDto) {

        return ResponseEntity.ok(userService.register(createUserRequestDto).getBody());
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("email") String email,
                                             @RequestParam("token") String token) {
        return userService.verifyUser(email,token);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
        boolean success = userService.requestPasswordReset(email);
        if (success) {
            return ResponseEntity.ok("Password reset request has been sent");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with provided email not found");        }
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestParam("token") String token,
                                                       @RequestParam("password") String newPassword) {
        boolean success = userService.confirmPasswordReset(token, newPassword);
        if (success) {
            return ResponseEntity.ok("Password has been reset successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
    }
}