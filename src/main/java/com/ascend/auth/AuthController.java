package com.ascend.auth;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import com.ascend.user.UserService;
import com.ascend.user.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;
    private final DomainValidator domainValidator;

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

            // Validate email domain
            if (!domainValidator.isValidDomain(request.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email domain not allowed. Allowed domains: " + String.join(", ", domainValidator.getAllowedDomains())));
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            // Always return success to prevent email enumeration
            return ResponseEntity.ok(Map.of("message", "If an account with that email exists, a password reset code has been sent"));
        } catch (Exception e) {
            log.error("Error in forgot password request", e);
            return ResponseEntity.status(500).body(Map.of("message", "An error occurred while processing your request"));
        }
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@Valid @RequestBody VerifyResetCodeRequest request) {
        try {
            boolean isValid = passwordResetService.verifyCode(request.getCode());
            
            if (isValid) {
                return ResponseEntity.ok(Map.of("message", "Code is valid"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired reset code"));
            }
        } catch (Exception e) {
            log.error("Error in code verification", e);
            return ResponseEntity.status(500).body(Map.of("message", "An error occurred while verifying the code"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            boolean success = passwordResetService.resetPassword(request.getCode(), request.getNewPassword());
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired reset code"));
            }
        } catch (Exception e) {
            log.error("Error in password reset", e);
            return ResponseEntity.status(500).body(Map.of("message", "An error occurred while resetting your password"));
        }
    }
}
