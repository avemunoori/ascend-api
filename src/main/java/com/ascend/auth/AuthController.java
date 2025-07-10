package com.ascend.auth;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import com.ascend.user.UserService;
import com.ascend.user.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        boolean passwordMatches = BCrypt.checkpw(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        String token = jwtService.generateToken(user.getId());
        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt(), user.getFirstName(), user.getLastName());
        return ResponseEntity.ok(new JwtResponse(token, userResponse));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Missing authorization header"));
        }
        
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtService.validateToken(token);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt(), user.getFirstName(), user.getLastName());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CreateUserRequest request) {
        try {
            // Validate request
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters long"));
            }
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "First name is required"));
            }
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Last name is required"));
            }

            // Check if user already exists
            if (userRepository.findByEmail(request.getEmail().trim().toLowerCase()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "User with this email already exists"));
            }

            // Create new user
            User user = userService.createUser(
                request.getEmail().trim().toLowerCase(),
                request.getPassword(),
                request.getFirstName().trim(),
                request.getLastName().trim()
            );
            
            // Generate token for immediate login
            String token = jwtService.generateToken(user.getId());
            UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt(), user.getFirstName(), user.getLastName());
            return ResponseEntity.ok(new JwtResponse(token, userResponse));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.status(500).body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
}
