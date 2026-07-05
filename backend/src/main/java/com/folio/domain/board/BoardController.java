package com.folio.domain.board;

import com.folio.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ApiResponse<List<BoardPost>> getMyPosts(@AuthenticationPrincipal Long userId,
                                                     @RequestParam(required = false) BoardType type) {
        return ApiResponse.success(boardService.getMyPosts(userId, type));
    }

    @GetMapping("/{id}")
    public ApiResponse<BoardPost> getMyPost(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ApiResponse.success(boardService.getOwnedPost(userId, id));
    }

    @PostMapping
    public ApiResponse<BoardPost> create(@AuthenticationPrincipal Long userId, @RequestBody BoardPost post) {
        return ApiResponse.success(boardService.create(userId, post));
    }

    @PutMapping("/{id}")
    public ApiResponse<BoardPost> update(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                          @RequestBody BoardPost post) {
        return ApiResponse.success(boardService.update(userId, id, post));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        boardService.delete(userId, id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/visibility")
    public ApiResponse<BoardPost> toggleVisibility(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ApiResponse.success(boardService.toggleVisibility(userId, id));
    }
}
