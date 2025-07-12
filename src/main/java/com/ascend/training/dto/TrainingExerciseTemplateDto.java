package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingExerciseTemplateDto {
    private UUID id;
    private String name;
    private String description;
    private String exerciseType;
    private Integer orderInSession;
    private Integer sets;
    private Integer reps;
    private Integer durationSeconds;
    private Integer restSeconds;
    private String instructions;
    private String equipment;
} 