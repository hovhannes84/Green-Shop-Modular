package com.example.greenshopcommon.dto.orderDto;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequestDto {

    private int id;
    private UserDto userDto;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date orderDate;
    private ProductDto productDto;
    private int quantity;

}
