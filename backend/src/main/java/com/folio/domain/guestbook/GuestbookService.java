package com.folio.domain.guestbook;

import com.folio.domain.guestbook.GuestbookEntryResponse.GuestbookCommentResponse;
import com.folio.domain.user.User;
import com.folio.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final GuestbookCommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getEntriesPage(String username, int page, int size) {
        Page<GuestbookEntry> entryPage = guestbookRepository.findByOwner_Username(
                username, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Long> entryIds = entryPage.getContent().stream().map(GuestbookEntry::getId).toList();
        Map<Long, List<GuestbookComment>> commentsByEntry = entryIds.isEmpty()
                ? Map.of()
                : commentRepository.findByEntry_IdInOrderByCreatedAtAsc(entryIds).stream()
                        .collect(Collectors.groupingBy(comment -> comment.getEntry().getId()));

        List<GuestbookEntryResponse> content = entryPage.getContent().stream()
                .map(entry -> toResponse(entry, commentsByEntry.getOrDefault(entry.getId(), List.of())))
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", content);
        result.put("page", entryPage.getNumber());
        result.put("totalPages", entryPage.getTotalPages());
        result.put("totalElements", entryPage.getTotalElements());
        return result;
    }

    @Transactional
    public GuestbookEntryResponse create(String ownerUsername, Long authorUserId, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("메시지를 입력해주세요.");
        }

        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + ownerUsername));
        User author = userRepository.findById(authorUserId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + authorUserId));

        GuestbookEntry entry = GuestbookEntry.builder()
                .owner(owner)
                .author(author)
                .authorName(author.getUsername())
                .message(message)
                .build();
        return toResponse(guestbookRepository.save(entry), List.of());
    }

    @Transactional
    public GuestbookCommentResponse addComment(String ownerUsername, Long entryId, Long authorUserId, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }

        GuestbookEntry entry = guestbookRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("방명록을 찾을 수 없습니다: " + entryId));
        if (!entry.getOwner().getUsername().equals(ownerUsername)) {
            throw new EntityNotFoundException("방명록을 찾을 수 없습니다: " + entryId);
        }
        User author = userRepository.findById(authorUserId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + authorUserId));

        GuestbookComment comment = commentRepository.save(GuestbookComment.builder()
                .entry(entry)
                .author(author)
                .authorName(author.getUsername())
                .message(message)
                .build());
        return toCommentResponse(comment);
    }

    @Transactional
    public void delete(Long entryId, Long requesterId) {
        GuestbookEntry entry = guestbookRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("방명록을 찾을 수 없습니다: " + entryId));

        boolean isAuthor = entry.getAuthor() != null && entry.getAuthor().getId().equals(requesterId);
        boolean isOwner = entry.getOwner().getId().equals(requesterId);

        if (!isAuthor && !isOwner) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        guestbookRepository.delete(entry);
    }

    /** 홈피 주인이 체크박스로 선택한 방명록을 한 번에 삭제한다. */
    @Transactional
    public void deleteBulk(List<Long> entryIds, Long requesterId) {
        if (entryIds == null || entryIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 방명록을 선택해주세요.");
        }

        List<GuestbookEntry> entries = guestbookRepository.findAllById(entryIds);
        for (GuestbookEntry entry : entries) {
            if (!entry.getOwner().getId().equals(requesterId)) {
                throw new IllegalArgumentException("삭제 권한이 없습니다.");
            }
        }
        guestbookRepository.deleteAll(entries);
    }

    private GuestbookEntryResponse toResponse(GuestbookEntry entry, List<GuestbookComment> comments) {
        return new GuestbookEntryResponse(
                entry.getId(),
                entry.getOwnerId(),
                entry.getAuthorId(),
                maskName(entry.getAuthorName()),
                entry.getMessage(),
                entry.getCreatedAt(),
                comments.size(),
                comments.stream().map(this::toCommentResponse).toList()
        );
    }

    private GuestbookCommentResponse toCommentResponse(GuestbookComment comment) {
        return new GuestbookCommentResponse(
                comment.getId(),
                comment.getAuthorId(),
                maskName(comment.getAuthorName()),
                comment.getMessage(),
                comment.getCreatedAt()
        );
    }

    /** 아이디 마스킹: 앞부분만 남기고 가린다 (예: chlqhgus3278 → chl***, 홍길동 → 홍*동) */
    static String maskName(String name) {
        if (name == null || name.isBlank()) return "익명";
        int len = name.length();
        if (len <= 2) return name.charAt(0) + "*";
        if (len == 3) return name.charAt(0) + "*" + name.charAt(2);
        return name.substring(0, 3) + "***";
    }
}
