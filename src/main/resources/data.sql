-- =====================================================
-- Data.sql - Default data for development
-- =====================================================

-- Set search path
SET search_path TO admin;

-- Insert default roles if not exists
INSERT INTO admin.role (role_name, description) VALUES
    ('ADMIN', 'Administrator with full access'),
    ('USER', 'Regular user with basic access'),
    ('MANAGER', 'Manager with elevated access')
ON CONFLICT (role_name) DO NOTHING;

-- Insert default permissions if not exists
INSERT INTO admin.permission (name, description) VALUES
    ('USER_CREATE', 'Can create users'),
    ('USER_READ', 'Can read users'),
    ('USER_UPDATE', 'Can update users'),
    ('USER_DELETE', 'Can delete users')
ON CONFLICT (name) DO NOTHING;

-- Insert default admin user if not exists
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

-- Assign admin role to admin user
INSERT INTO admin.user_role (user_id, role_id)
SELECT u.id, r.id
FROM admin.user u, admin.role r
WHERE u.user_name = 'admin' AND r.role_name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Insert default OAuth2 client
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
    'authorization_code,refresh_token,client_credentials,password',
    'http://localhost:8080/login/oauth2/code/client,http://localhost:3000/callback',
    'openid,profile,email,read,write,trust',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":"RS256","settings.token.access-token-time-to-live":3600,"settings.token.refresh-token-time-to-live":86400}'
) ON CONFLICT (id) DO NOTHING;