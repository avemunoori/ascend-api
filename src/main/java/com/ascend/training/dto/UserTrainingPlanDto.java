package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTrainingPlanDto {
    private UUID id;
    private String name;
    private String description;
    private String status;
    private Integer currentWeek;
    private Integer currentSession;
    private LocalDateTime startedAt;
    private LocalDateTime pausedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastActivityAt;
    private TrainingPlanTemplateDto template;
    private List<UserTrainingWeekDto> weeks;
    private Long completedSessions;
    private Long totalSessions;
    private Double progressPercentage;
} 