package com.ascend.training.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteSessionRequest {
    
    @NotNull(message = "Session number is required")
    private Integer sessionNumber;
    
    private Integer actualDurationMinutes;
    
    private String notes;
} 