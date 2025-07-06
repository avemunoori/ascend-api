package com.ascend.session;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class ProgressAnalytics {
    private int totalSessions;
    private double sentRate;
    private double avgDifficulty;
    private List<WeeklyProgress> progressByWeek;
    private List<MonthlyProgress> progressByMonth;
    
    @Data
    @Builder
    public static class WeeklyProgress {
        private String week; // Format: "2025-W25"
        private double avgDifficulty;
        private int sessionCount;
        private double sentRate;
    }
    
    @Data
    @Builder
    public static class MonthlyProgress {
        private String month; // Format: "2025-01"
        private double avgDifficulty;
        private int sessionCount;
        private double sentRate;
    }
} 