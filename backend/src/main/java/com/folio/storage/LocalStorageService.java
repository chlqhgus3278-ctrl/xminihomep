package com.folio.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * R2 자격증명이 없는 로컬 개발 환경용 폴백 스토리지.
 * 파일을 uploads/ 디렉토리에 저장하고 /uploads/** 정적 경로로 서빙한다 (WebConfig 참고).
 */
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("'${cloudflare.r2.endpoint:}'.isEmpty()")
public class LocalStorageService implements StorageService {

    private final StoragePolicy storagePolicy;

    @Value("${app.url}")
    private String appUrl;

    @Value("${app.upload.local-dir:uploads}")
    private String localDir;

    @Override
    public String upload(MultipartFile file, String folder) throws IOException {
        storagePolicy.validate(file, folder);

        String key = folder + "/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        Path target = Paths.get(localDir).resolve(key);
        Files.createDirectories(target.getParent());
        file.transferTo(target.toAbsolutePath());

        return appUrl + "/uploads/" + key;
    }

    @Override
    public void delete(String fileUrl) {
        String key = fileUrl.replace(appUrl + "/uploads/", "");
        try {
            Files.deleteIfExists(Paths.get(localDir).resolve(key));
        } catch (IOException e) {
            // 로컬 파일 삭제 실패는 치명적이지 않으므로 무시한다.
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
