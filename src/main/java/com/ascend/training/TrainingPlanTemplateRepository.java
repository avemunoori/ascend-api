package com.ascend.training;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingPlanTemplateRepository extends JpaRepository<TrainingPlanTemplate, UUID> {
    
    List<TrainingPlanTemplate> findByIsActiveTrue();
    
    List<TrainingPlanTemplate> findByIsActiveTrueAndDifficulty(String difficulty);
    
    List<TrainingPlanTemplate> findByIsActiveTrueAndCategory(String category);
    
    @Query("SELECT DISTINCT t.category FROM TrainingPlanTemplate t WHERE t.isActive = true")
    List<String> findDistinctCategories();
    
    @Query("SELECT DISTINCT t.difficulty FROM TrainingPlanTemplate t WHERE t.isActive = true")
    List<String> findDistinctDifficulties();
} 