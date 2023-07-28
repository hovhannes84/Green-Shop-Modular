package com.example.greenshopcommon.dto.orderDto;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderRequestDto {

    private int id;

    @Valid
    private UserDto userDto;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date orderDate;

    @Valid
    private ProductDto productDto;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

}
