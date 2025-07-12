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
@Table(name = "training_plan_templates")
public class TrainingPlanTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer totalWeeks;

    @Column(nullable = false)
    private Integer sessionsPerWeek;

    @Column(nullable = false)
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(nullable = false)
    private String category; // STRENGTH, ENDURANCE, TECHNIQUE, etc.

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainingWeekTemplate> weeks;
} 