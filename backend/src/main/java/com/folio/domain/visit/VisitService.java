package com.folio.domain.visit;

import com.folio.domain.user.User;
import com.folio.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitLogRepository visitLogRepository;
    private final VisitCountRepository visitCountRepository;
    private final UserRepository userRepository;

    @Transactional
    public void recordVisit(Long ownerId, String visitorIp) {
        boolean alreadyVisited = visitLogRepository
                .existsByOwner_IdAndVisitorIpAndVisitDate(ownerId, visitorIp, LocalDate.now());

        if (alreadyVisited) {
            return;
        }

        User owner = userRepository.getReferenceById(ownerId);
        visitLogRepository.save(VisitLog.builder().owner(owner).visitorIp(visitorIp).build());
        visitCountRepository.upsertIncrement(ownerId);
    }

    @Transactional(readOnly = true)
    public VisitCount getCount(Long ownerId) {
        return visitCountRepository.findById(ownerId)
                .orElseGet(() -> VisitCount.builder().userId(ownerId).total(0L).today(0L).build());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyCount() {
        visitCountRepository.resetAllToday(LocalDate.now());
    }
}
