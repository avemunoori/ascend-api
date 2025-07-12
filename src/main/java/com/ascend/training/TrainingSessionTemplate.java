package com.ascend.training;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "training_session_templates")
public class TrainingSessionTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_template_id", nullable = false)
    private TrainingWeekTemplate weekTemplate;

    @Column(nullable = false)
    private Integer sessionNumber;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer estimatedDurationMinutes;

    @Column(nullable = false)
    private String sessionType; // e.g., "Warm-up", "Main Workout", "Cool-down"

    @OneToMany(mappedBy = "sessionTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainingExerciseTemplate> exercises;
} 