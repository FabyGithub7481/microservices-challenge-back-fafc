package com.client_person_service.config;


import com.client_person_service.exceptions.ClientNotFoundException;
import com.client_person_service.exceptions.ClientAlreadyExistsException;
import com.client_person_service.exceptions.DataIntegrityViolationException;
import com.client_person_service.exceptions.UserNotFoundException;
import com.client_person_service.models.ErrorResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @ExceptionHandler({NoHandlerFoundException.class})
  public ResponseEntity<ErrorResponse> noResourceFoundException(NoHandlerFoundException ex) {
    ErrorResponse errorManagement = new ErrorResponse();
    errorManagement.setDate(new Date());
    errorManagement.setError("EndPoint no encontrado!!");
    errorManagement.setMessage(ex.getMessage());
    errorManagement.setStatus(HttpStatus.NOT_FOUND.value());
    return ResponseEntity.status(400).body(errorManagement);
  }

  @ExceptionHandler({UserNotFoundException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> nullPointerException(Exception ex) {
    Map<String, Object> errorManagement = new HashMap<>();
    errorManagement.put("fecha", new Date());
    errorManagement.put("error", "Cliente no existe!!");
    errorManagement.put("mensaje", ex.getMessage());
    errorManagement.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    return errorManagement; // el status envia como anotacion

  }

  @ExceptionHandler(ClientNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleClienteNotFoundException(
      ClientNotFoundException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", dateFormat.format(new Date()));
    errorResponse.put("status", HttpStatus.NOT_FOUND.value());
    errorResponse.put("error", "Not Found");
    errorResponse.put("message", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ClientAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleClientAlreadyExistsException(
      ClientAlreadyExistsException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", dateFormat.format(new Date()));
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "Bad Request");
    errorResponse.put("message", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", dateFormat.format(new Date()));
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "La longitud del campo es incorrecta");
    errorResponse.put("message", ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<Map<String, Object>> handleDataAccessException(
      DataAccessException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", dateFormat.format(new Date()));
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "La longitud del campo es incorrecta");
    String errorMessage = extractRelevantMessage(ex.getMessage());
    errorResponse.put("message", errorMessage);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Método para extraer la parte relevante del mensaje
  private String extractRelevantMessage(String message) {
    Pattern pattern = Pattern.compile("Data too long for column '[^']+'");
    Matcher matcher = pattern.matcher(message);
    if (matcher.find()) {
      return matcher.group();
    }
    return "Error desconocido";
  }

}
