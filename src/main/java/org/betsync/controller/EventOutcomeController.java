package org.betsync.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.betsync.controller.exception.handler.ErrorMessage;
import org.betsync.controller.model.EventOutcomeRequest;
import org.betsync.service.EventOutcomeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RequestMapping("/events")
@RestController
public class EventOutcomeController {

  private final EventOutcomeService eventOutcomeService;

  public EventOutcomeController(EventOutcomeService eventOutcomeService) {
    this.eventOutcomeService = eventOutcomeService;
  }

  @Operation(
      summary = "Posts an event outcome to the messaging system for processing and bet settlement.",
      description =
          "Accepts event outcome details including event ID, event name, and winner ID, then publishes this information to the configured messaging system for downstream processing.")
  @ApiResponse(
      responseCode = "200",
      description = "Message outcome successfully posted to the messaging system")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid request - validation errors in the event outcome request parameters",
      content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @ApiResponse(
      responseCode = "500",
      description = "Unexpected error occurred while processing event outcome",
      content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  @PostMapping("/outcome")
  public void postEvent(@RequestBody @Valid @NotNull EventOutcomeRequest eventOutcomeRequest) {
    eventOutcomeService.postEvent(eventOutcomeRequest);
  }
}
