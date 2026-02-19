package org.betsync.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.betsync.controller.model.EventOutcomeRequest;
import org.betsync.service.EventOutcomeService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventOutcomeControllerTest {
  @Mock private EventOutcomeService eventOutcomeService;

  @Test
  void postEventDelegatesToService() {
    EventOutcomeController controller = new EventOutcomeController(eventOutcomeService);
    EventOutcomeRequest request = new EventOutcomeRequest("event-1", "Match A", "winner-1");

    controller.postEvent(request);

    verify(eventOutcomeService).postEvent(request);
  }
}
