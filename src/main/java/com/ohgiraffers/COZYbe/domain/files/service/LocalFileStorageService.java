package com.ohgiraffers.COZYbe.domain.files.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService {

    private static final String PROFILE_IMAGE_DIR = "profile_images";

    private final Path uploadRoot;

    public LocalFileStorageService(@Value("${app.file-storage.upload-root:uploads}") String uploadRoot) {
        this.uploadRoot = Paths.get(uploadRoot).toAbsolutePath().normalize();
    }

    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        String originalName = file.getOriginalFilename();
        String safeName = (originalName == null || originalName.isBlank())
                ? "file"
                : Paths.get(originalName).getFileName().toString().replaceAll("\\s+", "_");
        String fileName = UUID.randomUUID() + "-" + safeName;

        Path profileDir = uploadRoot.resolve(PROFILE_IMAGE_DIR).normalize();
        Files.createDirectories(profileDir);

        Path destination = profileDir.resolve(fileName).normalize();
        if (!destination.startsWith(profileDir)) {
            throw new IllegalArgumentException("잘못된 파일 이름입니다.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return PROFILE_IMAGE_DIR + "/" + fileName;
    }

    public String uploadTaskAttachment(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > 10 * 1024 * 1024L) {
            throw new IllegalArgumentException("Invalid task attachment.");
        }
        String originalName = file.getOriginalFilename();
        String safeName = (originalName == null || originalName.isBlank())
                ? "file"
                : Paths.get(originalName).getFileName().toString()
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        Path directory = uploadRoot.resolve("task_attachments").normalize();
        Files.createDirectories(directory);
        Path destination = directory.resolve(UUID.randomUUID() + "-" + safeName).normalize();
        if (!destination.startsWith(directory)) {
            throw new IllegalArgumentException("Invalid file name.");
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        return uploadRoot.relativize(destination).toString().replace("\\", "/");
    }

    public String getPublicUrl(String keyOrUrl) {
        if (keyOrUrl == null || keyOrUrl.isBlank()) {
            return null;
        }
        if (keyOrUrl.startsWith("http://") || keyOrUrl.startsWith("https://")) {
            return keyOrUrl;
        }

        String normalized = keyOrUrl.startsWith("/") ? keyOrUrl : "/" + keyOrUrl;
        return normalized.replace("\\", "/");
    }
}
