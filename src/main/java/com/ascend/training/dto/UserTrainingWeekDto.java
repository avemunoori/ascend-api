package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTrainingWeekDto {
    private UUID id;
    private Integer weekNumber;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private TrainingWeekTemplateDto weekTemplate;
    private List<UserTrainingSessionDto> sessions;
} 