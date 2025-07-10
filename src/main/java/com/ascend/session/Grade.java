package com.ascend.session;

import lombok.Getter;

@Getter
public enum Grade {
    // V-Scale grades (for bouldering)
    V0(0.0, "V0", SessionDiscipline.BOULDER),
    V1(1.0, "V1", SessionDiscipline.BOULDER),
    V2(2.0, "V2", SessionDiscipline.BOULDER),
    V3(3.0, "V3", SessionDiscipline.BOULDER),
    V4(4.0, "V4", SessionDiscipline.BOULDER),
    V5(5.0, "V5", SessionDiscipline.BOULDER),
    V6(6.0, "V6", SessionDiscipline.BOULDER),
    V7(7.0, "V7", SessionDiscipline.BOULDER),
    V8(8.0, "V8", SessionDiscipline.BOULDER),
    V9(9.0, "V9", SessionDiscipline.BOULDER),
    V10(10.0, "V10", SessionDiscipline.BOULDER),
    V11(11.0, "V11", SessionDiscipline.BOULDER),
    V12(12.0, "V12", SessionDiscipline.BOULDER),
    V13(13.0, "V13", SessionDiscipline.BOULDER),
    V14(14.0, "V14", SessionDiscipline.BOULDER),
    V15(15.0, "V15", SessionDiscipline.BOULDER),
    V16(16.0, "V16", SessionDiscipline.BOULDER),
    V17(17.0, "V17", SessionDiscipline.BOULDER),

    // YDS grades (for lead and top rope climbing)
    YDS_5_6(6.0, "5.6", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_7(7.0, "5.7", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_8(8.0, "5.8", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_9(9.0, "5.9", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_10A(10.1, "5.10a", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_10B(10.2, "5.10b", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_10C(10.3, "5.10c", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_10D(10.4, "5.10d", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_11A(11.1, "5.11a", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_11B(11.2, "5.11b", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_11C(11.3, "5.11c", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_11D(11.4, "5.11d", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_12A(12.1, "5.12a", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_12B(12.2, "5.12b", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_12C(12.3, "5.12c", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_12D(12.4, "5.12d", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_13A(13.1, "5.13a", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_13B(13.2, "5.13b", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_13C(13.3, "5.13c", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_13D(13.4, "5.13d", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_14A(14.1, "5.14a", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_14B(14.2, "5.14b", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_14C(14.3, "5.14c", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_14D(14.4, "5.14d", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_15A(15.1, "5.15a", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_15B(15.2, "5.15b", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_15C(15.3, "5.15c", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE),
    YDS_5_15D(15.4, "5.15d", SessionDiscipline.LEAD, SessionDiscipline.TOP_ROPE);

    private final double numericValue;
    private final String displayValue;
    private final SessionDiscipline[] supportedDisciplines;

    Grade(double numericValue, String displayValue, SessionDiscipline... supportedDisciplines) {
        this.numericValue = numericValue;
        this.displayValue = displayValue;
        this.supportedDisciplines = supportedDisciplines;
    }

    public boolean supportsDiscipline(SessionDiscipline discipline) {
        for (SessionDiscipline supported : supportedDisciplines) {
            if (supported == discipline) {
                return true;
            }
        }
        return false;
    }

    public static Grade fromString(String gradeString) {
        for (Grade grade : values()) {
            if (grade.displayValue.equalsIgnoreCase(gradeString)) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Invalid grade: " + gradeString);
    }

    public static Grade fromStringForDiscipline(String gradeString, SessionDiscipline discipline) {
        for (Grade grade : values()) {
            if (grade.displayValue.equalsIgnoreCase(gradeString) && grade.supportsDiscipline(discipline)) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Invalid grade '" + gradeString + "' for discipline '" + discipline + "'. " +
                "Supported grades for " + discipline + ": " + getSupportedGradesForDiscipline(discipline));
    }

    public static Grade fromNumericValue(double numericValue, SessionDiscipline discipline) {
        for (Grade grade : values()) {
            if (grade.numericValue == numericValue && grade.supportsDiscipline(discipline)) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Invalid numeric value: " + numericValue + " for discipline: " + discipline);
    }

    private static String getSupportedGradesForDiscipline(SessionDiscipline discipline) {
        StringBuilder sb = new StringBuilder();
        for (Grade grade : values()) {
            if (grade.supportsDiscipline(discipline)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(grade.displayValue);
            }
        }
        return sb.toString();
    }
} 