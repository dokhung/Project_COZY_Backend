package com.ohgiraffers.COZYbe.domain.inquiry.controller;

import com.ohgiraffers.COZYbe.domain.inquiry.dto.InquiryDTO;
import com.ohgiraffers.COZYbe.domain.inquiry.entity.Inquiry;
import com.ohgiraffers.COZYbe.domain.inquiry.service.InquiryService;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
// inquiry.tsx
public class InquiryController {

    private final InquiryService inquiryService;
    private final JwtTokenProvider jwtTokenProvider;

    // Get information from all inquiries.
    @GetMapping("/list")
    public List<Inquiry> getAll() {
        return inquiryService.findAll();
    }

    // Create Inquiry
    @PostMapping("/create")
    public Inquiry create(@RequestBody InquiryDTO request, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization").substring(7);
        String writer = jwtTokenProvider.decodeUserIdFromJwt(token);
        return inquiryService.createInquiry(
                request.getType(),
                request.getTitle(),
                request.getContent(),
                writer
        );
    }
}

