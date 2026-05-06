package com.bridgelabz.user_service.controller;

import com.bridgelabz.common_library.dto.ApiResponse;
import com.bridgelabz.user_service.dto.LoginRequestDTO;
import com.bridgelabz.user_service.dto.UserRequestDTO;
import com.bridgelabz.user_service.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<?> register(@Valid @RequestBody UserRequestDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        return userService.login(dto);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<?> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/delete/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ApiResponse<>(true, "User deleted", null);
    }
    @GetMapping("/products")
    public ApiResponse<?> getProducts() {
        return userService.getProductsFromUserService();
    }
    @GetMapping("/email/{email}")
    public ApiResponse<?> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}