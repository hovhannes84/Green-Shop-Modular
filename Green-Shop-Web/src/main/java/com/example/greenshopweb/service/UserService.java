package com.example.greenshopweb.service;

public interface UserService {
    Boolean requestPasswordReset(String email);

    Boolean confirmPasswordReset(String token, String newPassword);
}
