package com.ohgiraffers.COZYbe.domain.user.domain.service;

import com.ohgiraffers.COZYbe.domain.user.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserDomainService {

    private final UserRepository repository;
}
