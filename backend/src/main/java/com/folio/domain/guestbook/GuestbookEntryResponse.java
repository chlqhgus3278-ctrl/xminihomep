package com.folio.domain.guestbook;

import java.time.LocalDateTime;
import java.util.List;

/** 공개 API 응답용 DTO. 작성자 이름은 마스킹되어 내려간다. */
public record GuestbookEntryResponse(
        Long id,
        Long ownerId,
        Long authorId,
        String authorName,
        String message,
        LocalDateTime createdAt,
        int commentCount,
        List<GuestbookCommentResponse> comments
) {

    public record GuestbookCommentResponse(
            Long id,
            Long authorId,
            String authorName,
            String message,
            LocalDateTime createdAt
    ) {
    }
}
