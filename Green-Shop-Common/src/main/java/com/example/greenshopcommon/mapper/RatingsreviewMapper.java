package com.example.greenshopcommon.mapper;

import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopcommon.entity.Ratingsreview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingsreviewMapper {

    Ratingsreview map(CreateRatingsreviewRequestDto dto);
    RatingsreviewDto mapToDto(Ratingsreview entity);
    Ratingsreview updateDto(UpdateRatingsreviewRequestDto entity);
}
