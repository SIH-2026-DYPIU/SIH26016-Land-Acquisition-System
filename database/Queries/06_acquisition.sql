INSERT INTO acquisition_notifications (
    project_id, notification_number, notification_type,
    title, issue_date, publication_date,
    effective_date, description, created_by
)
VALUES (
    :project_id, :notification_number, :notification_type,
    :title, :issue_date, :publication_date,
    :effective_date, :description, :created_by
)
RETURNING acquisition_notification_id;
SELECT
    an.acquisition_notification_id,
    an.notification_number,
    an.notification_type,
    an.title,
    an.issue_date,
    an.publication_date,
    an.effective_date,
    an.description,
    u.full_name AS created_by_name
FROM acquisition_notifications an
LEFT JOIN users u ON u.user_id = an.created_by
WHERE an.project_id = :project_id
ORDER BY an.issue_date DESC NULLS LAST;
INSERT INTO notification_parcels (
    acquisition_notification_id, parcel_id
)
VALUES (:acquisition_notification_id, :parcel_id)
ON CONFLICT DO NOTHING;
SELECT
    lp.parcel_id,
    lp.survey_number,
    lp.subdivision_number,
    lp.area_hectare,
    lp.acquisition_status
FROM notification_parcels np
JOIN land_parcels lp ON lp.parcel_id = np.parcel_id
WHERE np.acquisition_notification_id = :acquisition_notification_id
ORDER BY lp.survey_number;
INSERT INTO claims (
    parcel_id, claimant_owner_id,
    claim_type, claim_text
)
VALUES (
    :parcel_id, :claimant_owner_id,
    :claim_type, :claim_text
)
RETURNING claim_id;
SELECT
    c.claim_id,
    c.claim_type,
    c.claim_text,
    c.claim_status,
    c.submitted_at,
    po.owner_name AS claimant_name,
    u.full_name AS resolved_by_name,
    c.resolved_at,
    c.resolution_remarks
FROM claims c
LEFT JOIN parcel_owners po ON po.owner_id = c.claimant_owner_id
LEFT JOIN users u ON u.user_id = c.resolved_by
WHERE c.parcel_id = :parcel_id
ORDER BY c.submitted_at DESC;
UPDATE claims
SET claim_status = :claim_status,
    resolved_by = :resolved_by,
    resolved_at = CURRENT_TIMESTAMP,
    resolution_remarks = :resolution_remarks
WHERE claim_id = :claim_id;
INSERT INTO awards (
    project_id, parcel_id, award_number,
    award_date, award_amount, declared_by
)
VALUES (
    :project_id, :parcel_id, :award_number,
    :award_date, :award_amount, :declared_by
)
RETURNING award_id;
SELECT
    a.award_id,
    a.award_number,
    a.award_date,
    a.award_amount,
    a.award_status,
    lp.parcel_id,
    lp.survey_number,
    u.full_name AS declared_by_name
FROM awards a
JOIN land_parcels lp ON lp.parcel_id = a.parcel_id
LEFT JOIN users u ON u.user_id = a.declared_by
WHERE a.project_id = :project_id
ORDER BY a.award_date DESC NULLS LAST;
UPDATE awards
SET award_status = :award_status,
    award_date = COALESCE(:award_date, award_date)
WHERE award_id = :award_id;
