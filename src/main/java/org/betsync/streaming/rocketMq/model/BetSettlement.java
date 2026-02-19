package org.betsync.streaming.rocketMq.model;

import org.betsync.repository.model.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BetSettlement {

  @NotBlank String betId;
  @NotNull Status status;
}
