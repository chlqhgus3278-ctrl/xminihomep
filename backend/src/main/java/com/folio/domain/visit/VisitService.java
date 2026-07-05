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
        VisitCount count = visitCountRepository.findById(ownerId)
                .orElseGet(() -> VisitCount.builder().userId(ownerId).total(0L).today(0L).build());

        // 날짜가 바뀌었는데 아직 오늘 방문이 없어 upsert가 안 돈 경우, 어제 카운트가 남아 있으므로 0으로 보정한다.
        if (count.getDateReset() != null && count.getDateReset().isBefore(LocalDate.now())) {
            return VisitCount.builder()
                    .userId(count.getUserId())
                    .total(count.getTotal())
                    .today(0L)
                    .dateReset(count.getDateReset())
                    .build();
        }
        return count;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyCount() {
        visitCountRepository.resetAllToday(LocalDate.now());
    }
}
