package com.ascend.training;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingDataSeeder implements CommandLineRunner {

    private final TrainingPlanTemplateRepository templateRepository;

    @Override
    public void run(String... args) throws Exception {
        if (templateRepository.count() == 0) {
            log.info("Seeding training plan templates...");
            seedTrainingPlanTemplates();
            log.info("Training plan templates seeded successfully!");
        } else {
            log.info("Training plan templates already exist, skipping seeding.");
        }
    }

    private void seedTrainingPlanTemplates() {
        // Beginner Strength Plan
        TrainingPlanTemplate beginnerStrength = TrainingPlanTemplate.builder()
                .name("Beginner Strength Foundation")
                .description("A 4-week program designed to build fundamental climbing strength for beginners.")
                .totalWeeks(4)
                .sessionsPerWeek(3)
                .difficulty("BEGINNER")
                .category("STRENGTH")
                .isActive(true)
                .build();

        // Intermediate Endurance Plan
        TrainingPlanTemplate intermediateEndurance = TrainingPlanTemplate.builder()
                .name("Intermediate Endurance Builder")
                .description("A 6-week program focused on building climbing endurance and stamina.")
                .totalWeeks(6)
                .sessionsPerWeek(4)
                .difficulty("INTERMEDIATE")
                .category("ENDURANCE")
                .isActive(true)
                .build();

        // Advanced Technique Plan
        TrainingPlanTemplate advancedTechnique = TrainingPlanTemplate.builder()
                .name("Advanced Technique Mastery")
                .description("An 8-week program for advanced climbers to refine technique and movement skills.")
                .totalWeeks(8)
                .sessionsPerWeek(5)
                .difficulty("ADVANCED")
                .category("TECHNIQUE")
                .isActive(true)
                .build();

        // Beginner Technique Plan
        TrainingPlanTemplate beginnerTechnique = TrainingPlanTemplate.builder()
                .name("Beginner Technique Basics")
                .description("A 3-week program introducing fundamental climbing techniques and movement patterns.")
                .totalWeeks(3)
                .sessionsPerWeek(2)
                .difficulty("BEGINNER")
                .category("TECHNIQUE")
                .isActive(true)
                .build();

        // Intermediate Strength Plan
        TrainingPlanTemplate intermediateStrength = TrainingPlanTemplate.builder()
                .name("Intermediate Power Development")
                .description("A 5-week program focused on building climbing-specific power and strength.")
                .totalWeeks(5)
                .sessionsPerWeek(3)
                .difficulty("INTERMEDIATE")
                .category("STRENGTH")
                .isActive(true)
                .build();

        List<TrainingPlanTemplate> templates = Arrays.asList(
                beginnerStrength,
                intermediateEndurance,
                advancedTechnique,
                beginnerTechnique,
                intermediateStrength
        );

        templateRepository.saveAll(templates);
    }
} 