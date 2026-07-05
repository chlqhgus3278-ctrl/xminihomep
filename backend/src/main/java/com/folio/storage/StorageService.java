package com.folio.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String FOLDER_IMAGES = "images";
    String FOLDER_BGM = "bgm";

    String upload(MultipartFile file, String folder) throws IOException;

    void delete(String fileUrl);
}
