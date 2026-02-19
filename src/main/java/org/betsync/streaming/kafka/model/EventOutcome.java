package org.betsync.streaming.kafka.model;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EventOutcome {
  @NotBlank String eventId;
  @NotBlank String eventName;
  @NotBlank String winnerId;
}
