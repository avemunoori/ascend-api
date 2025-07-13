package com.ascend.session;

import com.ascend.auth.JwtService;
import com.ascend.user.User;
import com.ascend.user.UserRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.ascend.session.SessionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;
    private CreateSessionRequest createSessionRequest;
    private UpdateSessionRequest updateSessionRequest;
    private String validToken;
    private User testUser;
    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        // objectMapper = new ObjectMapper(); // Remove manual instantiation
        
        // Create a test user
        testUser = User.builder()
                .email("test@example.com")
                .password(BCrypt.hashpw("password123", BCrypt.gensalt()))
                .firstName("Test")
                .lastName("User")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);
        
        // Generate a valid JWT token
        validToken = jwtService.generateToken(testUser.getId());

        createSessionRequest = new CreateSessionRequest();
        createSessionRequest.setDiscipline(SessionDiscipline.BOULDER);
        createSessionRequest.setGrade(Grade.V4);
        createSessionRequest.setDate(LocalDate.now());
        createSessionRequest.setNotes("Test session");
        createSessionRequest.setSent(false);

        updateSessionRequest = new UpdateSessionRequest();
        updateSessionRequest.setGrade(Grade.V5);
        updateSessionRequest.setNotes("Updated session");
    }

    @Test
    void createSession_WithValidRequest_ShouldReturnSessionResponse() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andReturn();
        System.out.println("[createSession_WithValidRequest_ShouldReturnSessionResponse] Status: " + result.getResponse().getStatus());
        System.out.println("[createSession_WithValidRequest_ShouldReturnSessionResponse] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getUserSessions_ShouldReturnUserSessions() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/sessions")
                .header("Authorization", "Bearer " + validToken))
                .andReturn();
        System.out.println("[getUserSessions_ShouldReturnUserSessions] Status: " + result.getResponse().getStatus());
        System.out.println("[getUserSessions_ShouldReturnUserSessions] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    private String createSessionAndGetId() throws Exception {
        String response = mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asText();
    }

    @Test
    void getSessionById_WithValidId_ShouldReturnSession() throws Exception {
        String sessionId = createSessionAndGetId();
        MvcResult result = mockMvc.perform(get("/api/sessions/{sessionId}", sessionId)
                .header("Authorization", "Bearer " + validToken))
                .andReturn();
        System.out.println("[getSessionById_WithValidId_ShouldReturnSession] Status: " + result.getResponse().getStatus());
        System.out.println("[getSessionById_WithValidId_ShouldReturnSession] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getSessionsByDiscipline_ShouldReturnFilteredSessions() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/sessions/discipline/BOULDER")
                .header("Authorization", "Bearer " + validToken))
                .andReturn();
        System.out.println("[getSessionsByDiscipline_ShouldReturnFilteredSessions] Status: " + result.getResponse().getStatus());
        System.out.println("[getSessionsByDiscipline_ShouldReturnFilteredSessions] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getSessionsByDate_ShouldReturnFilteredSessions() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/sessions/date/{date}", LocalDate.now())
                .header("Authorization", "Bearer " + validToken))
                .andReturn();
        System.out.println("[getSessionsByDate_ShouldReturnFilteredSessions] Status: " + result.getResponse().getStatus());
        System.out.println("[getSessionsByDate_ShouldReturnFilteredSessions] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void updateSession_WithValidRequest_ShouldReturnUpdatedSession() throws Exception {
        String sessionId = createSessionAndGetId();
        MvcResult result = mockMvc.perform(patch("/api/sessions/{sessionId}", sessionId)
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSessionRequest)))
                .andReturn();
        System.out.println("[updateSession_WithValidRequest_ShouldReturnUpdatedSession] Status: " + result.getResponse().getStatus());
        System.out.println("[updateSession_WithValidRequest_ShouldReturnUpdatedSession] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void replaceSession_WithValidRequest_ShouldReturnReplacedSession() throws Exception {
        String sessionId = createSessionAndGetId();
        MvcResult result = mockMvc.perform(put("/api/sessions/{sessionId}", sessionId)
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andReturn();
        System.out.println("[replaceSession_WithValidRequest_ShouldReturnReplacedSession] Status: " + result.getResponse().getStatus());
        System.out.println("[replaceSession_WithValidRequest_ShouldReturnReplacedSession] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void deleteSession_WithValidId_ShouldReturnNoContent() throws Exception {
        String sessionId = createSessionAndGetId();
        MvcResult result = mockMvc.perform(delete("/api/sessions/{sessionId}", sessionId)
                .header("Authorization", "Bearer " + validToken))
                .andReturn();
        System.out.println("[deleteSession_WithValidId_ShouldReturnNoContent] Status: " + result.getResponse().getStatus());
        System.out.println("[deleteSession_WithValidId_ShouldReturnNoContent] Body: " + result.getResponse().getContentAsString());
        assertEquals(204, result.getResponse().getStatus());
    }

    @Test
    void getAnalytics_ShouldReturnSessionAnalytics() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/sessions/analytics")
                .header("Authorization", "Bearer " + validToken))
                .andReturn();
        System.out.println("[getAnalytics_ShouldReturnSessionAnalytics] Status: " + result.getResponse().getStatus());
        System.out.println("[getAnalytics_ShouldReturnSessionAnalytics] Body: " + result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void getAnalytics_WithWeekPeriod_ShouldReturnFilteredAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/analytics")
                .param("period", "week")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAnalytics_WithMonthPeriod_ShouldReturnFilteredAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/analytics")
                .param("period", "month")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAnalytics_WithYearPeriod_ShouldReturnFilteredAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/analytics")
                .param("period", "year")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAnalytics_WithInvalidPeriod_ShouldReturnAllTimeAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/analytics")
                .param("period", "invalid")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getStatsOverview_ShouldReturnSessionAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/overview")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getStatsOverview_WithWeekPeriod_ShouldReturnFilteredAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/overview")
                .param("period", "week")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getProgressStats_ShouldReturnProgressAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/progress")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getProgressStats_WithMonthPeriod_ShouldReturnFilteredAnalytics() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/progress")
                .param("period", "month")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getHighestGrades_ShouldReturnHighestGrades() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/highest")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getHighestGrades_WithYearPeriod_ShouldReturnFilteredGrades() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/highest")
                .param("period", "year")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAverageGrades_ShouldReturnAverageGrades() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/average")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAverageGrades_WithWeekPeriod_ShouldReturnFilteredGrades() throws Exception {
        mockMvc.perform(get("/api/sessions/stats/average")
                .param("period", "week")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void getGradesForDiscipline_ShouldReturnGrades() throws Exception {
        mockMvc.perform(get("/api/sessions/grades/BOULDER"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCreateSessionWithAllDisciplines() throws Exception {
        // Test BOULDER discipline
        CreateSessionRequest boulderRequest = new CreateSessionRequest();
        boulderRequest.setDiscipline(SessionDiscipline.BOULDER);
        boulderRequest.setGrade(Grade.V3);
        boulderRequest.setDate(LocalDate.now());
        boulderRequest.setSent(true);

        mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boulderRequest)))
                .andExpect(status().isOk());

        // Test LEAD discipline
        CreateSessionRequest leadRequest = new CreateSessionRequest();
        leadRequest.setDiscipline(SessionDiscipline.LEAD);
        leadRequest.setGrade(Grade.YDS_5_10A);
        leadRequest.setDate(LocalDate.now());
        leadRequest.setSent(true);

        mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(leadRequest)))
                .andExpect(status().isOk());

        // Test TOP_ROPE discipline
        CreateSessionRequest topRopeRequest = new CreateSessionRequest();
        topRopeRequest.setDiscipline(SessionDiscipline.TOP_ROPE);
        topRopeRequest.setGrade(Grade.YDS_5_8);
        topRopeRequest.setDate(LocalDate.now());
        topRopeRequest.setSent(true);

        mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(topRopeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testInvalidGradeForDiscipline() throws Exception {
        // Test V-scale grade with LEAD discipline (should fail)
        CreateSessionRequest invalidRequest = new CreateSessionRequest();
        invalidRequest.setDiscipline(SessionDiscipline.LEAD);
        invalidRequest.setGrade(Grade.V3); // V-scale grade for LEAD discipline
        invalidRequest.setDate(LocalDate.now());
        invalidRequest.setSent(true);

        mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Grade is not compatible with the selected discipline"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testInvalidDiscipline() throws Exception {
        // Test with invalid discipline (should fail)
        String invalidRequest = """
            {
                "discipline": "INVALID_DISCIPLINE",
                "grade": "V3",
                "date": "2024-01-15",
                "sent": true
            }
            """;

        mockMvc.perform(post("/api/sessions")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid discipline. Supported disciplines: BOULDER, LEAD, TOP_ROPE"));
    }
} 