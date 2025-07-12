package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTrainingSessionDto {
    private UUID id;
    private Integer sessionNumber;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer actualDurationMinutes;
    private String notes;
    private TrainingSessionTemplateDto sessionTemplate;
    private List<UserTrainingExerciseDto> exercises;
} 