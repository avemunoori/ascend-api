package com.ascend.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByUserId(UUID userId);
    List<Session> findByUserIdAndDiscipline(UUID userId, SessionDiscipline discipline);
    List<Session> findByUserIdAndDate(UUID userId, java.time.LocalDate date);
}
