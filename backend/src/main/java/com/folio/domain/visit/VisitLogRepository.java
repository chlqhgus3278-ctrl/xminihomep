package com.folio.domain.visit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    boolean existsByOwner_IdAndVisitorIpAndVisitDate(Long ownerId, String visitorIp, LocalDate visitDate);
}
