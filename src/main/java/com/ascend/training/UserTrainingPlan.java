package com.ascend.training;

import com.ascend.user.User;
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
@Table(name = "user_training_plans")
public class UserTrainingPlan {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private TrainingPlanTemplate template;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanStatus status; // ACTIVE, PAUSED, COMPLETED, ABANDONED

    @Column(name = "current_week")
    private Integer currentWeek;

    @Column(name = "current_session")
    private Integer currentSession;

    @Column(name = "started_at", nullable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "paused_at")
    private LocalDateTime pausedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_activity_at")
    @Builder.Default
    private LocalDateTime lastActivityAt = LocalDateTime.now();

    @OneToMany(mappedBy = "userPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserTrainingWeek> weeks;

    public enum PlanStatus {
        ACTIVE, PAUSED, COMPLETED, ABANDONED
    }
} 