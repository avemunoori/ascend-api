package com.ascend.training.dto;

import lombok.*;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingWeekTemplateDto {
    private UUID id;
    private Integer weekNumber;
    private String description;
    private String focus;
    private List<TrainingSessionTemplateDto> sessions;
} 