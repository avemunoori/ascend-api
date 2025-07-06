package com.ascend.session;

import lombok.Data;
import lombok.Builder;

import java.util.Map;

@Data
@Builder
public class SessionAnalytics {
    private int totalSessions;
    private double averageDifficulty;
    private double sentPercentage;
    private Map<SessionDiscipline, Integer> sessionsByDiscipline;
    private Map<SessionDiscipline, Double> averageDifficultyByDiscipline;
    private Map<SessionDiscipline, Double> sentPercentageByDiscipline;
} 