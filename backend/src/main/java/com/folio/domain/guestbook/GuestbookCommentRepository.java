package com.folio.domain.guestbook;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestbookCommentRepository extends JpaRepository<GuestbookComment, Long> {

    List<GuestbookComment> findByEntry_IdInOrderByCreatedAtAsc(List<Long> entryIds);
}
