package com.example.greenshopcommon.dto.userDto;

import com.example.greenshopcommon.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name can have a maximum of 50 characters")
    private String name;

    @Size(max = 50, message = "Surname can have a maximum of 50 characters")
    private String surname;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{11}", message = "Phone number must have exactly 11 digits")
    private String phone;

    private Role role;
    private String house;
    private String street;
    private String city;

    @Size(max = 10, message = "Postal code can have a maximum of 10 characters")
    private String postalCode;
}
