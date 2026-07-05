package com.folio.domain.publicapi;

import com.folio.common.ApiResponse;
import com.folio.domain.board.BoardPost;
import com.folio.domain.board.BoardType;
import com.folio.domain.guestbook.GuestbookEntry;
import com.folio.domain.guestbook.GuestbookService;
import com.folio.domain.visit.VisitCount;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/{username}")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;
    private final GuestbookService guestbookService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getHomepage(@PathVariable String username) {
        return ApiResponse.success(publicService.getHomepage(username));
    }

    @GetMapping("/board")
    public ApiResponse<List<BoardPost>> getBoardPosts(@PathVariable String username,
                                                        @RequestParam(required = false) BoardType type) {
        return ApiResponse.success(publicService.getBoardPosts(username, type));
    }

    @GetMapping("/board/{id}")
    public ApiResponse<BoardPost> getBoardPost(@PathVariable String username, @PathVariable Long id) {
        return ApiResponse.success(publicService.getBoardPost(username, id));
    }

    @GetMapping("/guestbook")
    public ApiResponse<List<GuestbookEntry>> getGuestbook(@PathVariable String username) {
        return ApiResponse.success(guestbookService.getEntries(username));
    }

    @PostMapping("/guestbook")
    public ApiResponse<GuestbookEntry> createGuestbookEntry(@PathVariable String username,
                                                              @AuthenticationPrincipal Long authorUserId,
                                                              @RequestBody Map<String, String> body) {
        return ApiResponse.success(guestbookService.create(username, authorUserId, body.get("message")));
    }

    @PostMapping("/visit")
    public ApiResponse<Void> recordVisit(@PathVariable String username, HttpServletRequest request) {
        publicService.recordVisit(username, resolveIp(request));
        return ApiResponse.success(null);
    }

    @GetMapping("/visit-count")
    public ApiResponse<VisitCount> getVisitCount(@PathVariable String username) {
        return ApiResponse.success(publicService.getVisitCount(username));
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
