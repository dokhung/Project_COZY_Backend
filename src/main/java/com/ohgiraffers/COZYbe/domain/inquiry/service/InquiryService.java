package com.ohgiraffers.COZYbe.domain.inquiry.service;

import com.ohgiraffers.COZYbe.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.COZYbe.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public List<Inquiry> findAll() {
        return inquiryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Inquiry createInquiry(String type, String title, String content, String writer) {
        Inquiry inquiry = Inquiry.builder()
                .type(type)
                .title(title)
                .content(content)
                .status("처리대기")
                .writer(writer)
                .createdAt(LocalDateTime.now())
                .build();
        return inquiryRepository.save(inquiry);
    }
}


