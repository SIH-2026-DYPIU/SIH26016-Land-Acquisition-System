INSERT INTO in_app_notifications (
    recipient_user_id, project_id,
    title, message, notification_type
)
VALUES (
    :recipient_user_id, :project_id,
    :title, :message, :notification_type
)
RETURNING in_app_notification_id;
SELECT
    in_app_notification_id,
    project_id,
    title,
    message,
    notification_type,
    read_at,
    created_at
FROM in_app_notifications
WHERE recipient_user_id = :user_id
ORDER BY created_at DESC
LIMIT :limit_value OFFSET :offset_value;
SELECT COUNT(*) AS unread_count
FROM in_app_notifications
WHERE recipient_user_id = :user_id
  AND read_at IS NULL;
UPDATE in_app_notifications
SET read_at = CURRENT_TIMESTAMP
WHERE in_app_notification_id = :notification_id
  AND recipient_user_id = :user_id;
