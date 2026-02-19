package org.betsync.service;

import org.springframework.stereotype.Service;

import org.betsync.controller.model.EventOutcomeRequest;
import org.betsync.service.mappers.EventOutcomeMapper;
import org.betsync.streaming.kafka.producer.EventOutcomePublisher;

@Service
public class EventOutcomeServiceImpl implements EventOutcomeService {

  private final EventOutcomePublisher eventOutcomePublisher;
  private final EventOutcomeMapper eventOutcomeMapper;

  public EventOutcomeServiceImpl(
      EventOutcomePublisher eventOutcomePublisher, EventOutcomeMapper eventOutcomeMapper) {
    this.eventOutcomePublisher = eventOutcomePublisher;
    this.eventOutcomeMapper = eventOutcomeMapper;
  }

  public void postEvent(EventOutcomeRequest eventOutcomeRequest) {
    eventOutcomePublisher.send(eventOutcomeMapper.mapFrom(eventOutcomeRequest));
  }
}
