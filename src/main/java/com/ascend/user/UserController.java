package com.ascend.user;

import com.ascend.auth.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ascend.auth.JwtService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request.getEmail(), request.getPassword());
        UserResponse response = new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id).orElseThrow();
        UserResponse response = new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        User user = userService.getUserById(userId).orElseThrow();
        UserResponse response = new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
        return ResponseEntity.ok(response);
    }
}