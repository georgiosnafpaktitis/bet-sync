package org.betsync.streaming.rocketMq.producer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import org.betsync.controller.exception.ApplicationException;
import org.betsync.streaming.rocketMq.model.BetSettlement;
import org.betsync.streaming.rocketMq.model.BetSettlementMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BetSettlementPublisher {

  private final RocketMQTemplate rocketMQTemplate;
  private final String topic;

  public BetSettlementPublisher(
      RocketMQTemplate rocketMQTemplate,
      @Value("${rocketmq.topics.bet-settlements-event-topic.name}") String topic) {
    this.rocketMQTemplate = rocketMQTemplate;
    this.topic = topic;
  }

  public void send(BetSettlement betSettlement) {
    try {
      var betSettlementMessage = BetSettlementMessage.builder().data(betSettlement).build();
      var message =
          MessageBuilder.withPayload(betSettlementMessage)
              .setHeader(RocketMQHeaders.KEYS, createKey(betSettlementMessage))
              .build();
      rocketMQTemplate.syncSend(topic, message);
    } catch (Exception ex) {
      log.error("Error while sending message into rocketmq topic {}", topic, ex);
      throw new ApplicationException(
          "Error while sending message into rocketmq topic " + topic, ex);
    }
  }

  private String createKey(BetSettlementMessage event) {
    var keyPlain = event.getData().getBetId();
    return Base64.getEncoder().encodeToString(keyPlain.getBytes(StandardCharsets.UTF_8));
  }
}
