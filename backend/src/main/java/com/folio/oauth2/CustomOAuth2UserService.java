package com.folio.oauth2;

import com.folio.domain.user.User;
import com.folio.domain.user.UserRepository;
import com.folio.domain.visit.VisitCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 20;

    private final UserRepository userRepository;
    private final VisitCountRepository visitCountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String googleSub = (String) attributes.get("sub");
        String email = (String) attributes.get("email");

        User user = upsertUser(googleSub, email);

        return new CustomOAuth2User(user.getId(), attributes, oAuth2User.getAuthorities());
    }

    private User upsertUser(String googleSub, String email) {
        return userRepository.findByGoogleSub(googleSub)
                .map(existing -> {
                    if (!existing.getEmail().equals(email)) {
                        existing.updateEmail(email);
                        return userRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    User created = userRepository.save(
                            User.builder()
                                    .email(email)
                                    .username(generateUsername(email))
                                    .googleSub(googleSub)
                                    .build()
                    );
                    // visit_counts는 users FK를 참조하므로 유저 row가 커밋되기 전엔 만들 수 없다.
                    // 같은 트랜잭션 안에서 바로 만들어 둔다 (없으면 첫 방문 때 지연 생성됨).
                    visitCountRepository.initializeForUser(created.getId());
                    return created;
                });
    }

    /**
     * users.username은 DB에 CHECK (username ~ '^[a-z0-9-]{3,20}$') 제약이 걸려 있다.
     * 이메일 로컬파트에서 영문 소문자/숫자/하이픈만 남기고, 길이를 3~20자로 맞춘다.
     */
    private String generateUsername(String email) {
        String base = email.substring(0, email.indexOf('@'))
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("^-+|-+$", "");

        if (base.length() > USERNAME_MAX_LENGTH - 4) {
            base = base.substring(0, USERNAME_MAX_LENGTH - 4);
        }
        if (base.length() < USERNAME_MIN_LENGTH) {
            base = (base + "user").substring(0, USERNAME_MIN_LENGTH);
        }

        String candidate = base;
        int suffix = 0;
        while (userRepository.existsByUsername(candidate)) {
            suffix++;
            String suffixText = String.valueOf(suffix);
            candidate = base.substring(0, Math.min(base.length(), USERNAME_MAX_LENGTH - suffixText.length()))
                    + suffixText;
        }
        return candidate;
    }
}
