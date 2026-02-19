package org.betsync.streaming.rocketMq.producer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import org.betsync.controller.exception.ApplicationException;
import org.betsync.repository.model.Status;
import org.betsync.streaming.rocketMq.model.BetSettlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BetSettlementPublisherTest {
  @Mock private RocketMQTemplate rocketMQTemplate;

  @Test
  void sendPublishesToRocketMq() {
    BetSettlementPublisher publisher = new BetSettlementPublisher(rocketMQTemplate, "topic");
    BetSettlement settlement = BetSettlement.builder().betId("bet-1").status(Status.WON).build();

    publisher.send(settlement);

    ArgumentCaptor<Message<?>> captor = ArgumentCaptor.forClass(Message.class);
    verify(rocketMQTemplate).syncSend(org.mockito.ArgumentMatchers.eq("topic"), captor.capture());

    Message<?> message = captor.getValue();
    String expectedKey =
        Base64.getEncoder().encodeToString("bet-1".getBytes(StandardCharsets.UTF_8));
    assertEquals(expectedKey, message.getHeaders().get(RocketMQHeaders.KEYS));
  }

  @Test
  void sendWrapsException() {
    BetSettlementPublisher publisher = new BetSettlementPublisher(rocketMQTemplate, "topic");
    BetSettlement settlement = BetSettlement.builder().betId("bet-1").status(Status.WON).build();

    when(rocketMQTemplate.syncSend(any(String.class), any(Message.class)))
        .thenThrow(new RuntimeException("fail"));

    assertThrows(ApplicationException.class, () -> publisher.send(settlement));
  }
}
