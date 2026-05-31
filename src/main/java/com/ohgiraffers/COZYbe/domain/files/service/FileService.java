package com.ohgiraffers.COZYbe.domain.files.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Service
public class FileService {

    private static final String FALLBACK_DEFAULT_IMAGE_KEY = "profile_images/Default_Profile.png";

    private final LocalFileStorageService fileStorageService;

    @Value("${app.profile.default-image-key:" + FALLBACK_DEFAULT_IMAGE_KEY + "}")
    private String defaultProfileImageKey;

    public FileService(LocalFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public String getDefaultProfileImageDir(){
        return defaultProfileImageKey;
    }

    public String getProfileImageUrl(String keyOrUrl) {
        if (keyOrUrl == null || keyOrUrl.isBlank()) {
            return null;
        }
        return fileStorageService.getPublicUrl(keyOrUrl);
    }

    public String saveProfileImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return fileStorageService.upload(file);
    }
}
