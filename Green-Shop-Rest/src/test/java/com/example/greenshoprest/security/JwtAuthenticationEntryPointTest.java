package com.example.greenshoprest.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationEntryPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testJwtAuthenticationEntryPoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/secured-resource"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
