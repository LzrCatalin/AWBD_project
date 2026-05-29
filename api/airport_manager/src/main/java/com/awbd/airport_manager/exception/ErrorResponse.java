package com.awbd.airport_manager.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {

    private final int status;
    private final String message;
    private final List<String> details;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.details = null;
    }

    public ErrorResponse(int status, String message, List<String> details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
