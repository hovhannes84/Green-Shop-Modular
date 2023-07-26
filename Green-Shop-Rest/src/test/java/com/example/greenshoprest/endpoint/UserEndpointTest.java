package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthResponseDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshoprest.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private UserEndpoint userEndpoint;


    @Test
    public void testAuth() throws Exception {
        UserAuthRequestDto userAuthRequestDto = new UserAuthRequestDto("test@example.com", "password");
        UserAuthResponseDto mockResponse = new UserAuthResponseDto("test-token");
        given(userService.auth(any(UserAuthRequestDto.class))).willReturn(ResponseEntity.ok(mockResponse));
        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userAuthRequestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("test-token"));
    }


    @Test
    public void testRegister() throws Exception {
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setName("user");
        requestDto.setPassword("password");
        requestDto.setEmail("testuser@mail.com");

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("user");
        userDto.setEmail("testuser@mail.com");
        Mockito.when(userService.register(Mockito.any(CreateUserRequestDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("testuser@mail.com"));
    }

    @Test
    public void testVerifyUser_Success() {
        String email = "test@mail.com";
        String token = "testToken";
        when(userService.verifyUser(email, token)).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<?> response = userEndpoint.verifyUser(email, token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testVerifyUser_Failure() {
        String email = "test@mail.com";
        String token = "invalidToken";
        when(userService.verifyUser(email, token)).thenReturn(ResponseEntity.badRequest().build());
        ResponseEntity<?> response = userEndpoint.verifyUser(email, token);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testVerifyUser_InvalidToken() throws Exception {
        String email = "test@mail.com";
        String token = "invalid-token";
        when(userService.verifyUser(email, token)).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        mockMvc.perform(get("/user/verify")
                        .param("email", email)
                        .param("token", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRequestPasswordReset_Success() throws Exception {
        String email = "test@mail.com";
        given(userService.requestPasswordReset(email)).willReturn(true);
        mockMvc.perform(post("/user/password-reset")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Password reset request has been sent"));
    }

    @Test
    public void testRequestPasswordReset_UserNotFound() throws Exception {
        String email = "nouser@mail.com";
        given(userService.requestPasswordReset(email)).willReturn(false);
        mockMvc.perform(post("/user/password-reset")
                        .param("email", email))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User with provided email not found"));
    }

    @Test
    public void testConfirmPasswordReset_Success() throws Exception {
        String token = "valid-token";
        String newPassword = "newPassword";
        given(userService.confirmPasswordReset(token, newPassword)).willReturn(true);
        mockMvc.perform(post("/user/password-reset/confirm")
                        .param("token", token)
                        .param("password", newPassword))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Password has been reset successfully"));
    }

    @Test
    public void testConfirmPasswordReset_InvalidToken() throws Exception {
        String token = "invalid-token";
        String newPassword = "newPassword";
        given(userService.confirmPasswordReset(token, newPassword)).willReturn(false);
        mockMvc.perform(post("/user/password-reset/confirm")
                        .param("token", token)
                        .param("password", newPassword))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid or expired token"));
    }

    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}