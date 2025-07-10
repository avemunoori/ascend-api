package com.ascend.session;

import com.ascend.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Session> createSession(@Valid @RequestBody CreateSessionRequest request,
                                 @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        Session session = sessionService.createSession(userId, request);
        return ResponseEntity.ok(session);
    }

    @GetMapping
    public ResponseEntity<List<Session>> getUserSessions(
            @RequestParam(required = false) SessionDiscipline discipline,
            @RequestParam(required = false) LocalDate date,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        
        List<Session> sessions;
        if (discipline != null && date != null) {
            // Filter by both discipline and date
            sessions = sessionService.getAllSessions(userId).stream()
                    .filter(session -> session.getDiscipline() == discipline && session.getDate().equals(date))
                    .collect(Collectors.toList());
        } else if (discipline != null) {
            sessions = sessionService.getSessionsByDiscipline(userId, discipline);
        } else if (date != null) {
            sessions = sessionService.getSessionsByDate(userId, date);
        } else {
            sessions = sessionService.getAllSessions(userId);
        }
        
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<Session> getSessionById(
            @PathVariable UUID sessionId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        Session session = sessionService.getSessionById(sessionId, userId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/discipline/{discipline}")
    public ResponseEntity<List<Session>> getSessionsByDiscipline(
            @PathVariable SessionDiscipline discipline,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        List<Session> sessions = sessionService.getSessionsByDiscipline(userId, discipline);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Session>> getSessionsByDate(
            @PathVariable LocalDate date,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        List<Session> sessions = sessionService.getSessionsByDate(userId, date);
        return ResponseEntity.ok(sessions);
    }

    @PatchMapping("/{sessionId}")
    public ResponseEntity<Session> updateSession(
            @PathVariable UUID sessionId,
            @RequestBody UpdateSessionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        Session session = sessionService.updateSession(sessionId, userId, request);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<Session> replaceSession(
            @PathVariable UUID sessionId,
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        Session session = sessionService.replaceSession(sessionId, userId, request);
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID sessionId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        sessionService.deleteSession(sessionId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<SessionAnalytics> getAnalytics(
            @RequestParam(required = false) String period,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        SessionAnalytics analytics = sessionService.getAnalytics(userId, period);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/stats/overview")
    public ResponseEntity<SessionAnalytics> getStatsOverview(
            @RequestParam(required = false) String period,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        SessionAnalytics analytics = sessionService.getAnalytics(userId, period);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/stats/progress")
    public ResponseEntity<ProgressAnalytics> getProgressStats(
            @RequestParam(required = false) String period,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        ProgressAnalytics progress = sessionService.getProgressAnalytics(userId, period);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/stats/highest")
    public ResponseEntity<Map<SessionDiscipline, Grade>> getHighestGrades(
            @RequestParam(required = false) String period,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        Map<SessionDiscipline, Grade> highestGrades = sessionService.getHighestGrades(userId, period);
        return ResponseEntity.ok(highestGrades);
    }

    @GetMapping("/stats/average")
    public ResponseEntity<Map<SessionDiscipline, Double>> getAverageGrades(
            @RequestParam(required = false) String period,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtService.validateToken(token);
        Map<SessionDiscipline, Double> averageGrades = sessionService.getAverageGrades(userId, period);
        return ResponseEntity.ok(averageGrades);
    }

    @GetMapping("/grades/{discipline}")
    public ResponseEntity<Grade[]> getGradesForDiscipline(@PathVariable SessionDiscipline discipline) {
        Grade[] grades = GradeUtils.getGradesForDiscipline(discipline);
        return ResponseEntity.ok(grades);
    }
}
