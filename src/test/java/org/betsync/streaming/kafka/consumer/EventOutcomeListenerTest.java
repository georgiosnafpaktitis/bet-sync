package org.betsync.streaming.kafka.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.betsync.service.BetService;
import org.betsync.streaming.kafka.model.EventOutcome;
import org.betsync.streaming.kafka.model.EventOutcomeMessage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventOutcomeListenerTest {
  @Mock private BetService betService;

  @Test
  void listenForEventThrowsOnNullMessage() {
    EventOutcomeListener listener = new EventOutcomeListener(betService);

    assertThrows(IllegalArgumentException.class, () -> listener.listenForEvent(null));
  }

  @Test
  void listenForEventDelegatesToService() {
    EventOutcomeListener listener = new EventOutcomeListener(betService);

    EventOutcome eventOutcome =
        EventOutcome.builder().eventId("event-1").eventName("Match A").winnerId("win").build();
    EventOutcomeMessage message = new EventOutcomeMessage(eventOutcome);

    listener.listenForEvent(message);

    verify(betService).publishEligibleToSettleBets("event-1", "win");
  }
}
