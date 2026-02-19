package org.betsync.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.betsync.repository.model.BetEntity;
import org.betsync.repository.model.Status;

@Repository
public interface BetRepository extends JpaRepository<BetEntity, Long> {

  Page<BetEntity> findByEventIdAndPublishedForSettlement(
      String eventId, boolean publishedForSettlement, Pageable pageable);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query("UPDATE BetEntity b SET b.status = :status WHERE b.betId = :betId")
  int updateStatusByBetId(@Param("betId") String betId, @Param("status") Status status);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query("UPDATE BetEntity b SET b.publishedForSettlement = true WHERE b.betId = :betId")
  int markPublishedForSettlement(@Param("betId") String betId);
}
