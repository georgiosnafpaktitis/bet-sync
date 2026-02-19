package org.betsync.controller.model;

import lombok.Value;

import jakarta.validation.constraints.NotBlank;

@Value
public class EventOutcomeRequest {

  @NotBlank String eventId;
  @NotBlank String eventName;
  @NotBlank String winnerId;
}
