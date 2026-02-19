package org.betsync.streaming.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import org.betsync.service.BetService;
import org.betsync.streaming.kafka.model.EventOutcomeMessage;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

/** Handler of event-outcome listening to a Kafka topic. */
@Component
@Slf4j
public class EventOutcomeListener {

  private final BetService betService;

  public EventOutcomeListener(BetService betService) {
    this.betService = betService;
  }

  @KafkaListener(
      id = "web-tracking-listener",
      topics = "${kafka.topics.event-outcomes-event-topic.name}",
      groupId = "${kafka.consumers.event-outcomes-consumer.group}",
      containerFactory = "containerFactory")
  public void listenForEvent(@Payload @Valid EventOutcomeMessage eventOutcomeMessage) {
    if (eventOutcomeMessage == null) {
      throw new IllegalArgumentException("Message event outcome is null");
    }
    log.debug("Consuming valid event outcome message with id {}", eventOutcomeMessage.getId());

    var eventId = eventOutcomeMessage.getData().getEventId();
    var eventWinnerId = eventOutcomeMessage.getData().getWinnerId();
    betService.publishEligibleToSettleBets(eventId, eventWinnerId);
  }
}
