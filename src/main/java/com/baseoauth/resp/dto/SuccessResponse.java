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
public class SuccessResponse extends AbstractResponse {
    
    private String message;
    private Integer statusCode;
    private Object data;
    private String token;
    private Long totalCount;
    private Integer page;
    private Integer size;
    
    public SuccessResponse(String message) {
        super(true, LocalDateTime.now());
        this.message = message;
        this.statusCode = 200;
    }
    
    public SuccessResponse(String message, Integer statusCode) {
        super(true, LocalDateTime.now());
        this.message = message;
        this.statusCode = statusCode;
    }
    
    public SuccessResponse(String message, Object data) {
        super(true, LocalDateTime.now());
        this.message = message;
        this.data = data;
        this.statusCode = 200;
    }
    
    public SuccessResponse(String message, Integer statusCode, Object data) {
        super(true, LocalDateTime.now());
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }
    
    public SuccessResponse(String message, String token) {
        super(true, LocalDateTime.now());
        this.message = message;
        this.token = token;
        this.statusCode = 200;
    }
    
    public SuccessResponse(String message, Integer statusCode, String token) {
        super(true, LocalDateTime.now());
        this.message = message;
        this.statusCode = statusCode;
        this.token = token;
    }
    
    public SuccessResponse(String message, Object data, Long totalCount, Integer page, Integer size) {
        super(true, LocalDateTime.now());
        this.message = message;
        this.data = data;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.statusCode = 200;
    }
    
    // Static factory methods for common use cases
    public static SuccessResponse ok(String message) {
        return new SuccessResponse(message, 200);
    }
    
    public static SuccessResponse ok(String message, Object data) {
        return new SuccessResponse(message, 200, data);
    }
    
    public static SuccessResponse created(String message) {
        return new SuccessResponse(message, 201);
    }
    
    public static SuccessResponse created(String message, Object data) {
        return new SuccessResponse(message, 201, data);
    }
    
    public static SuccessResponse noContent() {
        return new SuccessResponse("No content", 204);
    }
    
    public static SuccessResponse withToken(String message, String token) {
        return new SuccessResponse(message, 200, token);
    }
    
    public static SuccessResponse withPagination(String message, Object data, Long totalCount, Integer page, Integer size) {
        return new SuccessResponse(message, data, totalCount, page, size);
    }
    
    // Builder pattern for more flexible construction
    public static SuccessResponseBuilder builder() {
        return new SuccessResponseBuilder();
    }
    
    public static class SuccessResponseBuilder {
        private String message;
        private Integer statusCode;
        private Object data;
        private String token;
        private Long totalCount;
        private Integer page;
        private Integer size;
        
        public SuccessResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public SuccessResponseBuilder statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        public SuccessResponseBuilder data(Object data) {
            this.data = data;
            return this;
        }
        
        public SuccessResponseBuilder token(String token) {
            this.token = token;
            return this;
        }
        
        public SuccessResponseBuilder totalCount(Long totalCount) {
            this.totalCount = totalCount;
            return this;
        }
        
        public SuccessResponseBuilder page(Integer page) {
            this.page = page;
            return this;
        }
        
        public SuccessResponseBuilder size(Integer size) {
            this.size = size;
            return this;
        }
        
        public SuccessResponse build() {
            SuccessResponse response = new SuccessResponse(message, statusCode, data);
            response.setToken(token);
            response.setTotalCount(totalCount);
            response.setPage(page);
            response.setSize(size);
            return response;
        }
    }
}