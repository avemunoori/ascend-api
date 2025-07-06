package com.ascend.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByUserId(UUID id);
    List<Session> findByUserIdAndDiscipline(UUID id, SessionDiscipline discipline);
    List<Session> findByUserIdAndDate(UUID id, java.time.LocalDate date);
}
