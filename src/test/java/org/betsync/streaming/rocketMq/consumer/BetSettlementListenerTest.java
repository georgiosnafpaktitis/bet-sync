package org.betsync.streaming.rocketMq.consumer;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.betsync.repository.model.Status;
import org.betsync.service.BetService;
import org.betsync.streaming.rocketMq.model.BetSettlement;
import org.betsync.streaming.rocketMq.model.BetSettlementMessage;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class BetSettlementListenerTest {
  @Mock private BetService betService;
  @Mock private Validator validator;

  @Test
  void onMessageThrowsExceptionWhenNullMessage() {
    BetSettlementListener listener = new BetSettlementListener(betService, validator);

    Assertions.assertThrows(IllegalArgumentException.class, () -> listener.onMessage(null));

    verifyNoInteractions(betService, validator);
  }

  @Test
  void onMessageValidatesAndDelegates() {
    BetSettlementListener listener = new BetSettlementListener(betService, validator);

    BetSettlement settlement = BetSettlement.builder().betId("bet-1").status(Status.WON).build();
    BetSettlementMessage message = new BetSettlementMessage(settlement);

    when(validator.validate(message)).thenReturn(Collections.emptySet());

    listener.onMessage(message);

    verify(validator).validate(message);
    verify(betService).settleBet("bet-1", Status.WON);
  }
}
