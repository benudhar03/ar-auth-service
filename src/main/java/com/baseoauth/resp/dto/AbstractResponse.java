package com.baseoauth.resp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractResponse {
    
    private boolean success;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("formatted_timestamp")
    public String getFormattedTimestamp() {
        if (timestamp != null) {
            return timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return null;
    }
    
    public AbstractResponse(boolean success, LocalDateTime timestamp) {
        this.success = success;
        this.timestamp = timestamp;
    }
}