package com.ascend.training;

import com.ascend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTrainingPlanRepository extends JpaRepository<UserTrainingPlan, UUID> {
    
    List<UserTrainingPlan> findByUserOrderByStartedAtDesc(User user);
    
    List<UserTrainingPlan> findByUserAndStatusOrderByStartedAtDesc(User user, UserTrainingPlan.PlanStatus status);
    
    Optional<UserTrainingPlan> findByUserAndStatus(User user, UserTrainingPlan.PlanStatus status);
    
    @Query("SELECT utp FROM UserTrainingPlan utp WHERE utp.user = :user AND utp.status IN ('ACTIVE', 'PAUSED') ORDER BY utp.startedAt DESC")
    List<UserTrainingPlan> findActivePlansByUser(@Param("user") User user);
    
    @Query("SELECT utp FROM UserTrainingPlan utp WHERE utp.user = :user AND utp.status = 'ACTIVE'")
    Optional<UserTrainingPlan> findActivePlanByUser(@Param("user") User user);
    
    boolean existsByUserAndStatus(User user, UserTrainingPlan.PlanStatus status);
} 