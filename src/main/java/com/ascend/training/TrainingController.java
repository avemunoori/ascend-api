package com.ascend.training;

import com.ascend.training.dto.*;
import com.ascend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
@Slf4j
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping("/templates")
    public ResponseEntity<List<TrainingPlanTemplateDto>> getTemplates(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String category) {
        
        List<TrainingPlanTemplateDto> templates;
        
        if (difficulty != null) {
            templates = trainingService.getTemplatesByDifficulty(difficulty);
        } else if (category != null) {
            templates = trainingService.getTemplatesByCategory(category);
        } else {
            templates = trainingService.getAvailableTemplates();
        }
        
        return ResponseEntity.ok(templates);
    }

    @PostMapping("/user-plans")
    public ResponseEntity<?> startPlan(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StartPlanRequest request) {
        
        try {
            UserTrainingPlanDto plan = trainingService.startPlan(user, request);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            log.error("Error starting plan for user {}: {}", user.getId(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/user-plans")
    public ResponseEntity<List<UserTrainingPlanDto>> getUserPlans(
            @AuthenticationPrincipal User user) {
        
        List<UserTrainingPlanDto> plans = trainingService.getUserPlans(user);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/user-plans/active")
    public ResponseEntity<UserTrainingPlanDto> getActivePlan(
            @AuthenticationPrincipal User user) {
        
        Optional<UserTrainingPlanDto> activePlan = trainingService.getActivePlan(user);
        return activePlan.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user-plans/{planId}")
    public ResponseEntity<UserTrainingPlanDto> getPlanDetails(
            @AuthenticationPrincipal User user,
            @PathVariable String planId) {
        
        try {
            UUID planUuid = UUID.fromString(planId);
            UserTrainingPlanDto plan = trainingService.getPlanDetails(user, planUuid);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            log.error("Invalid plan ID format: {}", planId);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error getting plan details for user {}: {}", user.getId(), e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user-plans/{planId}/sessions/{sessionId}/complete")
    public ResponseEntity<UserTrainingPlanDto> completeSession(
            @AuthenticationPrincipal User user,
            @PathVariable String planId,
            @PathVariable String sessionId,
            @Valid @RequestBody CompleteSessionRequest request) {
        
        try {
            UUID planUuid = UUID.fromString(planId);
            Integer sessionNumber = Integer.parseInt(sessionId);
            
            // Validate that session number matches request
            if (!sessionNumber.equals(request.getSessionNumber())) {
                return ResponseEntity.badRequest().build();
            }
            
            UserTrainingPlanDto plan = trainingService.completeSession(user, planUuid, request);
            return ResponseEntity.ok(plan);
        } catch (NumberFormatException e) {
            log.error("Invalid session ID format: {}", sessionId);
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid plan ID format: {}", planId);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error completing session for user {}: {}", user.getId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user-plans/{planId}/pause")
    public ResponseEntity<UserTrainingPlanDto> pausePlan(
            @AuthenticationPrincipal User user,
            @PathVariable String planId) {
        
        try {
            UUID planUuid = UUID.fromString(planId);
            UserTrainingPlanDto plan = trainingService.pausePlan(user, planUuid);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            log.error("Invalid plan ID format: {}", planId);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error pausing plan for user {}: {}", user.getId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user-plans/{planId}/resume")
    public ResponseEntity<UserTrainingPlanDto> resumePlan(
            @AuthenticationPrincipal User user,
            @PathVariable String planId) {
        
        try {
            UUID planUuid = UUID.fromString(planId);
            UserTrainingPlanDto plan = trainingService.resumePlan(user, planUuid);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            log.error("Invalid plan ID format: {}", planId);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error resuming plan for user {}: {}", user.getId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user-plans/{planId}/progress")
    public ResponseEntity<UserTrainingPlanDto> getPlanProgress(
            @AuthenticationPrincipal User user,
            @PathVariable String planId) {
        
        try {
            UUID planUuid = UUID.fromString(planId);
            UserTrainingPlanDto plan = trainingService.getPlanDetails(user, planUuid);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            log.error("Invalid plan ID format: {}", planId);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error getting plan progress for user {}: {}", user.getId(), e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
} 