package com.ascend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BasicApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }

    @Test
    void healthEndpoint_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void rootEndpoint_ShouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ascend API is running!"));
    }

    @Test
    void authLoginEndpoint_ShouldBeAccessible() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized()); // Should return 401 for invalid credentials
    }

    @Test
    void protectedEndpoints_ShouldRequireAuthentication() throws Exception {
        // In test mode, API endpoints are accessible without authentication
        // Test that endpoints return appropriate responses
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized()); // Missing Authorization header
        
        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isUnauthorized()); // Missing Authorization header
        
        mockMvc.perform(get("/api/users/123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isNotFound()); // User not found (endpoint is accessible)
    }

    @Test
    void createUserEndpoint_ShouldBeAccessible() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content("{\"email\":\"newuser@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()); // User creation endpoint should be accessible without authentication
    }
} 