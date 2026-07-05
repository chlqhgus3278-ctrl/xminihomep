package com.folio.domain.visit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.folio.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "visit_counts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitCount {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Column(name = "total")
    private Long total = 0L;

    @Builder.Default
    @Column(name = "today")
    private Long today = 0L;

    @Builder.Default
    @Column(name = "date_reset")
    private LocalDate dateReset = LocalDate.now();
}
