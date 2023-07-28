package com.example.greenshopcommon.dto.userDto;

import com.example.greenshopcommon.entity.Role;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;

    @NotBlank(message = "Name is required")
    private String name;

    private String surname;

    @NotBlank(message = "Email is required")
    private String email;

    private Role role;
}
