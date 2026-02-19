package org.betsync.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.betsync.repository.BetRepository;
import org.betsync.repository.model.BetEntity;
import org.betsync.repository.model.Status;
import org.betsync.streaming.rocketMq.model.BetSettlement;
import org.betsync.streaming.rocketMq.producer.BetSettlementPublisher;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BetServiceImpl implements BetService {

  public static final int BATCH_SIZE = 100;
  public static final int FIRST_PAGE_NUMBER = 0;

  private final BetRepository betRepository;
  private final BetSettlementPublisher betSettlementPublisher;

  public BetServiceImpl(
      BetRepository betRepository, BetSettlementPublisher betSettlementPublisher) {
    this.betRepository = betRepository;
    this.betSettlementPublisher = betSettlementPublisher;
  }

  @Override
  public void publishEligibleToSettleBets(String eventId, String eventWinnerId) {

    final Pageable pageable = PageRequest.of(FIRST_PAGE_NUMBER, BATCH_SIZE);

    while (true) {
      List<BetEntity> batch =
          betRepository
              .findByEventIdAndPublishedForSettlement(eventId, false, pageable)
              .getContent();
      if (batch.isEmpty()) {
        return;
      }
      for (BetEntity betEntity : batch) {
        Status status = determineSettlementStatus(eventWinnerId, betEntity.getEventWinnerId());

        betSettlementPublisher.send(
            BetSettlement.builder().betId(betEntity.getBetId()).status(status).build());

        betEntity.setPublishedForSettlement(true);
      }
      betRepository.saveAll(batch);
    }
  }

  private static Status determineSettlementStatus(String eventWinnerId, String betEventWinnerId) {
    return Objects.equals(eventWinnerId, betEventWinnerId) ? Status.WON : Status.LOST;
  }

  @Override
  public void settleBet(String betId, Status status) {
    betRepository.updateStatusByBetId(betId, status);
  }
}
