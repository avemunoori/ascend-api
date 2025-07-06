package com.ascend.session;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateSessionRequest {
    private SessionDiscipline discipline;
    private Grade grade;
    private LocalDate date;
    private String notes;
    private Boolean sent;
} 