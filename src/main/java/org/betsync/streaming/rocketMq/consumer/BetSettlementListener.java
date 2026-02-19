package org.betsync.streaming.rocketMq.consumer;

import java.util.Set;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import org.betsync.service.BetService;
import org.betsync.streaming.rocketMq.model.BetSettlementMessage;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/** Handler of bet settlement listening to a RocketMQ topic. */
@Component
@Slf4j
@RocketMQMessageListener(
    topic = "${rocketmq.topics.bet-settlements-event-topic.name}",
    consumerGroup = "${rocketmq.consumers.bet-settlements-consumer.group}",
    maxReconsumeTimes = 16,
    delayLevelWhenNextConsume = 3)
public class BetSettlementListener implements RocketMQListener<BetSettlementMessage> {

  private final BetService betService;
  private final Validator validator;

  public BetSettlementListener(BetService betService, Validator validator) {
    this.betService = betService;
    this.validator = validator;
  }

  @Override
  public void onMessage(BetSettlementMessage betSettlementMessage) {
    if (betSettlementMessage == null) {
      log.error("Bet settlement message must not be null");
      return;
    }
    validateMessage(betSettlementMessage);

    log.debug("Consuming valid settlement bet message with id {}", betSettlementMessage.getId());

    final var settlementData = betSettlementMessage.getData();
    betService.settleBet(settlementData.getBetId(), settlementData.getStatus());
  }

  private void validateMessage(BetSettlementMessage betSettlementMessage) {
    final Set<ConstraintViolation<BetSettlementMessage>> violations =
        validator.validate(betSettlementMessage);

    if (!violations.isEmpty()) {
      log.error("Invalid bet settlement message: {}", violations);
      // Message will be sent to DLQ after max reconsume attempts
    }
  }
}
