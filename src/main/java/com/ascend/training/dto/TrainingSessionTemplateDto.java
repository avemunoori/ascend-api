package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSessionTemplateDto {
    private UUID id;
    private Integer sessionNumber;
    private String name;
    private String description;
    private Integer estimatedDurationMinutes;
    private String sessionType;
    private List<TrainingExerciseTemplateDto> exercises;
} 