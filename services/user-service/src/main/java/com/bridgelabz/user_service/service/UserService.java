package com.bridgelabz.user_service.service;

import com.bridgelabz.common_library.dto.ApiResponse;
import com.bridgelabz.user_service.dto.LoginRequestDTO;
import com.bridgelabz.user_service.dto.UserRequestDTO;
import com.bridgelabz.user_service.entity.User;

public interface UserService {

    ApiResponse<?> register(UserRequestDTO dto);

    ApiResponse<?> login(LoginRequestDTO dto);

    ApiResponse<?> getAllUsers();

    ApiResponse<?> getProductsFromUserService();

    User findByEmail(String email);

    void deleteUser(Long id);

    // ✅ ONLY DECLARE
    ApiResponse<?> getUserByEmail(String email);
}