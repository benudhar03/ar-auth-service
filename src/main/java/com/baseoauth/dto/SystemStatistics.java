package com.baseoauth.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SystemStatistics {
    private long totalUsers;
    private long activeUsers;
    private long totalRoles;
    private long totalPermissions;
    private Map<String, Long> usersByRole;
    private long totalTokensIssued;
    private long activeSessions;
    private double averageResponseTime;
}