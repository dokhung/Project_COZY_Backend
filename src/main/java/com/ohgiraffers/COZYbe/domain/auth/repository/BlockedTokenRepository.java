package com.ohgiraffers.COZYbe.domain.auth.repository;

import com.ohgiraffers.COZYbe.domain.auth.entity.BlockedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedTokenRepository extends CrudRepository<BlockedToken, String> {
}
