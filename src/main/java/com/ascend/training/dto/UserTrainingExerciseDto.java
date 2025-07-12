package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTrainingExerciseDto {
    private UUID id;
    private Integer orderInSession;
    private String status;
    private Integer actualSets;
    private Integer actualReps;
    private Integer actualDurationSeconds;
    private LocalDateTime completedAt;
    private String notes;
    private TrainingExerciseTemplateDto exerciseTemplate;
} 