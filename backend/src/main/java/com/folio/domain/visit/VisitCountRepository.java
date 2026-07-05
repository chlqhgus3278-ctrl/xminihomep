package com.folio.domain.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface VisitCountRepository extends JpaRepository<VisitCount, Long> {

    Optional<VisitCount> findByUser_Username(String username);

    // VisitCount는 @MapsId로 PK가 미리 채워지기 때문에 save()가 이를 기존 엔티티로 오인해
    // merge(UPDATE)를 시도하다 실패한다. INSERT ... ON CONFLICT로 원자적으로 생성/증가시킨다.
    @Modifying
    @Query(value = """
            INSERT INTO visit_counts (user_id, total, today, date_reset)
            VALUES (:ownerId, 1, 1, CURRENT_DATE)
            ON CONFLICT (user_id) DO UPDATE
            SET total = visit_counts.total + 1, today = visit_counts.today + 1
            """, nativeQuery = true)
    void upsertIncrement(@Param("ownerId") Long ownerId);

    // 유저 가입 시점에 0/0짜리 초기 레코드를 미리 만들어 둔다 (이미 있으면 손대지 않는다).
    @Modifying
    @Query(value = """
            INSERT INTO visit_counts (user_id, total, today, date_reset)
            VALUES (:userId, 0, 0, CURRENT_DATE)
            ON CONFLICT (user_id) DO NOTHING
            """, nativeQuery = true)
    void initializeForUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE VisitCount v SET v.today = 0, v.dateReset = :today")
    void resetAllToday(@Param("today") LocalDate today);
}
