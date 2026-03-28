-- =====================================================
-- Spring Security OAuth2 Authorization Server Schema
-- For Spring Boot 3.x and Java 21
-- PostgreSQL Compatible
-- =====================================================

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS admin;

-- Set search path
SET search_path TO admin;

-- =====================================================
-- OAuth2 Authorization Schema
-- =====================================================

-- OAuth2 Authorization Table
CREATE TABLE IF NOT EXISTS oauth2_authorization (
    id VARCHAR(100) NOT NULL,
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name VARCHAR(200) NOT NULL,
    authorization_grant_type VARCHAR(100) NOT NULL,
    authorized_scopes VARCHAR(1000),
    attributes BYTEA,
    state VARCHAR(500),
    authorization_code_value BYTEA,
    authorization_code_issued_at TIMESTAMP,
    authorization_code_expires_at TIMESTAMP,
    authorization_code_metadata BYTEA,
    access_token_value BYTEA,
    access_token_issued_at TIMESTAMP,
    access_token_expires_at TIMESTAMP,
    access_token_metadata BYTEA,
    access_token_type VARCHAR(100),
    access_token_scopes VARCHAR(1000),
    oidc_id_token_value BYTEA,
    oidc_id_token_issued_at TIMESTAMP,
    oidc_id_token_expires_at TIMESTAMP,
    oidc_id_token_metadata BYTEA,
    refresh_token_value BYTEA,
    refresh_token_issued_at TIMESTAMP,
    refresh_token_expires_at TIMESTAMP,
    refresh_token_metadata BYTEA,
    user_code_value BYTEA,
    user_code_issued_at TIMESTAMP,
    user_code_expires_at TIMESTAMP,
    user_code_metadata BYTEA,
    device_code_value BYTEA,
    device_code_issued_at TIMESTAMP,
    device_code_expires_at TIMESTAMP,
    device_code_metadata BYTEA,
    PRIMARY KEY (id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_registered_client_id 
    ON oauth2_authorization(registered_client_id);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_principal_name 
    ON oauth2_authorization(principal_name);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_state 
    ON oauth2_authorization(state);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_authorization_code_value 
    ON oauth2_authorization(authorization_code_value);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_access_token_value 
    ON oauth2_authorization(access_token_value);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_refresh_token_value 
    ON oauth2_authorization(refresh_token_value);

-- =====================================================
-- OAuth2 Authorization Consent Schema
-- =====================================================

-- OAuth2 Authorization Consent Table
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name VARCHAR(200) NOT NULL,
    authorities VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

-- =====================================================
-- Registered Clients Schema
-- =====================================================

-- Registered Clients Table
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
    id VARCHAR(100) NOT NULL,
    client_id VARCHAR(100) NOT NULL,
    client_id_issued_at TIMESTAMP NOT NULL,
    client_secret VARCHAR(200),
    client_secret_expires_at TIMESTAMP,
    client_name VARCHAR(200) NOT NULL,
    client_authentication_methods VARCHAR(1000) NOT NULL,
    authorization_grant_types VARCHAR(1000) NOT NULL,
    redirect_uris VARCHAR(1000),
    post_logout_redirect_uris VARCHAR(1000),
    scopes VARCHAR(1000) NOT NULL,
    client_settings VARCHAR(2000) NOT NULL,
    token_settings VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id)
);

-- Create unique index on client_id
CREATE UNIQUE INDEX IF NOT EXISTS idx_oauth2_registered_client_client_id 
    ON oauth2_registered_client(client_id);

-- =====================================================
-- User Tables (Custom)
-- =====================================================

-- User Table
CREATE TABLE IF NOT EXISTS admin.user (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    email_id VARCHAR(100) NOT NULL UNIQUE,
    mobile_no VARCHAR(15),
    password VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    is_credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    is_account_non_locked BOOLEAN NOT NULL DEFAULT true,
    is_account_non_expired BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    failed_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Role Table
CREATE TABLE IF NOT EXISTS admin.role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Permission Table
CREATE TABLE IF NOT EXISTS admin.permission (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Role Mapping Table
CREATE TABLE IF NOT EXISTS admin.user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES admin.user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES admin.role(id) ON DELETE CASCADE
);

-- Role-Permission Mapping Table
CREATE TABLE IF NOT EXISTS admin.role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES admin.role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES admin.permission(id) ON DELETE CASCADE
);

-- =====================================================
-- Create Indexes for Better Performance
-- =====================================================

CREATE INDEX IF NOT EXISTS idx_user_user_name ON admin.user(user_name);
CREATE INDEX IF NOT EXISTS idx_user_email ON admin.user(email_id);
CREATE INDEX IF NOT EXISTS idx_user_enabled ON admin.user(is_enabled);
CREATE INDEX IF NOT EXISTS idx_role_role_name ON admin.role(role_name);
CREATE INDEX IF NOT EXISTS idx_permission_name ON admin.permission(name);

-- =====================================================
-- Update timestamps trigger
-- =====================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_updated_at 
    BEFORE UPDATE ON admin.user 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_role_updated_at 
    BEFORE UPDATE ON admin.role 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_permission_updated_at 
    BEFORE UPDATE ON admin.permission 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Insert Default Data
-- =====================================================

-- Insert Roles
INSERT INTO admin.role (role_name, description) VALUES
    ('ADMIN', 'Administrator with full access'),
    ('USER', 'Regular user with basic access'),
    ('MANAGER', 'Manager with elevated access')
ON CONFLICT (role_name) DO NOTHING;

-- Insert Permissions
INSERT INTO admin.permission (name, description) VALUES
    ('USER_CREATE', 'Can create users'),
    ('USER_READ', 'Can read users'),
    ('USER_UPDATE', 'Can update users'),
    ('USER_DELETE', 'Can delete users'),
    ('ROLE_CREATE', 'Can create roles'),
    ('ROLE_READ', 'Can read roles'),
    ('ROLE_UPDATE', 'Can update roles'),
    ('ROLE_DELETE', 'Can delete roles'),
    ('PERMISSION_READ', 'Can read permissions')
ON CONFLICT (name) DO NOTHING;

-- Assign permissions to ADMIN role
INSERT INTO admin.role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM admin.role r, admin.permission p
WHERE r.role_name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Assign basic permissions to USER role
INSERT INTO admin.role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM admin.role r, admin.permission p
WHERE r.role_name = 'USER' 
    AND p.name IN ('USER_READ')
ON CONFLICT DO NOTHING;

-- Assign manager permissions to MANAGER role
INSERT INTO admin.role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM admin.role r, admin.permission p
WHERE r.role_name = 'MANAGER' 
    AND p.name IN ('USER_READ', 'USER_CREATE', 'USER_UPDATE')
ON CONFLICT DO NOTHING;

-- Insert default admin user (password: admin123 - will be encoded by application)
-- Password should be encoded using BCrypt, this is just a placeholder
INSERT INTO admin.user (
    user_name, 
    email_id, 
    password, 
    is_enabled, 
    is_credentials_non_expired,
    is_account_non_locked,
    is_account_non_expired
) VALUES (
    'admin',
    'admin@example.com',
    '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', -- admin123
    true,
    true,
    true,
    true
) ON CONFLICT (user_name) DO NOTHING;

-- Assign ADMIN role to admin user
INSERT INTO admin.user_role (user_id, role_id)
SELECT u.id, r.id
FROM admin.user u, admin.role r
WHERE u.user_name = 'admin' AND r.role_name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Insert a sample client for OAuth2
INSERT INTO oauth2_registered_client (
    id,
    client_id,
    client_id_issued_at,
    client_secret,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    redirect_uris,
    scopes,
    client_settings,
    token_settings
) VALUES (
    '1',
    'my-trusted-client',
    CURRENT_TIMESTAMP,
    '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', -- secret
    'My Trusted Client',
    'client_secret_basic',
    'authorization_code,refresh_token,client_credentials',
    'http://localhost:8080/login/oauth2/code/client,http://localhost:3000/callback',
    'openid,profile,email,read,write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":"RS256","settings.token.access-token-time-to-live":3600,"settings.token.refresh-token-time-to-live":86400}'
) ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- Migration Notes
-- =====================================================

/*
Migration from Old OAuth2 Tables:

If you have existing data in old tables, you can migrate using these queries:

-- Migrate client details
INSERT INTO oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, 
    client_name, client_authentication_methods, authorization_grant_types, 
    redirect_uris, scopes, client_settings, token_settings)
SELECT 
    client_id,
    client_id,
    CURRENT_TIMESTAMP,
    client_secret,
    client_id,
    'client_secret_basic',
    authorized_grant_types,
    web_server_redirect_uri,
    scope,
    '{"@class":"java.util.Collections$UnmodifiableMap"}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.access-token-time-to-live":' || 
    COALESCE(access_token_validity, 3600) || ',"settings.token.refresh-token-time-to-live":' || 
    COALESCE(refresh_token_validity, 86400) || '}'
FROM oauth_client_details;

-- Note: You'll need to migrate access tokens and refresh tokens programmatically
-- as the schema structure is significantly different.
*/