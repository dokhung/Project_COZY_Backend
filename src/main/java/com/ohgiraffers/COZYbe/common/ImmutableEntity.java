package com.ohgiraffers.COZYbe.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Immutable
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class ImmutableEntity {

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

}
