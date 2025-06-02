package com.ohgiraffers.COZYbe.domain.teams.domain.repository;

import com.ohgiraffers.COZYbe.domain.teams.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

}
