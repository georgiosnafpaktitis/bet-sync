package org.betsync.controller.exception.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

import static org.betsync.controller.exception.handler.ErrorMessage.ErrorType.INVALID_PARAM;
import static org.betsync.controller.exception.handler.ErrorMessage.ErrorType.UNKNOWN_ERROR;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<ErrorMessage> methodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest request) {

    BindingResult result = ex.getBindingResult();
    List<ErrorMessage.Error> errors = new ArrayList<>();
    if (result.hasErrors()) {
      errors =
          result.getAllErrors().stream()
              .map(
                  objectError -> {
                    String field = null;
                    if (objectError instanceof FieldError fieldError) {
                      field = fieldError.getField();
                    }
                    return new ErrorMessage.Error(field, objectError.getDefaultMessage());
                  })
              .toList();
    }

    ErrorMessage message =
        new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false))
            .setErrorType(INVALID_PARAM.name());
    message.setErrors(errors);
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
    ErrorMessage message =
        new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false))
            .setErrorType(UNKNOWN_ERROR.name());
    log.error("Error while request on {}. Error: {}", request, ex.getMessage(), ex);
    return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
