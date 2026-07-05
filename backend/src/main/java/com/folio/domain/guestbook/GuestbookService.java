package com.folio.domain.guestbook;

import com.folio.domain.user.User;
import com.folio.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<GuestbookEntry> getEntries(String username) {
        return guestbookRepository.findByOwner_UsernameOrderByCreatedAtDesc(username);
    }

    @Transactional
    public GuestbookEntry create(String ownerUsername, Long authorUserId, String message) {
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
        return guestbookRepository.save(entry);
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
}
