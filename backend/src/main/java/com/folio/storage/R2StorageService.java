package com.folio.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/** R2 자격증명이 설정된 환경(운영)에서 사용. 없으면 LocalStorageService가 대신 활성화된다. */
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("!'${cloudflare.r2.endpoint:}'.isEmpty()")
public class R2StorageService implements StorageService {

    private final S3Client s3Client;
    private final StoragePolicy storagePolicy;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    @Override
    public String upload(MultipartFile file, String folder) throws IOException {
        storagePolicy.validate(file, folder);

        String key = folder + "/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
        return publicUrl + "/" + key;
    }

    @Override
    public void delete(String fileUrl) {
        String key = fileUrl.replace(publicUrl + "/", "");
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
