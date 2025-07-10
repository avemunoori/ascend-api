package com.ascend.session;

public class GradeUtils {
    
    /**
     * Convert numeric value back to display string for a given discipline
     */
    public static String numericToDisplay(double numericValue, SessionDiscipline discipline) {
        try {
            Grade grade = Grade.fromNumericValue(numericValue, discipline);
            return grade.getDisplayValue();
        } catch (IllegalArgumentException e) {
            // Fallback for edge cases
            if (discipline == SessionDiscipline.BOULDER) {
                return "V" + (int) numericValue;
            } else {
                return String.format("%.1f", numericValue);
            }
        }
    }
    
    /**
     * Get all available grades for a discipline
     */
    public static Grade[] getGradesForDiscipline(SessionDiscipline discipline) {
        return java.util.Arrays.stream(Grade.values())
                .filter(grade -> grade.supportsDiscipline(discipline))
                .toArray(Grade[]::new);
    }
    
    /**
     * Check if a grade string is valid for a discipline
     */
    public static boolean isValidGrade(String gradeString, SessionDiscipline discipline) {
        try {
            Grade grade = Grade.fromString(gradeString);
            return grade.supportsDiscipline(discipline);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
} 