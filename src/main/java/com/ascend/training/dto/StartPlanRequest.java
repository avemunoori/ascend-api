package com.ascend.training.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartPlanRequest {
    
    @NotNull(message = "Template ID is required")
    private String templateId;
} 