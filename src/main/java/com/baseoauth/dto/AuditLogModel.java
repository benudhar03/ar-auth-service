package com.baseoauth.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogModel {
    private Long id;
    private String action;
    private String username;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;
}