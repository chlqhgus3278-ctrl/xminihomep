package com.folio.domain.guestbook;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestbookRepository extends JpaRepository<GuestbookEntry, Long> {

    List<GuestbookEntry> findByOwner_UsernameOrderByCreatedAtDesc(String username);

    List<GuestbookEntry> findByOwner_IdOrderByCreatedAtDesc(Long ownerId);
}
