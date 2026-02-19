package org.betsync.streaming.rocketMq.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.*;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MILLIS;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BetSettlementMessage extends Message {

  @Valid @NotNull BetSettlement data;

  public BetSettlementMessage(BetSettlement data) {
    super(UUID.randomUUID().toString(), ZonedDateTime.now(UTC).truncatedTo(MILLIS));
    this.data = data;
  }
}
