package com.vnpt.intership.news.api.v1.exception;

import com.vnpt.intership.news.api.v1.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiExceptionResponse handleException(MethodArgumentNotValidException methodEx, WebRequest request) {
        ApiExceptionResponse response = new ApiExceptionResponse();

        methodEx.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMsg = error.getDefaultMessage();
            response.addValidationError(ApiError.builder().msg(errorMsg).field(fieldName).build());
        });

        response.setCode(ErrorCode.BAD_REQUEST);
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ErrorCode.BAD_REQUEST.toString());

        return response;
    }

    @ExceptionHandler(value = { UserNotFoundException.class })
    public ResponseEntity<?> handleExceptionChecked(Exception e) {
        log.error("EntityException: {}", e.getMessage());
        ApiExceptionResponse response = new ApiExceptionResponse();
        response.setCode(ErrorCode.BAD_REQUEST);
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({DataAccessException.class})
    public String databaseException() {
        return "database error";
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void conflict() {

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleUnwantedException(Exception e, WebRequest request) {
        // Hide error details from client, only show in log
        log.error("SERVER ERROR: {}", e.getMessage());

        ApiExceptionResponse response = new ApiExceptionResponse();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setTimestamp(LocalDateTime.now());
        response.setCode(ErrorCode.SERVER_ERROR);
        response.setMessage("Unknown error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
