package com.folio.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StoragePolicy {

    @Value("${app.upload.image-max-size-bytes}")
    private long imageMaxSizeBytes;

    @Value("${app.upload.bgm-max-size-bytes}")
    private long bgmMaxSizeBytes;

    public void validate(MultipartFile file, String folder) {
        long maxSize = switch (folder) {
            case StorageService.FOLDER_IMAGES -> imageMaxSizeBytes;
            case StorageService.FOLDER_BGM -> bgmMaxSizeBytes;
            default -> throw new IllegalArgumentException("지원하지 않는 업로드 폴더입니다: " + folder);
        };

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    "파일 크기는 %dMB를 초과할 수 없습니다.".formatted(maxSize / (1024 * 1024)));
        }
    }
}
