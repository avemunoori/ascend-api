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
@Table(name = "training_week_templates")
public class TrainingWeekTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private TrainingPlanTemplate template;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String focus; // e.g., "Strength", "Endurance", "Technique"

    @OneToMany(mappedBy = "weekTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainingSessionTemplate> sessions;
} 