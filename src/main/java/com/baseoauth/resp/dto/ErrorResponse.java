package com.baseoauth.resp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends AbstractResponse {
    
    private String errorCode;
    private String errorMessage;
    private String errorDetails;
    private String path;
    private String method;
    private Integer statusCode;
    
    public ErrorResponse(String errorCode, String errorMessage) {
        super(false, LocalDateTime.now());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = 400;
    }
    
    public ErrorResponse(String errorCode, String errorMessage, Integer statusCode) {
        super(false, LocalDateTime.now());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }
    
    public ErrorResponse(String errorCode, String errorMessage, String errorDetails) {
        super(false, LocalDateTime.now());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.statusCode = 400;
    }
    
    public ErrorResponse(String errorCode, String errorMessage, String errorDetails, Integer statusCode) {
        super(false, LocalDateTime.now());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.statusCode = statusCode;
    }
    
    public ErrorResponse(String errorCode, String errorMessage, String errorDetails, String path, String method) {
        super(false, LocalDateTime.now());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.path = path;
        this.method = method;
        this.statusCode = 400;
    }
    
    // Builder pattern for more flexible construction
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }
    
    public static class ErrorResponseBuilder {
        private String errorCode;
        private String errorMessage;
        private String errorDetails;
        private String path;
        private String method;
        private Integer statusCode;
        
        public ErrorResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public ErrorResponseBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }
        
        public ErrorResponseBuilder errorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }
        
        public ErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }
        
        public ErrorResponseBuilder method(String method) {
            this.method = method;
            return this;
        }
        
        public ErrorResponseBuilder statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        public ErrorResponse build() {
            return new ErrorResponse(errorCode, errorMessage, errorDetails, path, method);
        }
    }
}