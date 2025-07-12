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
@Table(name = "user_training_weeks")
public class UserTrainingWeek {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_plan_id", nullable = false)
    private UserTrainingPlan userPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_template_id", nullable = false)
    private TrainingWeekTemplate weekTemplate;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WeekStatus status; // NOT_STARTED, IN_PROGRESS, COMPLETED

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "userWeek", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserTrainingSession> sessions;

    public enum WeekStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
} 