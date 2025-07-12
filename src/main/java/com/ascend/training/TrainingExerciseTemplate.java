package com.ascend.training;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "training_exercise_templates")
public class TrainingExerciseTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_template_id", nullable = false)
    private TrainingSessionTemplate sessionTemplate;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String exerciseType; // e.g., "Pull-ups", "Push-ups", "Planks"

    @Column(nullable = false)
    private Integer orderInSession;

    @Column(nullable = false)
    private Integer sets;

    @Column(nullable = false)
    private Integer reps;

    @Column
    private Integer durationSeconds; // For time-based exercises

    @Column
    private Integer restSeconds;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column
    private String equipment; // e.g., "None", "Pull-up bar", "Resistance bands"
} 