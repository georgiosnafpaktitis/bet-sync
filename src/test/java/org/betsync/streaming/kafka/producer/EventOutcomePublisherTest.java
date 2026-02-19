package org.betsync.streaming.kafka.producer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import org.betsync.controller.exception.ApplicationException;
import org.betsync.streaming.kafka.model.EventOutcome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventOutcomePublisherTest {
  @Mock private KafkaTemplate<String, Object> kafkaTemplate;

  @Test
  void sendPublishesToKafka() throws Exception {
    EventOutcomePublisher publisher = new EventOutcomePublisher(kafkaTemplate, "topic");
    EventOutcome eventOutcome =
        EventOutcome.builder().eventId("event-1").eventName("Match A").winnerId("win").build();

    when(kafkaTemplate.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    publisher.send(eventOutcome);

    ArgumentCaptor<ProducerRecord<String, Object>> captor =
        ArgumentCaptor.forClass(ProducerRecord.class);
    verify(kafkaTemplate).send(captor.capture());

    ProducerRecord<String, Object> record = captor.getValue();
    assertEquals("topic", record.topic());
    String expectedKey =
        Base64.getEncoder().encodeToString("event-1".getBytes(StandardCharsets.UTF_8));
    assertEquals(expectedKey, record.key());
  }

  @Test
  void sendWrapsException() {
    EventOutcomePublisher publisher = new EventOutcomePublisher(kafkaTemplate, "topic");
    EventOutcome eventOutcome =
        EventOutcome.builder().eventId("event-1").eventName("Match A").winnerId("win").build();

    when(kafkaTemplate.send(any(ProducerRecord.class)))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

    assertThrows(ApplicationException.class, () -> publisher.send(eventOutcome));
  }
}
