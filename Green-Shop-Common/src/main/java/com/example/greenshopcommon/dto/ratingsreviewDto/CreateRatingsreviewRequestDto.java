package com.example.greenshopcommon.dto.ratingsreviewDto;


import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRatingsreviewRequestDto {

    @Valid
    private UserDto userDto;
    @Valid
    private ProductDto productDto;

    @Max(value = 5, message = "Rating cannot be more than 5")
    private double rating;
    private String review;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date dateTime;

}
