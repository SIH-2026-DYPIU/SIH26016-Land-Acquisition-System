INSERT INTO audit_logs (
    user_id, action, entity_table, entity_id,
    old_values, new_values, ip_address, user_agent
)
VALUES (
    :user_id, :action, :entity_table, :entity_id,
    CAST(:old_values_json AS jsonb),
    CAST(:new_values_json AS jsonb),
    CAST(:ip_address AS inet), :user_agent
)
RETURNING audit_log_id;
SELECT
    al.audit_log_id,
    al.action,
    al.entity_table,
    al.entity_id,
    al.old_values,
    al.new_values,
    u.full_name AS user_name,
    al.ip_address,
    al.occurred_at
FROM audit_logs al
LEFT JOIN users u ON u.user_id = al.user_id
WHERE al.entity_table = :entity_table
  AND al.entity_id = :entity_id
ORDER BY al.occurred_at DESC;
SELECT
    al.audit_log_id,
    al.action,
    al.entity_table,
    al.entity_id,
    al.occurred_at
FROM audit_logs al
WHERE al.user_id = :user_id
ORDER BY al.occurred_at DESC
LIMIT :limit_value OFFSET :offset_value;
SELECT
    integration_id,
    system_name,
    integration_type,
    endpoint_reference,
    is_active
FROM external_system_integrations
WHERE is_active = TRUE
ORDER BY system_name;
