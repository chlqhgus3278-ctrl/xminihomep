package com.folio.domain.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.folio.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "homepage_title", length = 100)
    private String homepageTitle;

    @Column(name = "profile_img_url", columnDefinition = "TEXT")
    private String profileImgUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 100)
    private String location;

    @Column(name = "email_public")
    private String emailPublic;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String intro;

    @Builder.Default
    @Column(length = 20)
    private String theme = "retro";

    /** JSON 문자열 그대로 저장/조회. JSON 파싱·직렬화는 서비스 레이어에서 처리한다. */
    @Getter(AccessLevel.NONE)
    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skin_config", columnDefinition = "jsonb")
    private String skinConfig = "{}";

    @Column(name = "bgm_url", columnDefinition = "TEXT")
    private String bgmUrl;

    @Column(name = "bgm_title", length = 200)
    private String bgmTitle;

    @Builder.Default
    @Column(name = "is_public")
    private Boolean isPublic = true;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** skinConfig는 DB에 JSON 문자열로 저장되지만, API 응답에는 파싱된 객체로 그대로 내려준다. */
    @JsonRawValue
    public String getSkinConfig() {
        return skinConfig;
    }
}
