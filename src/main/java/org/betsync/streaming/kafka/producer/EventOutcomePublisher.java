package org.betsync.streaming.kafka.producer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.betsync.controller.exception.ApplicationException;
import org.betsync.streaming.kafka.model.EventOutcome;
import org.betsync.streaming.kafka.model.EventOutcomeMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventOutcomePublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final String topic;

  public EventOutcomePublisher(
      KafkaTemplate<String, Object> kafkaTemplate,
      @Value("${kafka.topics.event-outcomes-event-topic.name}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
  }

  public void send(EventOutcome eventOutcome) {
    try {
      var eventOutcomeMessage = EventOutcomeMessage.builder().data(eventOutcome).build();
      kafkaTemplate
          .send(
              new ProducerRecord<>(
                  topic, null, createKey(eventOutcomeMessage), eventOutcomeMessage))
          .get();
    } catch (Exception ex) {
      log.error("Error while sending message into kafka topic {}", topic, ex);
      throw new ApplicationException("Error while sending message into kafka topic " + topic, ex);
    }
  }

  private String createKey(EventOutcomeMessage event) {
    var keyPlain = event.getData().getEventId();
    return Base64.getEncoder().encodeToString(keyPlain.getBytes(StandardCharsets.UTF_8));
  }
}
