package org.betsync.controller.exception.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class ErrorMessage {
  @Schema(description = "HTTP status code", example = "400")
  private int statusCode;

  @Schema(description = "Timestamp of the error", example = "2022-04-05T16:48:00.18Z")
  private Date timestamp;

  @Schema(
      description = "The error message",
      example = "Invalid request - validation errors in the event outcome request parameters")
  private String message;

  @Schema(
      description = "A description containing info about the request",
      example = "uri=/api/events")
  private String description;

  @Schema(description = "Type of the error.", example = "INVALID_PARAM")
  private String errorType;

  @Schema(description = "List of errors related to specific fields.")
  @Setter
  private List<Error> errors;

  public ErrorMessage(
      @JsonProperty("statusCode") int statusCode,
      @JsonProperty("timestamp") Date timestamp,
      @JsonProperty("message") String message,
      @JsonProperty("description") String description) {
    this.statusCode = statusCode;
    this.timestamp = timestamp;
    this.message = message;
    this.description = description;
    this.errorType = null;
    this.errors = new ArrayList<>();
  }

  public ErrorMessage setErrorType(String errorType) {
    this.errorType = errorType;
    return this;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Error {

    @Schema(
        description = "name of the field if error related to specific field.",
        example = "winnerId")
    String field;

    @Schema(
        description = "The error message related to the given field.",
        example = "must not be blank")
    String message;
  }

  /** common errorTypes */
  public enum ErrorType {
    INVALID_PARAM,
    UNKNOWN_ERROR
  }
}
