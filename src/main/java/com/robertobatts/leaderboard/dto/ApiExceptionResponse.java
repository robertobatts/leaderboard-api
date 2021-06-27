package com.robertobatts.leaderboard.dto;

import org.springframework.http.HttpStatus;

public final class ApiExceptionResponse {

    private final String message;

    private final HttpStatus status;

    public ApiExceptionResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ApiExceptionResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
