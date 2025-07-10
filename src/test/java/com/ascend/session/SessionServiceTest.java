package com.ascend.session;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private User testUser;
    private Session currentWeekSession;
    private Session currentMonthSession;
    private Session currentYearSession;
    private Session oldSession;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        LocalDate now = LocalDate.now();
        
        // Create sessions for different periods
        currentWeekSession = Session.builder()
                .user(testUser)
                .discipline(SessionDiscipline.BOULDER)
                .grade(Grade.V4)
                .date(now)
                .sent(true)
                .build();

        currentMonthSession = Session.builder()
                .user(testUser)
                .discipline(SessionDiscipline.LEAD)
                .grade(Grade.V5)
                .date(now.withDayOfMonth(15))
                .sent(false)
                .build();

        currentYearSession = Session.builder()
                .user(testUser)
                .discipline(SessionDiscipline.BOULDER)
                .grade(Grade.V6)
                .date(now.withMonth(6))
                .sent(true)
                .build();

        oldSession = Session.builder()
                .user(testUser)
                .discipline(SessionDiscipline.LEAD)
                .grade(Grade.V3)
                .date(now.minusYears(1))
                .sent(false)
                .build();
    }

    @Test
    void getAnalytics_WithWeekPeriod_ShouldFilterCorrectly() {
        // Given
        List<Session> allSessions = Arrays.asList(currentWeekSession, currentMonthSession, currentYearSession, oldSession);
        when(sessionRepository.findByUserId(testUser.getId())).thenReturn(allSessions);

        // When
        SessionAnalytics analytics = sessionService.getAnalytics(testUser.getId(), "week");

        // Then
        assertEquals(1, analytics.getTotalSessions());
        assertEquals(Grade.V4.getNumericValue(), analytics.getAverageDifficulty());
        assertEquals(100.0, analytics.getSentPercentage());
        assertEquals(1, analytics.getSessionsByDiscipline().get(SessionDiscipline.BOULDER));
    }

    @Test
    void getAnalytics_WithMonthPeriod_ShouldFilterCorrectly() {
        // Given
        List<Session> allSessions = Arrays.asList(currentWeekSession, currentMonthSession, currentYearSession, oldSession);
        when(sessionRepository.findByUserId(testUser.getId())).thenReturn(allSessions);

        // When
        SessionAnalytics analytics = sessionService.getAnalytics(testUser.getId(), "month");

        // Then
        assertEquals(2, analytics.getTotalSessions());
        double expectedAvg = (Grade.V4.getNumericValue() + Grade.V5.getNumericValue()) / 2.0;
        assertEquals(expectedAvg, analytics.getAverageDifficulty());
        assertEquals(50.0, analytics.getSentPercentage());
    }

    @Test
    void getAnalytics_WithYearPeriod_ShouldFilterCorrectly() {
        // Given
        List<Session> allSessions = Arrays.asList(currentWeekSession, currentMonthSession, currentYearSession, oldSession);
        when(sessionRepository.findByUserId(testUser.getId())).thenReturn(allSessions);

        // When
        SessionAnalytics analytics = sessionService.getAnalytics(testUser.getId(), "year");

        // Then
        assertEquals(3, analytics.getTotalSessions());
        double expectedAvg = (Grade.V4.getNumericValue() + Grade.V5.getNumericValue() + Grade.V6.getNumericValue()) / 3.0;
        assertEquals(expectedAvg, analytics.getAverageDifficulty());
        assertEquals(66.67, analytics.getSentPercentage(), 0.01);
    }

    @Test
    void getAnalytics_WithNullPeriod_ShouldReturnAllSessions() {
        // Given
        List<Session> allSessions = Arrays.asList(currentWeekSession, currentMonthSession, currentYearSession, oldSession);
        when(sessionRepository.findByUserId(testUser.getId())).thenReturn(allSessions);

        // When
        SessionAnalytics analytics = sessionService.getAnalytics(testUser.getId(), null);

        // Then
        assertEquals(4, analytics.getTotalSessions());
    }

    @Test
    void getAnalytics_WithInvalidPeriod_ShouldReturnAllSessions() {
        // Given
        List<Session> allSessions = Arrays.asList(currentWeekSession, currentMonthSession, currentYearSession, oldSession);
        when(sessionRepository.findByUserId(testUser.getId())).thenReturn(allSessions);

        // When
        SessionAnalytics analytics = sessionService.getAnalytics(testUser.getId(), "invalid");

        // Then
        assertEquals(4, analytics.getTotalSessions());
    }

    @Test
    void getAnalytics_WithEmptyPeriod_ShouldReturnAllSessions() {
        // Given
        List<Session> allSessions = Arrays.asList(currentWeekSession, currentMonthSession, currentYearSession, oldSession);
        when(sessionRepository.findByUserId(testUser.getId())).thenReturn(allSessions);

        // When
        SessionAnalytics analytics = sessionService.getAnalytics(testUser.getId(), "");

        // Then
        assertEquals(4, analytics.getTotalSessions());
    }
} 