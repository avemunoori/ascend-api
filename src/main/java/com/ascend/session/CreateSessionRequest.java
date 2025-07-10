package com.ascend.session;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.AssertTrue;

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

    @AssertTrue(message = "Grade is not compatible with the selected discipline")
    public boolean isGradeCompatibleWithDiscipline() {
        if (discipline == null || grade == null) {
            return true; // Let @NotNull handle null validation
        }
        return grade.supportsDiscipline(discipline);
    }
}
