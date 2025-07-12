package com.ascend.training;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_training_exercises")
public class UserTrainingExercise {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_session_id", nullable = false)
    private UserTrainingSession userSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_template_id", nullable = false)
    private TrainingExerciseTemplate exerciseTemplate;

    @Column(nullable = false)
    private Integer orderInSession;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExerciseStatus status; // NOT_STARTED, IN_PROGRESS, COMPLETED

    @Column(name = "actual_sets")
    private Integer actualSets;

    @Column(name = "actual_reps")
    private Integer actualReps;

    @Column(name = "actual_duration_seconds")
    private Integer actualDurationSeconds;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum ExerciseStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
} 