package com.folio.domain.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private static final Set<String> ALLOWED_THEMES = Set.of("retro", "modern");

    /** 메인 대시보드 구획에 넣을 수 있는 섹션 (경력기술서는 경력사항으로 일원화되어 제외) */
    private static final Set<String> ALLOWED_LAYOUT_SECTIONS =
            Set.of("CAREER_HISTORY", "INTRO", "EDUCATION", "CERT");

    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Map<String, Object> getSettings(Long userId) {
        return toSettingsMap(profileService.getMyProfile(userId));
    }

    @Transactional
    public Map<String, Object> updateTheme(Long userId, String theme) {
        if (theme == null || !ALLOWED_THEMES.contains(theme)) {
            throw new IllegalArgumentException("지원하지 않는 테마입니다: " + theme);
        }
        Profile profile = profileService.getMyProfile(userId);
        profile.setTheme(theme);
        return toSettingsMap(profile);
    }

    @Transactional
    public Map<String, Object> updateSkin(Long userId, Map<String, Object> skinConfig) {
        Profile profile = profileService.getMyProfile(userId);
        profile.setSkinConfig(writeJson(skinConfig));
        return toSettingsMap(profile);
    }

    @Transactional
    public Map<String, Object> updateLayout(Long userId, Map<String, Object> body) {
        Object sections = body.get("sections");
        if (!(sections instanceof List<?> list)) {
            throw new IllegalArgumentException("sections 목록이 필요합니다.");
        }
        for (Object section : list) {
            if (!(section instanceof String s) || !ALLOWED_LAYOUT_SECTIONS.contains(s)) {
                throw new IllegalArgumentException("지원하지 않는 섹션입니다: " + section);
            }
        }
        if (list.stream().distinct().count() != list.size()) {
            throw new IllegalArgumentException("같은 게시판을 중복 배치할 수 없습니다.");
        }
        Profile profile = profileService.getMyProfile(userId);
        profile.setLayoutConfig(writeJson(Map.of("sections", list)));
        return toSettingsMap(profile);
    }

    @Transactional
    public Map<String, Object> updateVisibility(Long userId, Map<String, Object> body) {
        Object isPublic = body.get("isPublic");
        if (!(isPublic instanceof Boolean)) {
            throw new IllegalArgumentException("isPublic 값이 필요합니다.");
        }
        Profile profile = profileService.getMyProfile(userId);
        profile.setIsPublic((Boolean) isPublic);
        return toSettingsMap(profile);
    }

    private Map<String, Object> toSettingsMap(Profile profile) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("theme", profile.getTheme());
        result.put("skinConfig", readJson(profile.getSkinConfig()));
        result.put("layoutConfig", readJson(profile.getLayoutConfig()));
        result.put("isPublic", profile.getIsPublic());
        return result;
    }

    private String writeJson(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value == null ? Map.of() : value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("skinConfig 형식이 올바르지 않습니다.");
        }
    }

    private Map<String, Object> readJson(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
