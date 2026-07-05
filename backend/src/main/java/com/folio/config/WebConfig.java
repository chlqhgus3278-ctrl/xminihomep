package com.folio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** LocalStorageService가 저장한 파일을 /uploads/** 경로로 서빙한다 (로컬 개발용). */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.local-dir:uploads}")
    private String localDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + localDir + "/");
    }
}
