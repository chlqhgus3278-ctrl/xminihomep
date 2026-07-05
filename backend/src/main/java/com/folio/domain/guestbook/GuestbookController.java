package com.folio.domain.guestbook;

import com.folio.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guestbook")
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService guestbookService;

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        guestbookService.delete(id, userId);
        return ApiResponse.success(null);
    }

    /** 홈피 주인의 방명록 일괄 삭제 (체크박스 선택 삭제) */
    @PostMapping("/bulk-delete")
    public ApiResponse<Void> bulkDelete(@AuthenticationPrincipal Long userId,
                                          @RequestBody Map<String, List<Long>> body) {
        guestbookService.deleteBulk(body.get("ids"), userId);
        return ApiResponse.success(null);
    }
}
