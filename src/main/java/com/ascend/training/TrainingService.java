package com.ascend.training;

import com.ascend.training.dto.*;
import com.ascend.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainingService {

    private final TrainingPlanTemplateRepository templateRepository;
    private final UserTrainingPlanRepository userPlanRepository;
    private final UserTrainingSessionRepository userSessionRepository;

    public List<TrainingPlanTemplateDto> getAvailableTemplates() {
        List<TrainingPlanTemplate> templates = templateRepository.findByIsActiveTrue();
        return templates.stream()
                .map(this::convertToTemplateDto)
                .collect(Collectors.toList());
    }

    public List<TrainingPlanTemplateDto> getTemplatesByDifficulty(String difficulty) {
        List<TrainingPlanTemplate> templates = templateRepository.findByIsActiveTrueAndDifficulty(difficulty);
        return templates.stream()
                .map(this::convertToTemplateDto)
                .collect(Collectors.toList());
    }

    public List<TrainingPlanTemplateDto> getTemplatesByCategory(String category) {
        List<TrainingPlanTemplate> templates = templateRepository.findByIsActiveTrueAndCategory(category);
        return templates.stream()
                .map(this::convertToTemplateDto)
                .collect(Collectors.toList());
    }

    public UserTrainingPlanDto startPlan(User user, StartPlanRequest request) {
        UUID templateId = UUID.fromString(request.getTemplateId());
        TrainingPlanTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Training plan template not found"));

        // Check if user already has an active plan
        if (userPlanRepository.existsByUserAndStatus(user, UserTrainingPlan.PlanStatus.ACTIVE)) {
            throw new RuntimeException("User already has an active training plan");
        }

        // Create user training plan
        UserTrainingPlan userPlan = UserTrainingPlan.builder()
                .user(user)
                .template(template)
                .name(template.getName())
                .description(template.getDescription())
                .status(UserTrainingPlan.PlanStatus.ACTIVE)
                .currentWeek(1)
                .currentSession(1)
                .startedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();

        userPlan = userPlanRepository.save(userPlan);

        // Create user training weeks and sessions
        createUserTrainingWeeksAndSessions(userPlan, template);

        return convertToUserPlanDto(userPlan);
    }

    public List<UserTrainingPlanDto> getUserPlans(User user) {
        List<UserTrainingPlan> plans = userPlanRepository.findByUserOrderByStartedAtDesc(user);
        return plans.stream()
                .map(this::convertToUserPlanDto)
                .collect(Collectors.toList());
    }

    public Optional<UserTrainingPlanDto> getActivePlan(User user) {
        Optional<UserTrainingPlan> activePlan = userPlanRepository.findActivePlanByUser(user);
        return activePlan.map(this::convertToUserPlanDto);
    }

    public UserTrainingPlanDto getPlanDetails(User user, UUID planId) {
        UserTrainingPlan plan = userPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Training plan not found"));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToUserPlanDto(plan);
    }

    public UserTrainingPlanDto completeSession(User user, UUID planId, CompleteSessionRequest request) {
        UserTrainingPlan plan = userPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Training plan not found"));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (plan.getStatus() != UserTrainingPlan.PlanStatus.ACTIVE) {
            throw new RuntimeException("Cannot complete session for inactive plan");
        }

        // Find and complete the session
        Optional<UserTrainingSession> sessionOpt = userSessionRepository
                .findByPlanIdAndSessionNumber(planId, request.getSessionNumber());

        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session not found");
        }

        UserTrainingSession session = sessionOpt.get();
        session.setStatus(UserTrainingSession.SessionStatus.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());
        session.setActualDurationMinutes(request.getActualDurationMinutes());
        session.setNotes(request.getNotes());

        userSessionRepository.save(session);

        // Update plan progress
        updatePlanProgress(plan);

        return convertToUserPlanDto(plan);
    }

    public UserTrainingPlanDto pausePlan(User user, UUID planId) {
        UserTrainingPlan plan = userPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Training plan not found"));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (plan.getStatus() != UserTrainingPlan.PlanStatus.ACTIVE) {
            throw new RuntimeException("Plan is not active");
        }

        plan.setStatus(UserTrainingPlan.PlanStatus.PAUSED);
        plan.setPausedAt(LocalDateTime.now());
        plan.setLastActivityAt(LocalDateTime.now());

        userPlanRepository.save(plan);

        return convertToUserPlanDto(plan);
    }

    public UserTrainingPlanDto resumePlan(User user, UUID planId) {
        UserTrainingPlan plan = userPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Training plan not found"));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (plan.getStatus() != UserTrainingPlan.PlanStatus.PAUSED) {
            throw new RuntimeException("Plan is not paused");
        }

        plan.setStatus(UserTrainingPlan.PlanStatus.ACTIVE);
        plan.setPausedAt(null);
        plan.setLastActivityAt(LocalDateTime.now());

        userPlanRepository.save(plan);

        return convertToUserPlanDto(plan);
    }

    private void createUserTrainingWeeksAndSessions(UserTrainingPlan userPlan, TrainingPlanTemplate template) {
        // This would create all the user-specific weeks and sessions based on the template
        // Implementation would depend on how you want to structure the data
        log.info("Creating user training weeks and sessions for plan: {}", userPlan.getId());
    }

    private void updatePlanProgress(UserTrainingPlan plan) {
        // Update current week and session based on completed sessions
        // This is a simplified implementation
        plan.setLastActivityAt(LocalDateTime.now());
        userPlanRepository.save(plan);
    }

    private TrainingPlanTemplateDto convertToTemplateDto(TrainingPlanTemplate template) {
        return TrainingPlanTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .totalWeeks(template.getTotalWeeks())
                .sessionsPerWeek(template.getSessionsPerWeek())
                .difficulty(template.getDifficulty())
                .category(template.getCategory())
                .isActive(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .build();
    }

    private UserTrainingPlanDto convertToUserPlanDto(UserTrainingPlan plan) {
        long completedSessions = userSessionRepository.countCompletedSessionsByPlanId(plan.getId());
        long totalSessions = userSessionRepository.countTotalSessionsByPlanId(plan.getId());
        double progressPercentage = totalSessions > 0 ? (double) completedSessions / totalSessions * 100 : 0;

        return UserTrainingPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .status(plan.getStatus().name())
                .currentWeek(plan.getCurrentWeek())
                .currentSession(plan.getCurrentSession())
                .startedAt(plan.getStartedAt())
                .pausedAt(plan.getPausedAt())
                .completedAt(plan.getCompletedAt())
                .lastActivityAt(plan.getLastActivityAt())
                .template(convertToTemplateDto(plan.getTemplate()))
                .completedSessions(completedSessions)
                .totalSessions(totalSessions)
                .progressPercentage(progressPercentage)
                .build();
    }
} 