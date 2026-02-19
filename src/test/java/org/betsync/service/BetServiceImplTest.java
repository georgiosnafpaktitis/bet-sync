package org.betsync.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.betsync.repository.BetRepository;
import org.betsync.repository.model.BetEntity;
import org.betsync.repository.model.Status;
import org.betsync.streaming.rocketMq.model.BetSettlement;
import org.betsync.streaming.rocketMq.producer.BetSettlementPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetServiceImplTest {
  @Mock private BetRepository betRepository;
  @Mock private BetSettlementPublisher betSettlementPublisher;

  @Test
  void publishEligibleToSettleBetsProcessesBatches() {
    BetServiceImpl service = new BetServiceImpl(betRepository, betSettlementPublisher);

    BetEntity betWin = new BetEntity();
    betWin.setBetId("bet-1");
    betWin.setEventWinnerId("winner-1");
    betWin.setBetAmount(new BigDecimal("10.00"));

    BetEntity betLose = new BetEntity();
    betLose.setBetId("bet-2");
    betLose.setEventWinnerId("winner-2");
    betLose.setBetAmount(new BigDecimal("5.00"));

    when(betRepository.findByEventIdAndPublishedForSettlement(
            eq("event-1"), eq(false), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(betWin, betLose)))
        .thenReturn(new PageImpl<>(List.of()));

    service.publishEligibleToSettleBets("event-1", "winner-1");

    ArgumentCaptor<BetSettlement> settlementCaptor = ArgumentCaptor.forClass(BetSettlement.class);
    verify(betSettlementPublisher, times(2)).send(settlementCaptor.capture());

    List<BetSettlement> settlements = settlementCaptor.getAllValues();
    assertEquals(Status.WON, settlements.get(0).getStatus());
    assertEquals(Status.LOST, settlements.get(1).getStatus());

    ArgumentCaptor<List<BetEntity>> batchCaptor = ArgumentCaptor.forClass(List.class);
    verify(betRepository).saveAll(batchCaptor.capture());
    List<BetEntity> saved = batchCaptor.getValue();
    assertEquals(true, saved.get(0).isPublishedForSettlement());
    assertEquals(true, saved.get(1).isPublishedForSettlement());
  }

  @Test
  void settleBetUpdatesRepository() {
    BetServiceImpl service = new BetServiceImpl(betRepository, betSettlementPublisher);

    service.settleBet("bet-1", Status.WON);

    verify(betRepository).updateStatusByBetId("bet-1", Status.WON);
  }
}
