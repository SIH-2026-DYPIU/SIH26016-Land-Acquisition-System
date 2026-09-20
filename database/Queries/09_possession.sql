INSERT INTO possession_records (
    parcel_id, possession_status,
    possession_date, handed_over_by,
    received_by, remarks
)
VALUES (
    :parcel_id, :possession_status,
    :possession_date, :handed_over_by,
    :received_by, :remarks
)
RETURNING possession_record_id;
SELECT DISTINCT ON (pr.parcel_id)
    pr.parcel_id,
    pr.possession_record_id,
    pr.possession_status,
    pr.possession_date,
    hu.full_name AS handed_over_by_name,
    ru.full_name AS received_by_name,
    pr.remarks
FROM possession_records pr
LEFT JOIN users hu ON hu.user_id = pr.handed_over_by
LEFT JOIN users ru ON ru.user_id = pr.received_by
WHERE pr.parcel_id = :parcel_id
ORDER BY pr.parcel_id, pr.created_at DESC;
SELECT
    COUNT(*) AS total_parcels,
    COUNT(*) FILTER (WHERE lp.acquisition_status = 'ACQUIRED') AS acquired_parcels,
    COUNT(*) FILTER (WHERE latest.possession_status = 'COMPLETED') AS parcels_in_possession
FROM land_parcels lp
LEFT JOIN LATERAL (
    SELECT pr.possession_status
    FROM possession_records pr
    WHERE pr.parcel_id = lp.parcel_id
    ORDER BY pr.created_at DESC
    LIMIT 1
) latest ON TRUE
WHERE lp.project_id = :project_id;
