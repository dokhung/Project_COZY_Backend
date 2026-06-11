package com.ohgiraffers.COZYbe.domain.personal.repository;

import com.ohgiraffers.COZYbe.domain.personal.entity.PersonalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

public interface PersonalScheduleRepository extends JpaRepository<PersonalSchedule, UUID> {
    List<PersonalSchedule> findAllByUser_UserIdOrderByStartAtAsc(UUID userId);
    Optional<PersonalSchedule> findByScheduleIdAndUser_UserId(UUID scheduleId, UUID userId);
    Optional<PersonalSchedule> findFirstByUser_UserIdAndStartAtLessThanEqualAndEndAtGreaterThanEqualOrderByStartAtDesc(
            UUID userId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );
}
