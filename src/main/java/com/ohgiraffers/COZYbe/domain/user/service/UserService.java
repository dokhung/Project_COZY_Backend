package com.ohgiraffers.COZYbe.domain.user.service;

import com.ohgiraffers.COZYbe.domain.user.dto.SignUpDTO;
import com.ohgiraffers.COZYbe.domain.user.dto.UserUpdateDTO;
import com.ohgiraffers.COZYbe.domain.user.entity.User;
import com.ohgiraffers.COZYbe.domain.user.repository.UserRepository;
import com.ohgiraffers.COZYbe.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/profile_images/";
    private static final String SERVER_URL = "http://localhost:8080/";


    // TODO: 회원가입
    public User register(SignUpDTO signUpDTO, MultipartFile profileImage) throws IOException {
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = saveProfileImage(profileImage);
        }

        User user = User.builder()
                .email(signUpDTO.getEmail())
                .nickname(signUpDTO.getNickname())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .profileImageUrl(profileImageUrl)
                .build();

        return userRepository.save(user);
    }

    // TODO : 프로필 이미지 저장
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

        return SERVER_URL + UPLOAD_DIR + newFileName;
    }


    public User getUserInfo(String userId) {
//        String userEmail = jwtTokenProvider.decodeUserIdFromJwt(userId);
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 🔹 이메일 중복 확인
    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean verifyPassword(String userId, String inputPassword) {
        Optional<User> userOptional = userRepository.findById(UUID.fromString(userId));

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return true;
    }

    public User updateUserInfo(String userId, UserUpdateDTO userUpdateDTO, MultipartFile profileImage) throws IOException {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        user.setNickname(userUpdateDTO.getNickname());
        user.setStatusMessage(userUpdateDTO.getStatusMessage());

        if (profileImage != null && !profileImage.isEmpty()) {
            if (user.getProfileImageUrl() != null) {
                File oldFile = new File(user.getProfileImageUrl());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            String profileImageUrl = saveProfileImage(profileImage);
            user.setProfileImageUrl(profileImageUrl);
        }

        return userRepository.save(user);
    }



}
