package com.ohgiraffers.COZYbe.domain.inquiry.repository;

import com.ohgiraffers.COZYbe.domain.inquiry.entity.Inquiry;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry,Long> {
    List<Inquiry> findByType(String type, Sort sort);
}
