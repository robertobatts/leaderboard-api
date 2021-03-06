package com.robertobatts.leaderboard.controller;

import com.robertobatts.leaderboard.dto.ApiExceptionResponse;
import com.robertobatts.leaderboard.exception.NotFoundException;
import com.robertobatts.leaderboard.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity handleValidationException(ValidationException e, ServletWebRequest request) {
        logExceptionWithWebRequestInfo(e, request);
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity handleNotFoundException(NotFoundException e, ServletWebRequest request) {
        logExceptionWithWebRequestInfo(e, request);
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity handleException(Exception e, ServletWebRequest request) {
        logExceptionWithWebRequestInfo(e, request);
        ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logExceptionWithWebRequestInfo(Exception e, ServletWebRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((name, values) -> params.put(name, Arrays.toString(values)));
        logger.error("REST endpoint error :: uri=" + request.getRequest().getRequestURI() + ", parameters=" + params, e);
    }

}