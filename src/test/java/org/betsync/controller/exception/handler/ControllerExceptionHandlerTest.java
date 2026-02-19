package org.betsync.controller.exception.handler;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import org.betsync.controller.model.EventOutcomeRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerExceptionHandlerTest {
  private static class DummyController {
    @SuppressWarnings("unused")
    void handle(EventOutcomeRequest request) {}
  }

  @Test
  void methodArgumentNotValidExceptionReturnsBadRequest() throws Exception {
    ControllerExceptionHandler handler = new ControllerExceptionHandler();

    Object target = new Object();
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
    bindingResult.addError(new FieldError("target", "eventId", "must not be blank"));

    Method method = DummyController.class.getDeclaredMethod("handle", EventOutcomeRequest.class);
    MethodParameter methodParameter = new MethodParameter(method, 0);
    MethodArgumentNotValidException ex =
        new MethodArgumentNotValidException(methodParameter, bindingResult);

    WebRequest webRequest = mock(WebRequest.class);
    when(webRequest.getDescription(false)).thenReturn("uri=/events/outcome");

    ResponseEntity<ErrorMessage> response = handler.methodArgumentNotValidException(ex, webRequest);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INVALID_PARAM", response.getBody().getErrorType());
    assertEquals(1, response.getBody().getErrors().size());
    assertEquals("eventId", response.getBody().getErrors().get(0).getField());
  }

  @Test
  void globalExceptionHandlerReturnsInternalServerError() {
    ControllerExceptionHandler handler = new ControllerExceptionHandler();
    WebRequest webRequest = mock(WebRequest.class);
    when(webRequest.getDescription(false)).thenReturn("uri=/events/outcome");

    ResponseEntity<ErrorMessage> response =
        handler.globalExceptionHandler(new RuntimeException("boom"), webRequest);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("UNKNOWN_ERROR", response.getBody().getErrorType());
    assertEquals("boom", response.getBody().getMessage());
  }
}
