package com.bridgelabz.user_service.service;

import com.bridgelabz.common_library.dto.ApiResponse;
import com.bridgelabz.user_service.client.ProductClient;
import com.bridgelabz.user_service.dto.LoginRequestDTO;
import com.bridgelabz.user_service.dto.UserRequestDTO;
import com.bridgelabz.user_service.entity.User;
import com.bridgelabz.user_service.mapper.UserMapper;
import com.bridgelabz.user_service.repository.UserRepository;
import com.bridgelabz.user_service.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final ProductClient productClient;

    public UserServiceImpl(UserRepository repository,
                           BCryptPasswordEncoder encoder,
                           JwtUtil jwtUtil,
                           ProductClient productClient) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.productClient = productClient;
    }

    @Override
    public ApiResponse<?> register(UserRequestDTO dto) {

        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            return new ApiResponse<>(false, "Email already exists", null);
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(encoder.encode(dto.getPassword()));

        if (dto.getRole() != null && dto.getRole().equalsIgnoreCase("ADMIN")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        repository.save(user);

        return new ApiResponse<>(true, "User created", null);
    }

    @Override
    public ApiResponse<?> login(LoginRequestDTO dto) {

        User user = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            return new ApiResponse<>(false, "Invalid password", null);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new ApiResponse<>(true, "Login successful", token);
    }

    @Override
    public ApiResponse<?> getAllUsers() {
        return new ApiResponse<>(true, "Users fetched", repository.findAll());
    }

    @Override
    public ApiResponse<?> getProductsFromUserService() {
        return productClient.getAllProducts();
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public ApiResponse<?> getUserByEmail(String email) {
        return repository.findByEmail(email)
                .map(user -> new ApiResponse<>(true, "User found", user))
                .orElse(new ApiResponse<>(false, "User not found", null));
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}