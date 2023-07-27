package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
@SpringBootTest
@AutoConfigureMockMvc
public class OrderEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @MockBean
    private OrderService orderService;

    private User testUser;

    @BeforeEach
    public void setUp() {

        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("poxos@mail.com");
        testUser.setName("poxos");
        testUser.setEnabled(true);
        testUser.setPassword("password");
        testUser.setRole(Role.CUSTOMER);
        when(userRepository.save(testUser)).thenReturn(testUser);

        UserDto userDto = new UserDto();
        userDto.setId(testUser.getId());
        userDto.setName(testUser.getName());
        when(userMapper.mapToDto(testUser)).thenReturn(userDto);

    }

    @Test
    @WithMockUser("poxos")
    public void testGetOrderById() throws Exception {

        int orderId = 1;
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);

        when(orderService.getOrderById(orderId)).thenReturn(ResponseEntity.ok(orderDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/order/{id}", orderId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(orderDto.getId()));
    }

    @Test
    public void testGetOrdersByUserId() throws Exception {

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
        CurrentUser testCurrentUser = new CurrentUser(testUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(testCurrentUser, null, authorities);
        List<OrderDto> orderDtos = Collections.singletonList(new OrderDto());
        when(orderService.getOrdersByUserId(testUser)).thenReturn(ResponseEntity.ok(orderDtos));

        mockMvc.perform(MockMvcRequestBuilders.get("/order")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)) // Authenticate the user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists());
    }
}
