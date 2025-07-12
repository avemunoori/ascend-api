package com.ascend.training;

import com.ascend.training.UserTrainingWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTrainingSessionRepository extends JpaRepository<UserTrainingSession, UUID> {
    
    List<UserTrainingSession> findByUserWeekOrderBySessionNumber(UserTrainingWeek userWeek);
    
    Optional<UserTrainingSession> findByUserWeekAndSessionNumber(UserTrainingWeek userWeek, Integer sessionNumber);
    
    @Query("SELECT uts FROM UserTrainingSession uts WHERE uts.userWeek.userPlan.id = :planId AND uts.sessionNumber = :sessionNumber")
    Optional<UserTrainingSession> findByPlanIdAndSessionNumber(@Param("planId") UUID planId, @Param("sessionNumber") Integer sessionNumber);
    
    @Query("SELECT COUNT(uts) FROM UserTrainingSession uts WHERE uts.userWeek.userPlan.id = :planId AND uts.status = 'COMPLETED'")
    long countCompletedSessionsByPlanId(@Param("planId") UUID planId);
    
    @Query("SELECT COUNT(uts) FROM UserTrainingSession uts WHERE uts.userWeek.userPlan.id = :planId")
    long countTotalSessionsByPlanId(@Param("planId") UUID planId);
} 