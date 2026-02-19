package org.betsync.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.betsync.controller.model.EventOutcomeRequest;
import org.betsync.service.mappers.EventOutcomeMapper;
import org.betsync.streaming.kafka.model.EventOutcome;
import org.betsync.streaming.kafka.producer.EventOutcomePublisher;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventOutcomeServiceImplTest {
  @Mock private EventOutcomePublisher eventOutcomePublisher;
  @Mock private EventOutcomeMapper eventOutcomeMapper;

  @Test
  void postEventMapsAndPublishes() {
    EventOutcomeServiceImpl service =
        new EventOutcomeServiceImpl(eventOutcomePublisher, eventOutcomeMapper);

    EventOutcomeRequest request = new EventOutcomeRequest("event-1", "Match A", "winner-1");
    EventOutcome outcome =
        EventOutcome.builder().eventId("event-1").eventName("Match A").winnerId("winner-1").build();

    when(eventOutcomeMapper.mapFrom(request)).thenReturn(outcome);

    service.postEvent(request);

    verify(eventOutcomeMapper).mapFrom(request);
    verify(eventOutcomePublisher).send(outcome);
  }
}
