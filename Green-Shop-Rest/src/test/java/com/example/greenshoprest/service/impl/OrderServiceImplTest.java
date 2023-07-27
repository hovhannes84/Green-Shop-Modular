package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshopcommon.entity.*;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.mapper.OrderMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.CartRepository;
import com.example.greenshopcommon.repository.OrderRepository;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshoprest.service.CartService;
import com.example.greenshoprest.service.OrderService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderServiceImplTest {


    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;


    private User testUser;
    private Product testProduct;
    private Order testOrder;
    private Category testCategory;


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

        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("TestCategory");

        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("TestProduct");
        testProduct.setPrice(10.0);
        testProduct.setQuantity(100);
        testProduct.setCategory(testCategory);
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        testOrder = new Order();
        testOrder.setId(1);
        testOrder.setUser(testUser);
        testOrder.setProduct(testProduct);
        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        UserDto userDto = new UserDto();
        userDto.setId(testUser.getId());
        userDto.setName(testUser.getName());
        when(userMapper.mapToDto(testUser)).thenReturn(userDto);

        ProductDto productDto = new ProductDto();
        productDto.setId(testProduct.getId());
        productDto.setName(testProduct.getName());
        productDto.setPrice(testProduct.getPrice());
        productDto.setQuantity(testProduct.getQuantity());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(testCategory.getId());
        categoryDto.setName(testCategory.getName());
        productDto.setCategoryDto(categoryDto);

        when(productMapper.mapToDto(testProduct)).thenReturn(productDto);
        when(categoryMapper.mapToDto(testCategory)).thenReturn(categoryDto);
    }

    @Test
    void testGetOrdersByUserId_ExistingUser_ReturnsOrderDtoList() {

        List<Order> orders = new ArrayList<>();
        orders.add(testOrder);
        when(orderRepository.findAllByUserId(testUser.getId())).thenReturn(orders);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(testOrder.getId());

        UserDto userDto = new UserDto();
        userDto.setId(testUser.getId());
        userDto.setName(testUser.getName());
        orderDto.setUserDto(userDto);

        ProductDto productDto = new ProductDto();
        productDto.setId(testProduct.getId());
        productDto.setName(testProduct.getName());
        productDto.setPrice(testProduct.getPrice());
        productDto.setQuantity(testProduct.getQuantity());
        orderDto.setProductDto(productDto);

        when(orderMapper.mapToDto(testOrder)).thenReturn(orderDto);
        when(userMapper.mapToDto(testUser)).thenReturn(userDto);
        when(productMapper.mapToDto(testProduct)).thenReturn(productDto);

        ResponseEntity<List<OrderDto>> response = orderService.getOrdersByUserId(testUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<OrderDto> resultOrderDtos = response.getBody();
        assertEquals(1, resultOrderDtos.size());
        OrderDto resultOrderDto = resultOrderDtos.get(0);
        assertEquals(testOrder.getId(), resultOrderDto.getId());
        assertEquals(testUser.getId(), resultOrderDto.getUserDto().getId());
        assertEquals(testUser.getName(), resultOrderDto.getUserDto().getName());

        assertEquals(testProduct.getId(), resultOrderDto.getProductDto().getId());
        assertEquals(testProduct.getName(), resultOrderDto.getProductDto().getName());
        assertEquals(testProduct.getPrice(), resultOrderDto.getProductDto().getPrice());
        assertEquals(testProduct.getQuantity(), resultOrderDto.getProductDto().getQuantity());
    }

    @Test
    void testGetOrdersByUserId_UserWithNoOrders_ReturnsNotFoundResponse() {

        when(orderRepository.findAllByUserId(testUser.getId())).thenReturn(new ArrayList<>());
        ResponseEntity<List<OrderDto>> response = orderService.getOrdersByUserId(testUser);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(orderRepository, times(1)).findAllByUserId(testUser.getId());
        verify(userMapper, never()).mapToDto(any());
        verify(productMapper, never()).mapToDto(any());
        verify(categoryMapper, never()).mapToDto(any());
    }

    @Test
    void testGetOrderById_NonExistentOrderId_ReturnsNotFoundResponse() {

        int nonExistentOrderId = 999;
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());
        ResponseEntity<OrderDto> response = orderService.getOrderById(nonExistentOrderId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void testAddOrder_OrderNotFound() {

        User user = new User();
        user.setId(1);
        CreateOrderRequestDto createOrderRequestDto = new CreateOrderRequestDto();
        createOrderRequestDto.setProductDto(new ProductDto());
        createOrderRequestDto.getProductDto().setId(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        ResponseEntity<OrderDto> response = orderService.addOrder(createOrderRequestDto, user);

        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Product.class));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddOrder_ProductNotFound() {

        User user = new User();
        user.setId(1);
        CreateOrderRequestDto createOrderRequestDto = new CreateOrderRequestDto();
        createOrderRequestDto.setProductDto(new ProductDto());
        createOrderRequestDto.getProductDto().setId(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(createOrderRequestDto.getProductDto().getId())).thenReturn(Optional.empty());
        ResponseEntity<OrderDto> response = orderService.addOrder(createOrderRequestDto, user);

        verify(orderRepository, never()).save(any(Order.class));
        verify(productRepository, never()).save(any(Product.class));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteOrderById_OrderExists_Success() {

        int orderId = 1;
        Order order = new Order();
        order.setId(orderId);

        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        OrderService orderService = new OrderServiceImpl(orderRepository,
                orderMapper,
                userRepository,
                userMapper,
                productRepository,
                productMapper,
                categoryMapper,
                cartRepository,
                cartService);

        ResponseEntity<?> response = orderService.deleteOrderById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteOrderById_OrderNotExists_NotFound() {

        int orderId = 1;
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        OrderService orderService = new OrderServiceImpl(orderRepository,
                orderMapper,
                userRepository,
                userMapper,
                productRepository,
                productMapper,
                categoryMapper,
                cartRepository,
                cartService);

        ResponseEntity<?> response = orderService.deleteOrderById(orderId);
        verify(orderRepository, never()).deleteById(orderId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

