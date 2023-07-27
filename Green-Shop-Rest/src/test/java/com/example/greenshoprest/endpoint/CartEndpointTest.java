package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CartEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private CurrentUser currentUser;


    private UserDto regularUserDto;
    private ProductDto productDto;
    private User regularUser;

    @BeforeEach
    void setUp() {
        regularUserDto = new UserDto();
        regularUserDto.setId(2);
        regularUserDto.setEmail("poxos@mail.com");
        regularUserDto.setName("poxos");
        regularUserDto.setRole(Role.CUSTOMER);

        regularUser = new User();
        regularUser.setId(2);
        regularUser.setEmail("poxos@mail.com");
        regularUser.setEnabled(true);
        regularUser.setName("poxos");
        regularUser.setRole(Role.CUSTOMER);

        productDto = new ProductDto();
        productDto.setId(101);
        productDto.setName("Sample Product");
        productDto.setQuantity(5);
        productDto.setPrice(50.0);

        currentUser = Mockito.mock(CurrentUser.class);
        User user = new User();
        user.setId(2);
        user.setEmail("poxos@mail.com");
        user.setEnabled(true);
        user.setName("poxos");
        user.setRole(Role.CUSTOMER);

        Mockito.when(currentUser.getUser()).thenReturn(user);
    }
    @Test
    @WithMockUser(username = "poxos", roles = {"CUSTOMER"})
    public void testCreateCart() throws Exception {

        CurrentUser mockUser = Mockito.mock(CurrentUser.class);
        User user = new User();
        user.setId(2);
        user.setEmail("poxos@mail.com");
        user.setEnabled(true);
        user.setName("poxos");
        user.setRole(Role.CUSTOMER);

        Mockito.when(mockUser.getUser()).thenReturn(user);

        CreateCartRequestDto requestDto = new CreateCartRequestDto();
        requestDto.setId(1);
        requestDto.setQuantity(2);
        requestDto.setProductDto(productDto);
        requestDto.setUserDto(regularUserDto);

        CartDto expectedCartDto = new CartDto();
        expectedCartDto.setId(1);
        expectedCartDto.setProductDto(productDto);
        expectedCartDto.setUserDto(regularUserDto);
        expectedCartDto.setQuantity(3);
        ResponseEntity<CartDto> expectedResponse = ResponseEntity.ok(expectedCartDto);
        Mockito.when(cartService.addCart(user, requestDto)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/cart")
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(asJsonString(expectedCartDto)));
    }

    @Test
    @WithMockUser(username = "poxos", roles = {"CUSTOMER"})
    void testGetCartById() throws Exception {

        int cartId = 1;
        CartDto cartDto = new CartDto();
        cartDto.setId(cartId);
        cartDto.setUserDto(regularUserDto);
        when(cartService.findById(cartId)).thenReturn(ResponseEntity.ok(cartDto));

        mockMvc.perform(get("/cart/{id}", cartId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(cartId))
                .andExpect(jsonPath("$.userDto.id").value(regularUserDto.getId()))
                .andExpect(jsonPath("$.userDto.name").value(regularUserDto.getName()));

        verify(cartService).findById(cartId);
    }
    @Test
    public void testDeleteCartById() throws Exception {

        Product product = new Product();
        product.setId(102);
        product.setName("Sample Product");
        product.setQuantity(7);
        product.setPrice(50.0);

        Cart existingCart = new Cart();
        existingCart.setId(1);
        existingCart.setUser(regularUser);
        existingCart.setProduct(product);
        existingCart.setQuantity(1);

        CartService cartService = Mockito.mock(CartService.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);

        Mockito.when(currentUser.getUser()).thenReturn(regularUser);

        Mockito.when(cartService.deleteById(1)).thenReturn(ResponseEntity.noContent().build());

        CartEndpoint cartEndpoint = new CartEndpoint(cartService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(cartEndpoint).build();
        mockMvc.perform(delete("/cart/1"))
                .andExpect(status().isNoContent());


        Mockito.verify(cartService).deleteById(1);
        Mockito.verifyNoMoreInteractions(cartService);
    }

    private static String asJsonString(Object obj) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
