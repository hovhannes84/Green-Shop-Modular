package com.example.greenshoprest.filter;

import com.example.greenshoprest.security.CurrentUserDetailServiceImpl;
import com.example.greenshoprest.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
class JWTAuthenticationTokenFilterTest {

    @Mock
    private JwtTokenUtil tokenUtil;

    @Mock
    private CurrentUserDetailServiceImpl userDetailsService;

    @InjectMocks
    private JWTAuthenticationTokenFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testValidToken() throws ServletException, IOException {
        String token = "valid_token_here";
        String username = "test_user";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(tokenUtil.validateToken(token, username)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

    }
}
