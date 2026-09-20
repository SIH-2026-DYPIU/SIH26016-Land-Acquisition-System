INSERT INTO parcel_field_verifications (
    parcel_id, field_officer_id,
    verification_status, latitude, longitude,
    verified_location, observations, verified_at
)
VALUES (
    :parcel_id, :field_officer_id,
    :verification_status, :latitude, :longitude,
    CASE WHEN :latitude IS NULL OR :longitude IS NULL THEN NULL
         ELSE ST_SetSRID(ST_Point(:longitude, :latitude), 4326) END,
    :observations, :verified_at
)
RETURNING verification_id;
SELECT
    pfv.verification_id,
    pfv.verification_status,
    pfv.latitude,
    pfv.longitude,
    pfv.observations,
    pfv.verified_at,
    u.full_name AS field_officer
FROM parcel_field_verifications pfv
JOIN users u ON u.user_id = pfv.field_officer_id
WHERE pfv.parcel_id = :parcel_id
ORDER BY pfv.created_at DESC;
INSERT INTO field_tasks (
    project_id, parcel_id, assigned_to,
    assigned_by, task_title, task_description, due_date
)
VALUES (
    :project_id, :parcel_id, :assigned_to,
    :assigned_by, :task_title, :task_description, :due_date
)
RETURNING field_task_id;
SELECT
    ft.field_task_id,
    ft.project_id,
    p.project_name,
    ft.parcel_id,
    lp.survey_number,
    ft.task_title,
    ft.task_description,
    ft.task_status,
    ft.due_date
FROM field_tasks ft
JOIN projects p ON p.project_id = ft.project_id
LEFT JOIN land_parcels lp ON lp.parcel_id = ft.parcel_id
WHERE ft.assigned_to = :user_id
ORDER BY ft.due_date NULLS LAST, ft.created_at DESC;
INSERT INTO field_observations (
    field_task_id, parcel_id, submitted_by,
    observation_text, location, observed_at
)
VALUES (
    :field_task_id, :parcel_id, :submitted_by,
    :observation_text,
    CASE WHEN :latitude IS NULL OR :longitude IS NULL THEN NULL
         ELSE ST_SetSRID(ST_Point(:longitude, :latitude), 4326) END,
    :observed_at
)
RETURNING field_observation_id;
INSERT INTO parcel_photos (
    parcel_id, uploaded_by, file_path,
    caption, location, captured_at
)
VALUES (
    :parcel_id, :uploaded_by, :file_path,
    :caption,
    CASE WHEN :latitude IS NULL OR :longitude IS NULL THEN NULL
         ELSE ST_SetSRID(ST_Point(:longitude, :latitude), 4326) END,
    :captured_at
)
RETURNING parcel_photo_id;
