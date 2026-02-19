package org.betsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.betsync")
public class BetSyncApplication {
  public static void main(String[] args) {
    SpringApplication.run(BetSyncApplication.class, args);
  }
}
