package org.betsync.streaming.rocketMq.model;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

  @NotBlank private String id;
  private ZonedDateTime time;
}
