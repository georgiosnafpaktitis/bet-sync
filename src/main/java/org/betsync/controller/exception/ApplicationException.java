package org.betsync.controller.exception;

public class ApplicationException extends RuntimeException {

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
