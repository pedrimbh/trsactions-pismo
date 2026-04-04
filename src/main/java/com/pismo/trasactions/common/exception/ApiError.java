package com.pismo.trasactions.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ApiError {

    private final LocalDateTime timestamp;
    private final int status;
    private final String message;

    @JsonProperty("path")
    private final String requestPath;

    public ApiError(int status, String message, String requestPath) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.requestPath = requestPath;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestPath() {
        return requestPath;
    }
}

