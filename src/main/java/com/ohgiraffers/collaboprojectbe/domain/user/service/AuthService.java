package com.ohgiraffers.collaboprojectbe.domain.user.service;

import com.ohgiraffers.collaboprojectbe.domain.user.dto.SignUpDTO;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    private static final String UPLOAD_DIR = "uploads/profile_images/";

    // 로그인 로직
    public Map<String, Object> login(String email, String password) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        if (currentAuth != null && currentAuth.isAuthenticated() && !"anonymousUser".equals(currentAuth.getName())) {
            System.out.println("로그인한 유저의 아이디 : " + currentAuth.getName());
            throw new IllegalStateException("로그인이 성공하였습니다.");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("성공적인 로그인 : " + user.getEmail());

                String token = jwtTokenProvider.createToken(user.getEmail());
                System.out.println("생성된 토큰의 코드 :: " + token);

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user);
                return response;
            }
            else {
                System.out.println("사용자에게 잘못된 비밀번호" + email);
                throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
            }
        }else {
            System.out.println("알수가 없는 이메일 입니다." + email);
            throw new IllegalArgumentException("잘못된 이메일 입니다.");
        }
    }


    public User register(SignUpDTO signUpDTO, MultipartFile profileImage) throws IOException {
        String profileImageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = saveProfileImage(profileImage); // 이미지 저장 후 파일 경로 반환
        }

        User user = new User();
        user.setEmail(signUpDTO.getEmail());
        user.setNickname(signUpDTO.getNickname());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setProfileImageUrl(profileImageUrl); // **DB에 파일 경로 저장**

        return userRepository.save(user);
    }

    private String saveProfileImage(MultipartFile file) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 디렉토리가 없으면 생성
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");
        }

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 확장자 추출
        String newFileName = UUID.randomUUID() + fileExtension; // 중복 방지를 위해 UUID 사용

        Path filePath = Path.of(UPLOAD_DIR, newFileName); // 저장할 파일 경로
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // 파일 저장

        return newFileName; // **DB에는 파일명만 저장**
    }

    public User getUserInfo(String userId) {
        return userRepository.findByEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean verifyPassword(String email, String inputPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return true;
    }

    public User updateUserInfo(String email, String newNickname, String newStatusMessage) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (newNickname != null && !newNickname.isEmpty()) {
                user.setNickname(newNickname);
            }
            if (newStatusMessage != null) {
                user.setStatusMessage(newStatusMessage);
            }

            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
    }



}
