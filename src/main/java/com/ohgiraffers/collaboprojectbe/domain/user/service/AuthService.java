package com.ohgiraffers.collaboprojectbe.domain.user.service;

import com.ohgiraffers.collaboprojectbe.domain.user.dto.SignUpDTO;
import com.ohgiraffers.collaboprojectbe.domain.user.dto.UserUpdateDTO;
import com.ohgiraffers.collaboprojectbe.domain.user.entity.User;
import com.ohgiraffers.collaboprojectbe.domain.user.repository.UserRepository;
import com.ohgiraffers.collaboprojectbe.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/profile_images/";
    private static final String SERVER_URL = "http://localhost:8080/"; // ✅ 클라이언트에서 접근 가능하게 설정

    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // ✅ JWT 생성
        String token = jwtTokenProvider.createToken(user.getEmail());
        System.out.println("🔑 생성된 JWT: " + token);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return response;
    }


    // 🔹 회원가입 처리
    public User register(SignUpDTO signUpDTO, MultipartFile profileImage) throws IOException {
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = saveProfileImage(profileImage);
        }

        User user = new User();
        user.setEmail(signUpDTO.getEmail());
        user.setNickname(signUpDTO.getNickname());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setProfileImageUrl(profileImageUrl);

        return userRepository.save(user);
    }

    // 🔹 프로필 이미지 저장 (서버 URL 반환)
    private String saveProfileImage(MultipartFile file) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");
        }

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + fileExtension;

        Path filePath = Path.of(UPLOAD_DIR, newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return SERVER_URL + UPLOAD_DIR + newFileName; // ✅ 전체 URL 반환
    }

    // 🔹 현재 로그인된 사용자 정보 가져오기
    public User getUserInfo(String token) {
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 🔹 이메일 중복 확인
    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean verifyPassword(String email, String inputPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("❌ 사용자를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        System.out.println("🔍 입력된 비밀번호: " + inputPassword);
        System.out.println("🔍 저장된 해시된 비밀번호: " + user.getPassword());

        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            System.out.println("❌ 비밀번호가 일치하지 않음");
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        System.out.println("✅ 비밀번호 검증 성공");
        return true;
    }

    public User updateUserInfo(String email, UserUpdateDTO userUpdateDTO, MultipartFile profileImage) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        user.setNickname(userUpdateDTO.getNickname());
        user.setStatusMessage(userUpdateDTO.getStatusMessage());

        // ✅ 기존 이미지 삭제 후 새로운 이미지 저장
        if (profileImage != null && !profileImage.isEmpty()) {
            if (user.getProfileImageUrl() != null) {
                File oldFile = new File(user.getProfileImageUrl());
                if (oldFile.exists()) {
                    oldFile.delete();  // 기존 이미지 삭제
                }
            }

            String profileImageUrl = saveProfileImage(profileImage);
            user.setProfileImageUrl(profileImageUrl);
        }

        return userRepository.save(user);
    }



    // 🔹 JWT에서 이메일 추출 (사용자 이메일 가져오기)
    public String getEmailFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // 현재 로그인된 사용자의 이메일 반환
        }
        return null;
    }

    private Set<String> invalidatedTokens = new HashSet<>(); // 🚀 블랙리스트 (무효화된 토큰 저장)

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public boolean isTokenValid(String token) {
        return !invalidatedTokens.contains(token); // 🔹 블랙리스트에 없으면 유효한 토큰
    }


}
