package org.betsync.repository.model;

import java.math.BigDecimal;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "bet",
        indexes = {
                @Index(name = "idx_bet_event_published", columnList = "event_id,published_for_settlement")
        })
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BetEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(nullable = false, unique = true, length = 64)
  private String betId;

  @Column(nullable = false, length = 64)
  private String userId;

  @Column(nullable = false, length = 64)
  private String eventId;

  @Column(nullable = false, length = 64)
  private String eventMarketId;

  @Column(nullable = true, length = 64)
  private String eventWinnerId;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal betAmount;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean publishedForSettlement;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private Status status = Status.OPEN;

  @Version private long version;
}
