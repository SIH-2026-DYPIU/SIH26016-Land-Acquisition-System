SELECT
    u.user_id, u.firebase_uid, u.full_name, u.official_email, u.password_hash,
    u.role_id, r.role_name, u.organization_id,
    u.administrative_level, u.state_id, u.district_id,
    u.account_status, u.last_login_at
FROM users u
JOIN roles r ON r.role_id = u.role_id
WHERE LOWER(u.official_email) = LOWER(:official_email)
LIMIT 1;
UPDATE users
SET last_login_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = :user_id;
SELECT p.permission_id, p.permission_name, p.description
FROM permissions p
JOIN role_permissions rp ON rp.permission_id = p.permission_id
WHERE rp.role_id = :role_id
ORDER BY p.permission_name;
SELECT
    usa.scope_assignment_id,
    usa.state_id, s.state_name,
    usa.district_id, d.district_name,
    usa.organization_id, o.organization_name
FROM user_scope_assignments usa
LEFT JOIN states s ON s.state_id = usa.state_id
LEFT JOIN districts d ON d.district_id = usa.district_id
LEFT JOIN organizations o ON o.organization_id = usa.organization_id
WHERE usa.user_id = :user_id
ORDER BY s.state_name, d.district_name, o.organization_name;
INSERT INTO users (
    firebase_uid, full_name, official_email, password_hash, role_id,
    organization_id, administrative_level, state_id, district_id,
    account_status
)
VALUES (
    :firebase_uid, :full_name, :official_email, :password_hash, :role_id,
    :organization_id, :administrative_level, :state_id, :district_id,
    'PENDING'
)
RETURNING user_id;
UPDATE users
SET account_status = :account_status,
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = :user_id;
SELECT state_id, state_name, state_code
FROM states
ORDER BY state_name;
SELECT district_id, district_name
FROM districts
WHERE state_id = :state_id
ORDER BY district_name;
SELECT village_id, village_name, village_code
FROM villages
WHERE district_id = :district_id
ORDER BY village_name;
SELECT
    o.organization_id,
    o.organization_name,
    o.organization_type,
    o.parent_organization_id,
    o.state_id,
    s.state_name,
    o.district_id,
    d.district_name,
    o.is_active
