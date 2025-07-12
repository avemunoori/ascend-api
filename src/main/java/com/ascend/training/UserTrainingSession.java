package com.ascend.training;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_training_sessions")
public class UserTrainingSession {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_week_id", nullable = false)
    private UserTrainingWeek userWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_template_id", nullable = false)
    private TrainingSessionTemplate sessionTemplate;

    @Column(nullable = false)
    private Integer sessionNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status; // NOT_STARTED, IN_PROGRESS, COMPLETED

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "userSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserTrainingExercise> exercises;

    public enum SessionStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
} 