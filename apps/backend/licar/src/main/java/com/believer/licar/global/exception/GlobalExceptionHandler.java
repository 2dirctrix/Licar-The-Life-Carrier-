package com.believer.licar.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handlerCustomException(CustomException e) {
        log.error("handleCustomException : {}", e.getErrorCode());
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());

        return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handlerException(Exception e) {
        log.error("unhandledException : {}", e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
