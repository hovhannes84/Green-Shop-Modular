package com.example.greenshoprest.service;


import com.example.greenshopcommon.dto.userDto.CreateUserRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthRequestDto;
import com.example.greenshopcommon.dto.userDto.UserAuthResponseDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<UserAuthResponseDto> auth(UserAuthRequestDto userAuthRequestDto);

    public ResponseEntity<UserDto> register(CreateUserRequestDto createUserRequestDto);


}
