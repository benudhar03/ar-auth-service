package com.baseoauth.constant;

public final class PermissionConstants {
    
    // User Permissions
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_READ = "USER_READ";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    public static final String USER_LIST = "USER_LIST";
    public static final String USER_VIEW = "USER_VIEW";
    
    // Role Permissions
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_READ = "ROLE_READ";
    public static final String ROLE_UPDATE = "ROLE_UPDATE";
    public static final String ROLE_DELETE = "ROLE_DELETE";
    public static final String ROLE_ASSIGN = "ROLE_ASSIGN";
    
    // Permission Permissions
    public static final String PERMISSION_CREATE = "PERMISSION_CREATE";
    public static final String PERMISSION_READ = "PERMISSION_READ";
    public static final String PERMISSION_UPDATE = "PERMISSION_UPDATE";
    public static final String PERMISSION_DELETE = "PERMISSION_DELETE";
    
    // Admin Permissions
    public static final String ADMIN_ALL = "ADMIN_ALL";
    public static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";
    public static final String AUDIT_LOG_VIEW = "AUDIT_LOG_VIEW";
    
    // OAuth2 Permissions
    public static final String CLIENT_CREATE = "CLIENT_CREATE";
    public static final String CLIENT_READ = "CLIENT_READ";
    public static final String CLIENT_UPDATE = "CLIENT_UPDATE";
    public static final String CLIENT_DELETE = "CLIENT_DELETE";
    public static final String TOKEN_REVOKE = "TOKEN_REVOKE";
    
    // Common Permission Groups
    public static final String[] USER_PERMISSIONS = {
        USER_CREATE, USER_READ, USER_UPDATE, USER_DELETE, USER_LIST, USER_VIEW
    };
    
    public static final String[] ROLE_PERMISSIONS = {
        ROLE_CREATE, ROLE_READ, ROLE_UPDATE, ROLE_DELETE, ROLE_ASSIGN
    };
    
    public static final String[] ADMIN_PERMISSIONS = {
        ADMIN_ALL, SYSTEM_CONFIG, AUDIT_LOG_VIEW
    };
    
    private PermissionConstants() {
        // Private constructor to prevent instantiation
    }
}