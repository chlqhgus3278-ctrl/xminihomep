package com.folio.domain.profile;

import com.folio.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<Profile> getMyProfile(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(profileService.getMyProfile(userId));
    }

    @PutMapping
    public ApiResponse<Profile> updateMyProfile(@AuthenticationPrincipal Long userId,
                                                 @RequestBody Map<String, Object> updates) {
        return ApiResponse.success(profileService.updateMyProfile(userId, updates));
    }

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ApiResponse<Profile> uploadImage(@AuthenticationPrincipal Long userId,
                                             @RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.success(profileService.uploadImage(userId, file));
    }

    @DeleteMapping("/image")
    public ApiResponse<Void> deleteImage(@AuthenticationPrincipal Long userId) {
        profileService.deleteImage(userId);
        return ApiResponse.success(null);
    }

    @PostMapping(value = "/bgm", consumes = "multipart/form-data")
    public ApiResponse<Profile> uploadBgm(@AuthenticationPrincipal Long userId,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.success(profileService.uploadBgm(userId, file));
    }

    @DeleteMapping("/bgm")
    public ApiResponse<Void> deleteBgm(@AuthenticationPrincipal Long userId) {
        profileService.deleteBgm(userId);
        return ApiResponse.success(null);
    }
}
