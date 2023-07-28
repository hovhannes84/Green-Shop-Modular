package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.exception.IllegalArgumentExceptionError;
import com.example.greenshopcommon.mapper.CartMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User adminUser;
    private User regularUser;
    private ProductDto productDto;
    private Product product;
    private UserDto userDto;


    @BeforeEach
    void setUp() {

        adminUser = new User();
        adminUser.setId(1);
        adminUser.setEmail("poxos@mail.com");
        adminUser.setName("poxos");
        adminUser.setEnabled(true);
        adminUser.setRole(Role.ADMIN);

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

        product = new Product();
        product.setId(102);
        product.setName("Sample Product");
        product.setQuantity(7);
        product.setPrice(50.0);

        userDto = new UserDto();
        userDto.setId(5);
        userDto.setName("poxos");
        userDto.setEmail("poxos@example.com");
        userDto.setRole(Role.CUSTOMER);
    }

    @Test
    void testFindCartsByUser_AdminRole_ReturnsCarts() {
        UserDto userDto = new UserDto();
        userDto.setName("Admin_Poxos");
        userDto.setEmail("poxos@mail.com");
        userDto.setRole(Role.ADMIN);

        List<Cart> carts = new ArrayList<>();
        when(cartRepository.findAll()).thenReturn(carts);
        List<CartDto> cartDtos = new ArrayList<>();

        for (Cart cart : carts) {
            CartDto cartDto = CartDto.builder()
                    .id(cart.getId())
                    .userDto(userDto)
                    .productDto(productDto)
                    .quantity(cart.getQuantity())
                    .build();
            cartDtos.add(cartDto);
        }
        ResponseEntity<List<CartDto>> responseEntity = cartService.findCartsByUser(adminUser);

        assertEquals(cartDtos, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(cartRepository, times(1)).findAll();
        verify(cartRepository, never()).findAllByUserId(anyInt());
        verify(cartMapper, times(carts.size())).mapToDto(any(Cart.class));
    }

    @Test
    void testFindCartsByUser_RegularUserRole_ReturnsCarts() {
        int regularUserId = regularUser.getId();
        List<Cart> carts = new ArrayList<>();

        when(cartRepository.findAllByUserId(regularUserId)).thenReturn(carts);

        List<CartDto> cartDtos = new ArrayList<>();
        ResponseEntity<List<CartDto>> responseEntity = cartService.findCartsByUser(regularUser);

        assertEquals(cartDtos, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(cartRepository, never()).findAll();
        verify(cartRepository, times(1)).findAllByUserId(anyInt());
        verify(cartMapper, times(carts.size())).mapToDto(any(Cart.class));
    }

    @Test
    void testFindCartById_CartFound_ReturnsCartDto() throws EntityNotFoundException {

        int cartId = 1;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setProduct(product);
        cart.setUser(regularUser);
        cart.setQuantity(1);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        CartDto cartDto = new CartDto();
        cartDto.setId(1);
        cartDto.setUserDto(userDto);
        cartDto.setProductDto(productDto);
        cartDto.setQuantity(7);

        when(cartMapper.mapToDto(cart)).thenReturn(cartDto);
        ResponseEntity<CartDto> responseEntity = cartService.findById(cartId);

        assertEquals(cartDto, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartMapper, times(1)).mapToDto(cart);
    }

    @Test
    void testFindCartById_CartNotFound_ReturnsNotFound() throws EntityNotFoundException {

        int cartId = 1;

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        ResponseEntity<CartDto> responseEntity = cartService.findById(cartId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        verify(cartRepository, times(1)).findById(cartId);
        verify(cartMapper, never()).mapToDto(any());
    }

    @Test
    void testAddCart_ReturnsCreatedCartDto() throws IllegalArgumentExceptionError {

        CreateCartRequestDto createCartRequestDto = new CreateCartRequestDto();
        createCartRequestDto.setId(1);
        createCartRequestDto.setQuantity(2);
        createCartRequestDto.setProductDto(productDto);
        createCartRequestDto.setUserDto(userDto);

        Cart cart = new Cart();
        cart.setId(101);
        cart.setProduct(product);
        cart.setUser(regularUser);
        cart.setQuantity(1);

        when(cartMapper.map(createCartRequestDto)).thenReturn(cart);
        when(productMapper.dtoToMap(createCartRequestDto.getProductDto())).thenReturn(product);

        ResponseEntity<CartDto> responseEntity = cartService.addCart(regularUser, createCartRequestDto);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(cartMapper, times(1)).map(createCartRequestDto);
        verify(productMapper, times(1)).dtoToMap(createCartRequestDto.getProductDto());
        verify(cartRepository, times(1)).save(cart);
        verify(cartMapper, times(1)).mapToDto(cart);
    }

    @Test
    void testDeleteCartById_CartFound_ReturnsNoContent() {

        int cartId = 1;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setProduct(product);
        cart.setUser(regularUser);
        cart.setQuantity(1);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        ResponseEntity<?> responseEntity = cartService.deleteById(cartId);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).deleteById(cartId);
    }

    @Test
    void testDeleteCartById_CartNotFound_ReturnsNotFound() {

        int cartId = 1;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setProduct(product);
        cart.setUser(regularUser);
        cart.setQuantity(1);

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = cartService.deleteById(cartId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, never()).deleteById(anyInt());
    }

    @Test
    void testUpdateCart_CartFound_ReturnsUpdatedCartDto() {

        int cartId = 1;
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setProduct(product);
        cart.setUser(regularUser);
        cart.setQuantity(1);

        UpdateCartRequestDto updateCartRequestDto = new UpdateCartRequestDto();
        updateCartRequestDto.setId(1);
        updateCartRequestDto.setProductDto(productDto);
        updateCartRequestDto.setQuantity(3);
        updateCartRequestDto.setUserDto(userDto);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productMapper.dtoToMap(updateCartRequestDto.getProductDto())).thenReturn(product);

        Cart updatedCart = new Cart();
        updatedCart.setId(cartId);
        updatedCart.setProduct(product);
        updatedCart.setUser(regularUser);
        updatedCart.setQuantity(3);

        when(cartRepository.save(cart)).thenReturn(updatedCart);

        CartDto expectedCartDto = new CartDto();
        expectedCartDto.setId(cart.getId());
        expectedCartDto.setUserDto(userDto);
        expectedCartDto.setProductDto(productDto);
        expectedCartDto.setQuantity(updatedCart.getQuantity());

        when(cartMapper.mapToDto(updatedCart)).thenReturn(expectedCartDto);
        ResponseEntity<CartDto> responseEntity = cartService.updateCart(cartId, regularUser, updateCartRequestDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedCartDto, responseEntity.getBody());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productMapper, times(1)).dtoToMap(updateCartRequestDto.getProductDto());
        verify(cartRepository, times(1)).save(cart);
        verify(cartMapper, times(1)).mapToDto(updatedCart);
    }

    @Test
    void testUpdateCart_CartNotFound_ReturnsNoContent() {

        int cartId = 1;
        User user = new User();
        UpdateCartRequestDto updateCartRequestDto = new UpdateCartRequestDto();
        updateCartRequestDto.setId(1);
        updateCartRequestDto.setProductDto(productDto);
        updateCartRequestDto.setQuantity(3);
        updateCartRequestDto.setUserDto(userDto);
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        ResponseEntity<CartDto> responseEntity = cartService.updateCart(cartId, user, updateCartRequestDto);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productMapper, never()).dtoToMap(any());
        verify(cartRepository, never()).save(any());
        verify(cartMapper, never()).mapToDto(any());
    }
}
