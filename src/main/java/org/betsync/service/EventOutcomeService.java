package org.betsync.service;

import org.betsync.controller.model.EventOutcomeRequest;

public interface EventOutcomeService {

  /**
   * Publishes an event outcome to kafka after mapping the request data to the appropriate kafka
   * event format.
   *
   * @param eventOutcomeRequest the event outcome request containing details such as event ID, event
   *     name, and winner ID to be published.
   */
  void postEvent(EventOutcomeRequest eventOutcomeRequest);
}
