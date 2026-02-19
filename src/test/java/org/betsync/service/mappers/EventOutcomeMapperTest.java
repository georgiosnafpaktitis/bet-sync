package org.betsync.service.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import org.betsync.controller.model.EventOutcomeRequest;
import org.betsync.streaming.kafka.model.EventOutcome;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventOutcomeMapperTest {
  @Test
  void mapsRequestToEventOutcome() {
    EventOutcomeMapper mapper = Mappers.getMapper(EventOutcomeMapper.class);

    EventOutcomeRequest request = new EventOutcomeRequest("event-1", "Match A", "winner-1");
    EventOutcome outcome = mapper.mapFrom(request);

    assertEquals("event-1", outcome.getEventId());
    assertEquals("Match A", outcome.getEventName());
    assertEquals("winner-1", outcome.getWinnerId());
  }
}
