package com.folio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.folio.common.ApiResponse;
import com.folio.oauth2.CustomOAuth2UserService;
import com.folio.oauth2.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // oauth2Login()의 인가 요청/state 검증은 세션에 의존하므로 완전한 STATELESS는 사용할 수 없다.
                // 로그인 핸드셰이크 때만 세션이 쓰이고, 이후 API 호출은 JwtFilter가 세션 없이 인증을 채운다.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // 방명록 작성(POST)만 로그인이 필요하고, /api/public/** 나머지 조회성 엔드포인트는 공개다.
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/public/*/visit").permitAll()
                        .requestMatchers("/api/public/**").authenticated()
                        // /api/auth/** (예: GET /api/auth/me)는 JWT로 "누구인지"를 알아내는 용도라 인증이 필요하다.
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler))
                // oauth2Login()의 기본 진입점은 미인증 요청을 구글 로그인 페이지로 302 리다이렉트한다.
                // SPA가 axios로 호출하는 API에는 맞지 않으므로, 인증 실패 시 다른 API 응답과 동일한
                // ApiResponse 형태의 401 JSON을 내려준다 (GlobalExceptionHandler는 스프링 시큐리티
                // 필터 단계에서 걸러지는 이 실패를 가로챌 수 없어 여기서 직접 처리해야 한다).
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeApiError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다."))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeApiError(response, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.")))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void writeApiError(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(message)));
    }
}
