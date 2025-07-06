package com.ascend.session;

import com.ascend.user.User;
import com.ascend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public Session createSession(UUID userId, CreateSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Session session = Session.builder()
                .user(user)
                .discipline(request.getDiscipline())
                .grade(request.getGrade())
                .date(request.getDate())
                .notes(request.getNotes())
                .sent(request.isSent())
                .build();
        return sessionRepository.save(session);
    }

    public List<Session> getAllSessions(UUID userId) {
        return sessionRepository.findByUserId(userId);
    }

    public Optional<Session> getSessionById(UUID id) {
        return sessionRepository.findById(id);
    }

    public Session getSessionById(UUID sessionId, UUID userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to access this session");
        }
        
        return session;
    }

    public void deleteSession(UUID sessionId, UUID userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this session");
        }
        
        sessionRepository.deleteById(sessionId);
    }

    public Session updateSession(UUID sessionId, UUID userId, UpdateSessionRequest request) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this session");
        }
        
        // Update only non-null fields
        if (request.getDiscipline() != null) {
            session.setDiscipline(request.getDiscipline());
        }
        if (request.getGrade() != null) {
            session.setGrade(request.getGrade());
        }
        if (request.getDate() != null) {
            session.setDate(request.getDate());
        }
        if (request.getSent() != null) {
            session.setSent(request.getSent());
        }
        if (request.getNotes() != null) {
            session.setNotes(request.getNotes());
        }
        
        return sessionRepository.save(session);
    }

    public Session replaceSession(UUID sessionId, UUID userId, CreateSessionRequest request) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this session");
        }
        
        // Replace all fields
        session.setDiscipline(request.getDiscipline());
        session.setGrade(request.getGrade());
        session.setDate(request.getDate());
        session.setNotes(request.getNotes());
        session.setSent(request.isSent());
        
        return sessionRepository.save(session);
    }

    public Session updateSession(Session session) {
        return sessionRepository.save(session);
    }

    public List<Session> getSessionsByDiscipline(UUID userId, SessionDiscipline discipline) {
        return sessionRepository.findByUserIdAndDiscipline(userId, discipline);
    }

    public List<Session> getSessionsByDate(UUID userId, LocalDate date) {
        return sessionRepository.findByUserIdAndDate(userId, date);
    }

    public SessionAnalytics getAnalytics(UUID userId) {
        List<Session> sessions = getAllSessions(userId);
        
        if (sessions.isEmpty()) {
            return SessionAnalytics.builder()
                    .totalSessions(0)
                    .averageDifficulty(0.0)
                    .sentPercentage(0.0)
                    .sessionsByDiscipline(Map.of())
                    .averageDifficultyByDiscipline(Map.of())
                    .sentPercentageByDiscipline(Map.of())
                    .build();
        }

        // Calculate overall metrics
        double averageDifficulty = sessions.stream()
                .mapToDouble(session -> session.getGrade().getNumericValue())
                .average()
                .orElse(0.0);
        
        double sentPercentage = sessions.stream()
                .filter(Session::isSent)
                .count() * 100.0 / sessions.size();

        // Calculate metrics by discipline
        Map<SessionDiscipline, List<Session>> sessionsByDiscipline = sessions.stream()
                .collect(Collectors.groupingBy(Session::getDiscipline));

        Map<SessionDiscipline, Integer> disciplineCounts = sessionsByDiscipline.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                ));

        Map<SessionDiscipline, Double> avgDifficultyByDiscipline = sessionsByDiscipline.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(session -> session.getGrade().getNumericValue())
                                .average()
                                .orElse(0.0)
                ));

        Map<SessionDiscipline, Double> sentPercentageByDiscipline = sessionsByDiscipline.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<Session> disciplineSessions = entry.getValue();
                            return disciplineSessions.stream()
                                    .filter(Session::isSent)
                                    .count() * 100.0 / disciplineSessions.size();
                        }
                ));

        return SessionAnalytics.builder()
                .totalSessions(sessions.size())
                .averageDifficulty(averageDifficulty)
                .sentPercentage(sentPercentage)
                .sessionsByDiscipline(disciplineCounts)
                .averageDifficultyByDiscipline(avgDifficultyByDiscipline)
                .sentPercentageByDiscipline(sentPercentageByDiscipline)
                .build();
    }

    public ProgressAnalytics getProgressAnalytics(UUID userId) {
        List<Session> sessions = getAllSessions(userId);
        
        if (sessions.isEmpty()) {
            return ProgressAnalytics.builder()
                    .totalSessions(0)
                    .sentRate(0.0)
                    .avgDifficulty(0.0)
                    .progressByWeek(List.of())
                    .progressByMonth(List.of())
                    .build();
        }

        // Calculate overall metrics
        double avgDifficulty = sessions.stream()
                .mapToDouble(session -> session.getGrade().getNumericValue())
                .average()
                .orElse(0.0);
        
        double sentRate = sessions.stream()
                .filter(Session::isSent)
                .count() * 100.0 / sessions.size();

        // Group by week and month
        Map<String, List<Session>> weeklySessions = sessions.stream()
                .collect(Collectors.groupingBy(session -> {
                    LocalDate date = session.getDate();
                    int year = date.getYear();
                    int weekOfYear = date.getDayOfYear() / 7 + 1;
                    return String.format("%d-W%02d", year, weekOfYear);
                }));

        Map<String, List<Session>> monthlySessions = sessions.stream()
                .collect(Collectors.groupingBy(session -> {
                    LocalDate date = session.getDate();
                    return String.format("%d-%02d", date.getYear(), date.getMonthValue());
                }));

        // Build weekly progress
        List<ProgressAnalytics.WeeklyProgress> weeklyProgress = weeklySessions.entrySet().stream()
                .map(entry -> {
                    List<Session> weekSessions = entry.getValue();
                    double weekAvgDifficulty = weekSessions.stream()
                            .mapToDouble(session -> session.getGrade().getNumericValue())
                            .average()
                            .orElse(0.0);
                    double weekSentRate = weekSessions.stream()
                            .filter(Session::isSent)
                            .count() * 100.0 / weekSessions.size();
                    
                    return ProgressAnalytics.WeeklyProgress.builder()
                            .week(entry.getKey())
                            .avgDifficulty(weekAvgDifficulty)
                            .sessionCount(weekSessions.size())
                            .sentRate(weekSentRate)
                            .build();
                })
                .sorted((a, b) -> a.getWeek().compareTo(b.getWeek()))
                .collect(Collectors.toList());

        // Build monthly progress
        List<ProgressAnalytics.MonthlyProgress> monthlyProgress = monthlySessions.entrySet().stream()
                .map(entry -> {
                    List<Session> monthSessions = entry.getValue();
                    double monthAvgDifficulty = monthSessions.stream()
                            .mapToDouble(session -> session.getGrade().getNumericValue())
                            .average()
                            .orElse(0.0);
                    double monthSentRate = monthSessions.stream()
                            .filter(Session::isSent)
                            .count() * 100.0 / monthSessions.size();
                    
                    return ProgressAnalytics.MonthlyProgress.builder()
                            .month(entry.getKey())
                            .avgDifficulty(monthAvgDifficulty)
                            .sessionCount(monthSessions.size())
                            .sentRate(monthSentRate)
                            .build();
                })
                .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                .collect(Collectors.toList());

        return ProgressAnalytics.builder()
                .totalSessions(sessions.size())
                .sentRate(sentRate)
                .avgDifficulty(avgDifficulty)
                .progressByWeek(weeklyProgress)
                .progressByMonth(monthlyProgress)
                .build();
    }

    public Map<SessionDiscipline, Grade> getHighestGrades(UUID userId) {
        List<Session> sessions = getAllSessions(userId);
        
        if (sessions.isEmpty()) {
            return Map.of();
        }

        return sessions.stream()
                .collect(Collectors.groupingBy(Session::getDiscipline,
                        Collectors.maxBy((s1, s2) -> 
                                Double.compare(s1.getGrade().getNumericValue(), s2.getGrade().getNumericValue()))))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().orElseThrow().getGrade()
                ));
    }

    public Map<SessionDiscipline, Double> getAverageGrades(UUID userId) {
        List<Session> sessions = getAllSessions(userId);
        
        if (sessions.isEmpty()) {
            return Map.of();
        }

        return sessions.stream()
                .filter(Session::isSent) // Only sent routes
                .collect(Collectors.groupingBy(Session::getDiscipline,
                        Collectors.averagingDouble(session -> session.getGrade().getNumericValue())));
    }
}
