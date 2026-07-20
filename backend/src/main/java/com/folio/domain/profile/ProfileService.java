package com.folio.domain.profile;

import com.folio.domain.user.User;
import com.folio.domain.user.UserRepository;
import com.folio.storage.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional
    public Profile getMyProfile(Long userId) {
        return getOrCreate(userId);
    }

    @Transactional
    public Profile updateMyProfile(Long userId, Map<String, Object> updates) {
        Profile profile = getOrCreate(userId);

        if (updates.containsKey("displayName")) {
            profile.setDisplayName((String) updates.get("displayName"));
        }
        if (updates.containsKey("homepageTitle")) {
            profile.setHomepageTitle((String) updates.get("homepageTitle"));
        }
        if (updates.containsKey("birthDate")) {
            Object value = updates.get("birthDate");
            profile.setBirthDate(value == null ? null : LocalDate.parse((String) value));
        }
        if (updates.containsKey("location")) {
            profile.setLocation((String) updates.get("location"));
        }
        if (updates.containsKey("emailPublic")) {
            profile.setEmailPublic((String) updates.get("emailPublic"));
        }
        if (updates.containsKey("phone")) {
            profile.setPhone((String) updates.get("phone"));
        }
        if (updates.containsKey("intro")) {
            profile.setIntro((String) updates.get("intro"));
        }
        if (updates.containsKey("job")) {
            profile.setJob((String) updates.get("job"));
        }

        return profile;
    }

    @Transactional
    public Profile uploadImage(Long userId, MultipartFile file) throws IOException {
        Profile profile = getOrCreate(userId);
        if (profile.getProfileImgUrl() != null) {
            storageService.delete(profile.getProfileImgUrl());
        }
        profile.setProfileImgUrl(storageService.upload(file, StorageService.FOLDER_IMAGES));
        return profile;
    }

    @Transactional
    public void deleteImage(Long userId) {
        Profile profile = getOrCreate(userId);
        if (profile.getProfileImgUrl() != null) {
            storageService.delete(profile.getProfileImgUrl());
            profile.setProfileImgUrl(null);
        }
    }

    @Transactional
    public Profile uploadBgm(Long userId, MultipartFile file) throws IOException {
        Profile profile = getOrCreate(userId);
        if (profile.getBgmUrl() != null) {
            storageService.delete(profile.getBgmUrl());
        }
        profile.setBgmUrl(storageService.upload(file, StorageService.FOLDER_BGM));
        profile.setBgmTitle(file.getOriginalFilename());
        return profile;
    }

    @Transactional
    public void deleteBgm(Long userId) {
        Profile profile = getOrCreate(userId);
        if (profile.getBgmUrl() != null) {
            storageService.delete(profile.getBgmUrl());
            profile.setBgmUrl(null);
            profile.setBgmTitle(null);
        }
    }

    private Profile getOrCreate(Long userId) {
        return profileRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
                    return profileRepository.save(Profile.builder().user(user).build());
                });
    }
}
