package com.example.greenshoprest.security;

import static org.junit.jupiter.api.Assertions.*;

import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@ExtendWith(MockitoExtension.class)
public class CurrentUserTest {

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("testPassword");
        testUser.setEnabled(true);
        testUser.setRole(Role.CUSTOMER);
    }

    @Test
    public void testCurrentUserProperties() {

        CurrentUser currentUser = new CurrentUser(testUser);

        assertEquals(testUser.getEmail(), currentUser.getUsername());
        assertEquals(testUser.getPassword(), currentUser.getPassword());
        assertTrue(currentUser.isEnabled());
        assertTrue(currentUser.isAccountNonExpired());
        assertTrue(currentUser.isAccountNonLocked());
        assertTrue(currentUser.isCredentialsNonExpired());
    }

    @Test
    public void testCurrentUserAuthorities() {
        CurrentUser currentUser = new CurrentUser(testUser);
        Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();

        assertNotNull(authorities);
        assertFalse(authorities.isEmpty());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("CUSTOMER")));
    }
}
