package com.example.greenshopcommon.mapper;


import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.dto.orderDto.UpdateOrderRequestDto;
import com.example.greenshopcommon.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order map(CreateOrderRequestDto dto);
    OrderDto mapToDto(Order entity);
    Order updateDto(UpdateOrderRequestDto entity);

}
