package org.betsync.service;

import org.betsync.repository.model.Status;

public interface BetService {

  /**
   * Publishes eligible bets for settlement based on the provided event ID This method retrieves
   * bets in batches, evaluates their settlement status based on the provided event winner ID,
   * publishes the settlement for further processing, and marks them as published for settlement.
   *
   * @param eventId The unique identifier of the event for which bets are being processed.
   * @param eventWinnerId The unique identifier of the event winner, used to determine the
   *     settlement result (WON or LOST) for each bet.
   */
  void publishEligibleToSettleBets(String eventId, String eventWinnerId);

  /**
   * Updates the status of a bet identified by the provided bet ID.
   *
   * @param betId the unique identifier of the bet whose status is to be updated.
   * @param status the new status to be assigned to the bet.
   */
  void settleBet(String betId, Status status);
}
