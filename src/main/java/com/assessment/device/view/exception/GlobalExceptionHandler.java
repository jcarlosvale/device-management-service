package com.assessment.device.view.exception;

import com.assessment.device.view.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Void> handle(final EmptyResultDataAccessException exception) {
        log.info("Error: {} ", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handle(final MethodArgumentTypeMismatchException exception) {
        final String name = exception.getName();
        final String type = Objects.requireNonNull(exception.getRequiredType()).getSimpleName();
        final Object value = exception.getValue();
        final String message = String.format("'%s' should be a valid '%s' and '%s' isn't", name, type, value);
        log.info("Error: {} ", message);
        final var errorDto =
                ErrorDto.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .message(message)
                        .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception) {

        final var messages = exception
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.info("Validation errors: {}", messages);
        final var errorDto =
                ErrorDto.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .message(messages)
                        .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleError(final Exception exception) {
        log.error("Error: {}", exception.getMessage(), exception);
        final var errorDto =
                ErrorDto.builder()
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Server error, contact us.")
                        .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }
}