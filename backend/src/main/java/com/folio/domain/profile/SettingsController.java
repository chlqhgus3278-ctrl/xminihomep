package com.folio.domain.profile;

import com.folio.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getSettings(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(settingsService.getSettings(userId));
    }

    @PutMapping("/theme")
    public ApiResponse<Map<String, Object>> updateTheme(@AuthenticationPrincipal Long userId,
                                                          @RequestBody Map<String, String> body) {
        return ApiResponse.success(settingsService.updateTheme(userId, body.get("theme")));
    }

    @PutMapping("/skin")
    public ApiResponse<Map<String, Object>> updateSkin(@AuthenticationPrincipal Long userId,
                                                         @RequestBody Map<String, Object> skinConfig) {
        return ApiResponse.success(settingsService.updateSkin(userId, skinConfig));
    }

    @PutMapping("/layout")
    public ApiResponse<Map<String, Object>> updateLayout(@AuthenticationPrincipal Long userId,
                                                           @RequestBody Map<String, Object> body) {
        return ApiResponse.success(settingsService.updateLayout(userId, body));
    }

    @PutMapping("/visibility")
    public ApiResponse<Map<String, Object>> updateVisibility(@AuthenticationPrincipal Long userId,
                                                               @RequestBody Map<String, Object> body) {
        return ApiResponse.success(settingsService.updateVisibility(userId, body));
    }
}
