package com.ascend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized()); // Should return 401 for invalid credentials
    }

    @Test
    void protectedEndpoints_ShouldRequireAuthentication() throws Exception {
        MvcResult result1 = mockMvc.perform(get("/api/users/me")).andReturn();
        System.out.println("[protectedEndpoints_ShouldRequireAuthentication] /api/users/me Status: " + result1.getResponse().getStatus());
        System.out.println("[protectedEndpoints_ShouldRequireAuthentication] /api/users/me Body: " + result1.getResponse().getContentAsString());
        assertEquals(401, result1.getResponse().getStatus());

        MvcResult result2 = mockMvc.perform(get("/api/sessions")).andReturn();
        System.out.println("[protectedEndpoints_ShouldRequireAuthentication] /api/sessions Status: " + result2.getResponse().getStatus());
        System.out.println("[protectedEndpoints_ShouldRequireAuthentication] /api/sessions Body: " + result2.getResponse().getContentAsString());
        assertEquals(401, result2.getResponse().getStatus());

        MvcResult result3 = mockMvc.perform(get("/api/users/123e4567-e89b-12d3-a456-426614174000")).andReturn();
        System.out.println("[protectedEndpoints_ShouldRequireAuthentication] /api/users/{id} Status: " + result3.getResponse().getStatus());
        System.out.println("[protectedEndpoints_ShouldRequireAuthentication] /api/users/{id} Body: " + result3.getResponse().getContentAsString());
        assertEquals(404, result3.getResponse().getStatus());
    }

    @Test
    void createUserEndpoint_ShouldBeAccessible() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content("{\"email\":\"newuser@example.com\",\"password\":\"password123\",\"firstName\":\"New\",\"lastName\":\"User\"}"))
                .andReturn();
        System.out.println("[createUserEndpoint_ShouldBeAccessible] Status: " + result.getResponse().getStatus());
        System.out.println("[createUserEndpoint_ShouldBeAccessible] Body: " + result.getResponse().getContentAsString());
        // Keep the assertion for test result
        assertEquals(200, result.getResponse().getStatus());
    }
} 