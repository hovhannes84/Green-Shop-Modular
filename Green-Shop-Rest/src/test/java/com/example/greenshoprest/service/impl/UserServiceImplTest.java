package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthResponseDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopcommon.service.SendMailService;
import com.example.greenshoprest.service.UserService;
import com.example.greenshoprest.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserServiceImplTest {


    @Autowired
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SendMailService sendMailService;

    @MockBean
    private JwtTokenUtil tokenUtil;

    @Test
    @WithMockUser("poxos")
    void register() throws Exception {
        String email = "user@example.com";
        String token = "7e43d0ac-2795-4525-9722-b0ff90a535ba";
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.setEmail(email);
        createUserRequestDto.setName("poxos");
        createUserRequestDto.setPassword("password");
        createUserRequestDto.setEnabled(false);
        createUserRequestDto.setToken(token);
        createUserRequestDto.setRole(Role.CUSTOMER);
        String jsonUser = new ObjectMapper().writeValueAsString(createUserRequestDto);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        doNothing().when(sendMailService).sendMail(anyString(), anyString(), anyString());
        UserDto registeredUser = userService.register(createUserRequestDto).getBody();
        assertNotNull(registeredUser);
        assertEquals("example@mail.com", registeredUser.getEmail());
        verify(userRepository, times(1)).findByEmail(eq(email));
        verify(userRepository, times(1)).save(any(User.class));
        verify(sendMailService, times(1)).sendMail(eq(email), eq("Welcome Green-Shop "),
                contains("Please verify your email by clicking on this url"));
    }

    @Test
    void requestPasswordReset_ValidEmail_ShouldReturnTrueAndSendEmail() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean result = userService.requestPasswordReset(email);
        assertTrue(result);
        assertNotNull(user.getToken());
        verify(sendMailService).sendMail(eq(email), eq("Change Password Green Shop "),
                contains("Please click here to change your password."));
        verify(userRepository).save(user);
    }

    @Test
    void requestPasswordReset_InvalidEmail_ShouldReturnFalse() {
        String invalidEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());
        boolean result = userService.requestPasswordReset(invalidEmail);
        assertFalse(result);
        verify(sendMailService, never()).sendMail(anyString(), anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void confirmPasswordReset_ValidToken_ShouldReturnTrueAndUpdatePasswordAndToken() {
        String validToken = "valid_token";
        String newPassword = "newPassword";
        User user = new User();
        user.setToken(validToken);
        when(userRepository.findByToken(validToken)).thenReturn(Optional.of(user));

        boolean result = userService.confirmPasswordReset(validToken, newPassword);
        assertTrue(result);
        assertNull(user.getToken());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
    }

    @Test
    void confirmPasswordReset_InvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid_token";
        when(userRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        boolean result = userService.confirmPasswordReset(invalidToken, "newPassword");
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyUser_ValidEmailAndToken_ShouldReturnOkAndVerifyUser() {
        String email = "user@example.com";
        String token = "valid_token";
        User user = new User();
        user.setEmail(email);
        user.setToken(token);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userService.verifyUser(email, token);
        assertEquals(ResponseEntity.ok("User verified"), response);
        assertTrue(user.isEnabled());
        assertNull(user.getToken());
        verify(userRepository).save(user);
    }

    @Test
    void verifyUser_InvalidEmail_ShouldReturnBadRequestWithInvalidEmailMessage() {
        String invalidEmail = "nonexistent@example.com";
        String token = "valid_token";
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userService.verifyUser(invalidEmail, token);
        assertEquals(ResponseEntity.badRequest().body("Invalid email"), response);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyUser_UserAlreadyVerified_ShouldReturnBadRequestWithUserAlreadyVerifiedMessage() {
        String email = "user@example.com";
        String token = "valid_token";
        User user = new User();
        user.setEmail(email);
        user.setToken(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.verifyUser(email, token);
        assertEquals(ResponseEntity.badRequest().body("User already verified"), response);
        assertFalse(user.isEnabled());
        assertNull(user.getToken());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyUser_InvalidToken_ShouldReturnBadRequestWithInvalidTokenMessage() {
        String email = "user@example.com";
        String invalidToken = "invalid_token";
        User user = new User();
        user.setEmail(email);
        user.setToken("valid_token");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userService.verifyUser(email, invalidToken);

        assertEquals(ResponseEntity.badRequest().body("Invalid token"), response);
        assertFalse(user.isEnabled());
        assertNotNull(user.getToken());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void auth_ValidCredentials_ShouldReturnOkWithToken() {
        String email = "user@example.com";
        String password = "password";
        String hashedPassword = passwordEncoder.encode(password);

        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(email, password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(tokenUtil.generateToken(email)).thenReturn("test_token");

        ResponseEntity<UserAuthResponseDto> response = userService.auth(userAuthRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test_token", response.getBody().getToken());
    }

    @Test
    void auth_InvalidEmail_ShouldReturnUnauthorized() {
        String invalidEmail = "nonexistent@example.com";
        String password = "password";

        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(invalidEmail, password);
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());
        ResponseEntity<UserAuthResponseDto> response = userService.auth(userAuthRequestDto);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void auth_InvalidPassword_ShouldReturnUnauthorized() {
        String email = "user@example.com";
        String validPassword = "password";
        String invalidPassword = "wrong_password";
        String hashedPassword = passwordEncoder.encode(validPassword);

        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto(email, invalidPassword);
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(invalidPassword, hashedPassword)).thenReturn(false);
        ResponseEntity<UserAuthResponseDto> response = userService.auth(userAuthRequestDto);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }
}