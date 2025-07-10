package com.ascend.auth;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import com.ascend.user.CreateUserRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String testUserEmail = "test@example.com";
    private String testUserPassword = "password123";
    private String testUserFirstName = "Test";
    private String testUserLastName = "User";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // Create a test user
        testUser = User.builder()
                .email(testUserEmail)
                .password(BCrypt.hashpw(testUserPassword, BCrypt.gensalt()))
                .firstName(testUserFirstName)
                .lastName(testUserLastName)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCompleteAuthenticationFlow() throws Exception {
        // 1. Test registration
        CreateUserRequest registerRequest = new CreateUserRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("newpassword123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("New"))
                .andExpect(jsonPath("$.user.lastName").value("User"))
                .andReturn().getResponse().getContentAsString();

        JsonNode registerJson = objectMapper.readTree(registerResponse);
        String registerToken = registerJson.get("token").asText();
        assertNotNull(registerToken);
        assertFalse(registerToken.isEmpty());
        assertEquals("newuser@example.com", registerJson.get("user").get("email").asText());
        assertEquals("New", registerJson.get("user").get("firstName").asText());
        assertEquals("User", registerJson.get("user").get("lastName").asText());

        // 2. Test login with existing user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testUserEmail);
        loginRequest.setPassword(testUserPassword);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value(testUserEmail))
                .andExpect(jsonPath("$.user.firstName").value(testUserFirstName))
                .andExpect(jsonPath("$.user.lastName").value(testUserLastName))
                .andReturn().getResponse().getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String loginToken = loginJson.get("token").asText();
        assertNotNull(loginToken);
        assertFalse(loginToken.isEmpty());
        assertEquals(testUserEmail, loginJson.get("user").get("email").asText());
        assertEquals(testUserFirstName, loginJson.get("user").get("firstName").asText());
        assertEquals(testUserLastName, loginJson.get("user").get("lastName").asText());

        // 3. Test token validation
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer " + loginToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(testUserEmail))
                .andExpect(jsonPath("$.firstName").value(testUserFirstName))
                .andExpect(jsonPath("$.lastName").value(testUserLastName));

        // 4. Test invalid login
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail(testUserEmail);
        invalidRequest.setPassword("wrongpassword");

        String invalidLoginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andReturn().getResponse().getContentAsString();
        JsonNode invalidLoginJson = objectMapper.readTree(invalidLoginResponse);
        assertEquals("Invalid credentials", invalidLoginJson.get("message").asText());

        // 5. Test invalid token validation
        String invalidTokenResponse = mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token"))
                .andReturn().getResponse().getContentAsString();
        JsonNode invalidTokenJson = objectMapper.readTree(invalidTokenResponse);
        assertEquals("Invalid token", invalidTokenJson.get("message").asText());

        // 6. Test missing authorization header
        String missingAuthResponse = mockMvc.perform(post("/api/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing authorization header"))
                .andReturn().getResponse().getContentAsString();
        JsonNode missingAuthJson = objectMapper.readTree(missingAuthResponse);
        assertEquals("Missing authorization header", missingAuthJson.get("message").asText());
    }

    @Test
    void testRegistrationValidation() throws Exception {
        // Test registration with invalid email
        CreateUserRequest invalidEmailRequest = new CreateUserRequest();
        invalidEmailRequest.setEmail("");
        invalidEmailRequest.setPassword("password123");
        invalidEmailRequest.setFirstName("Test");
        invalidEmailRequest.setLastName("User");

        String invalidEmailResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is required"))
                .andReturn().getResponse().getContentAsString();
        JsonNode invalidEmailJson = objectMapper.readTree(invalidEmailResponse);
        assertEquals("Email is required", invalidEmailJson.get("message").asText());

        // Test registration with short password
        CreateUserRequest shortPasswordRequest = new CreateUserRequest();
        shortPasswordRequest.setEmail("test@example.com");
        shortPasswordRequest.setPassword("123");
        shortPasswordRequest.setFirstName("Test");
        shortPasswordRequest.setLastName("User");

        String shortPasswordResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortPasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must be at least 6 characters long"))
                .andReturn().getResponse().getContentAsString();
        JsonNode shortPasswordJson = objectMapper.readTree(shortPasswordResponse);
        assertEquals("Password must be at least 6 characters long", shortPasswordJson.get("message").asText());

        // Test registration with missing first name
        CreateUserRequest missingFirstNameRequest = new CreateUserRequest();
        missingFirstNameRequest.setEmail("test2@example.com");
        missingFirstNameRequest.setPassword("password123");
        missingFirstNameRequest.setFirstName("");
        missingFirstNameRequest.setLastName("User");

        String missingFirstNameResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingFirstNameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("First name is required"))
                .andReturn().getResponse().getContentAsString();
        JsonNode missingFirstNameJson = objectMapper.readTree(missingFirstNameResponse);
        assertEquals("First name is required", missingFirstNameJson.get("message").asText());

        // Test registration with missing last name
        CreateUserRequest missingLastNameRequest = new CreateUserRequest();
        missingLastNameRequest.setEmail("test3@example.com");
        missingLastNameRequest.setPassword("password123");
        missingLastNameRequest.setFirstName("Test");
        missingLastNameRequest.setLastName("");

        String missingLastNameResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingLastNameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Last name is required"))
                .andReturn().getResponse().getContentAsString();
        JsonNode missingLastNameJson = objectMapper.readTree(missingLastNameResponse);
        assertEquals("Last name is required", missingLastNameJson.get("message").asText());

        // Test registration with duplicate email
        CreateUserRequest duplicateRequest = new CreateUserRequest();
        duplicateRequest.setEmail(testUserEmail);
        duplicateRequest.setPassword("password123");
        duplicateRequest.setFirstName("Test");
        duplicateRequest.setLastName("User");

        String duplicateEmailResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with this email already exists"))
                .andReturn().getResponse().getContentAsString();
        JsonNode duplicateEmailJson = objectMapper.readTree(duplicateEmailResponse);
        assertEquals("User with this email already exists", duplicateEmailJson.get("message").asText());
    }

    @Test
    void testJwtTokenExpiration() throws Exception {
        // This test verifies that JWT tokens are properly generated and can be validated
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testUserEmail);
        loginRequest.setPassword(testUserPassword);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value(testUserEmail))
                .andExpect(jsonPath("$.user.firstName").value(testUserFirstName))
                .andExpect(jsonPath("$.user.lastName").value(testUserLastName))
                .andReturn().getResponse().getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String token = loginJson.get("token").asText();
        assertNotNull(token);
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length, "JWT token should have 3 parts");

        // Verify token can be validated
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(testUserEmail))
                .andExpect(jsonPath("$.firstName").value(testUserFirstName))
                .andExpect(jsonPath("$.lastName").value(testUserLastName));
    }
} 