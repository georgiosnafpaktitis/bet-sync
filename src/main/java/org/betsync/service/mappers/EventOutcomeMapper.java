package org.betsync.service.mappers;

import org.mapstruct.Mapper;

import org.betsync.controller.model.EventOutcomeRequest;
import org.betsync.streaming.kafka.model.EventOutcome;

@Mapper(componentModel = "spring")
public abstract class EventOutcomeMapper {
  public abstract EventOutcome mapFrom(EventOutcomeRequest eventOutcomeRequest);
}
