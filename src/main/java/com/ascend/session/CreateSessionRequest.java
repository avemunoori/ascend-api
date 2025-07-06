package com.ascend.session;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class CreateSessionRequest {
    @NotNull(message = "Discipline is required")
    private SessionDiscipline discipline;
    
    @NotNull(message = "Grade is required")
    private Grade grade;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    private String notes;
    
    @NotNull(message = "Sent status is required")
    private boolean sent;
}
