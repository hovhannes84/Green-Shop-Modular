package com.example.greenshopcommon.dto.userDto;

import com.example.greenshopcommon.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;
    private String name;
    private String surname;
    private String email;
    private Role role;
}
