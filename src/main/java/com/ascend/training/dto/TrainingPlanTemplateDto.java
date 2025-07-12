package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingPlanTemplateDto {
    private UUID id;
    private String name;
    private String description;
    private Integer totalWeeks;
    private Integer sessionsPerWeek;
    private String difficulty;
    private String category;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<TrainingWeekTemplateDto> weeks;
} 