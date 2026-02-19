package org.betsync.streaming.kafka.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.*;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.MILLIS;

import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EventOutcomeMessage extends Message {

  @NotNull EventOutcome data;

  public EventOutcomeMessage(EventOutcome data) {
    super(UUID.randomUUID().toString(), ZonedDateTime.now(UTC).truncatedTo(MILLIS));
    this.data = data;
  }
}
