INSERT INTO external_system_integrations (
    system_name, integration_type,
    endpoint_reference, is_active, configuration
)
VALUES (
    :system_name, :integration_type,
    :endpoint_reference, :is_active,
    CAST(:configuration_json AS jsonb)
)
RETURNING integration_id;
INSERT INTO integration_sync_logs (
    integration_id, sync_status,
    records_processed, error_details
)
VALUES (
    :integration_id, :sync_status,
    :records_processed, :error_details
)
RETURNING sync_log_id;
UPDATE integration_sync_logs
SET sync_completed_at = CURRENT_TIMESTAMP,
    sync_status = :sync_status,
    records_processed = :records_processed,
    error_details = :error_details
WHERE sync_log_id = :sync_log_id;
