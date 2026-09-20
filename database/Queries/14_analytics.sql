SELECT
    p.project_id,
    p.project_name,
    p.project_status,
    p.proposed_area_hectare,
    COALESCE(SUM(lp.area_hectare), 0) AS parcel_area_hectare,
    COUNT(lp.parcel_id) AS parcel_count,
    COUNT(lp.parcel_id)
        FILTER (WHERE lp.acquisition_status = 'ACQUIRED')
        AS acquired_parcel_count,
    COALESCE(SUM(lp.area_hectare)
        FILTER (WHERE lp.acquisition_status = 'ACQUIRED'), 0)
        AS acquired_area_hectare,
    COUNT(DISTINCT af.family_id) AS affected_family_count,
    COUNT(DISTINCT af.family_id)
        FILTER (WHERE af.displacement_status = 'DISPLACED')
        AS displaced_family_count,
    COUNT(DISTINCT a.award_id) AS award_count,
    COALESCE(SUM(a.award_amount), 0) AS total_award_amount,
    COALESCE((
        SELECT SUM(ca.assessed_amount)
        FROM compensation_assessments ca
        JOIN awards ax ON ax.award_id = ca.award_id
        WHERE ax.project_id = p.project_id
    ), 0) AS compensation_assessed,
    COALESCE((
        SELECT SUM(cp.amount_paid)
        FROM compensation_payments cp
        JOIN compensation_assessments ca2
          ON ca2.compensation_assessment_id = cp.compensation_assessment_id
        JOIN awards ax2 ON ax2.award_id = ca2.award_id
        WHERE ax2.project_id = p.project_id
    ), 0) AS compensation_paid
FROM projects p
LEFT JOIN land_parcels lp ON lp.project_id = p.project_id
LEFT JOIN affected_families af ON af.project_id = p.project_id
LEFT JOIN awards a ON a.project_id = p.project_id
WHERE p.project_id = :project_id
GROUP BY p.project_id, p.project_name,
         p.project_status, p.proposed_area_hectare;
SELECT
    s.state_id,
    s.state_name,
    COUNT(DISTINCT p.project_id) AS project_count,
    COALESCE(SUM(p.proposed_area_hectare), 0)
        AS proposed_area_hectare,
    COUNT(DISTINCT lp.parcel_id)
        FILTER (WHERE lp.acquisition_status = 'ACQUIRED')
        AS acquired_parcel_count,
    COALESCE(SUM(lp.area_hectare)
        FILTER (WHERE lp.acquisition_status = 'ACQUIRED'), 0)
        AS acquired_area_hectare,
    COUNT(DISTINCT af.family_id) AS affected_family_count
FROM states s
LEFT JOIN projects p ON p.state_id = s.state_id
LEFT JOIN land_parcels lp ON lp.project_id = p.project_id
LEFT JOIN affected_families af ON af.project_id = p.project_id
GROUP BY s.state_id, s.state_name
ORDER BY s.state_name;
SELECT
    d.district_id,
    d.district_name,
    s.state_name,
    COUNT(DISTINCT p.project_id) AS project_count,
    COALESCE(SUM(p.proposed_area_hectare), 0)
        AS proposed_area_hectare,
    COUNT(DISTINCT lp.parcel_id)
        FILTER (WHERE lp.acquisition_status = 'ACQUIRED')
        AS acquired_parcel_count,
    COUNT(DISTINCT af.family_id) AS affected_family_count
FROM districts d
JOIN states s ON s.state_id = d.state_id
LEFT JOIN projects p ON p.district_id = d.district_id
LEFT JOIN land_parcels lp ON lp.project_id = p.project_id
LEFT JOIN affected_families af ON af.project_id = p.project_id
WHERE (:state_id IS NULL OR d.state_id = :state_id)
GROUP BY d.district_id, d.district_name, s.state_name
ORDER BY s.state_name, d.district_name;
SELECT
    acquisition_status,
    COUNT(*) AS parcel_count,
    COALESCE(SUM(area_hectare), 0) AS area_hectare
FROM land_parcels
WHERE project_id = :project_id
GROUP BY acquisition_status
ORDER BY acquisition_status;
SELECT
    COALESCE(SUM(ca.assessed_amount), 0) AS assessed_amount,
    COALESCE(SUM(ca.approved_amount), 0) AS approved_amount,
    COALESCE(SUM(cp.total_paid), 0) AS paid_amount
FROM compensation_assessments ca
JOIN awards a ON a.award_id = ca.award_id
LEFT JOIN (
    SELECT compensation_assessment_id, SUM(amount_paid) AS total_paid
    FROM compensation_payments
    GROUP BY compensation_assessment_id
) cp ON cp.compensation_assessment_id = ca.compensation_assessment_id
WHERE a.project_id = :project_id;
SELECT
    COUNT(DISTINCT af.family_id) AS affected_families,
    COUNT(DISTINCT af.family_id)
        FILTER (WHERE af.displacement_status = 'DISPLACED')
        AS displaced_families,
    COUNT(DISTINCT rb.rr_benefit_id)
        FILTER (WHERE rb.benefit_status = 'APPROVED')
        AS approved_benefits,
    COUNT(DISTINCT rd.rr_delivery_id)
        FILTER (WHERE rd.delivery_status = 'COMPLETED')
        AS completed_deliveries
FROM affected_families af
LEFT JOIN rr_benefits rb ON rb.family_id = af.family_id
LEFT JOIN rr_deliveries rd ON rd.rr_benefit_id = rb.rr_benefit_id
WHERE af.project_id = :project_id;
INSERT INTO analytics_snapshots (
    project_id, state_id, snapshot_date,
    area_proposed_hectare, area_notified_hectare,
    area_acquired_hectare, compensation_assessed,
    compensation_paid, affected_family_count,
    displaced_family_count, rr_completed_family_count,
    parcels_in_possession, parcels_total
)
SELECT
    p.project_id,
    p.state_id,
    CURRENT_DATE,
    p.proposed_area_hectare,
    COALESCE((
        SELECT SUM(lp.area_hectare)
        FROM land_parcels lp
        JOIN notification_parcels np ON np.parcel_id = lp.parcel_id
        JOIN acquisition_notifications an
          ON an.acquisition_notification_id =
             np.acquisition_notification_id
        WHERE lp.project_id = p.project_id
    ), 0),
    COALESCE((
        SELECT SUM(lp.area_hectare)
        FROM land_parcels lp
        WHERE lp.project_id = p.project_id
          AND lp.acquisition_status = 'ACQUIRED'
    ), 0),
    COALESCE((
        SELECT SUM(ca.assessed_amount)
        FROM compensation_assessments ca
        JOIN awards a ON a.award_id = ca.award_id
        WHERE a.project_id = p.project_id
    ), 0),
    COALESCE((
        SELECT SUM(cp.amount_paid)
        FROM compensation_payments cp
        JOIN compensation_assessments ca ON
             ca.compensation_assessment_id =
             cp.compensation_assessment_id
        JOIN awards a ON a.award_id = ca.award_id
        WHERE a.project_id = p.project_id
    ), 0),
    (SELECT COUNT(*) FROM affected_families af
     WHERE af.project_id = p.project_id),
    (SELECT COUNT(*) FROM affected_families af
     WHERE af.project_id = p.project_id
       AND af.displacement_status = 'DISPLACED'),
    (SELECT COUNT(DISTINCT af.family_id)
     FROM affected_families af
     JOIN rr_benefits rb ON rb.family_id = af.family_id
     JOIN rr_deliveries rd ON rd.rr_benefit_id = rb.rr_benefit_id
     WHERE af.project_id = p.project_id
       AND rd.delivery_status = 'COMPLETED'),
    (SELECT COUNT(*)
     FROM land_parcels lp
     WHERE lp.project_id = p.project_id
       AND EXISTS (
           SELECT 1
           FROM possession_records pr
           WHERE pr.parcel_id = lp.parcel_id
             AND pr.possession_status = 'COMPLETED'
       )),
    (SELECT COUNT(*) FROM land_parcels lp
     WHERE lp.project_id = p.project_id)
FROM projects p
WHERE p.project_id = :project_id
ON CONFLICT (project_id, state_id, snapshot_date)
DO UPDATE SET
    area_proposed_hectare = EXCLUDED.area_proposed_hectare,
    area_notified_hectare = EXCLUDED.area_notified_hectare,
    area_acquired_hectare = EXCLUDED.area_acquired_hectare,
    compensation_assessed = EXCLUDED.compensation_assessed,
    compensation_paid = EXCLUDED.compensation_paid,
    affected_family_count = EXCLUDED.affected_family_count,
    displaced_family_count = EXCLUDED.displaced_family_count,
    rr_completed_family_count = EXCLUDED.rr_completed_family_count,
    parcels_in_possession = EXCLUDED.parcels_in_possession,
    parcels_total = EXCLUDED.parcels_total;
SELECT
    snapshot_date,
    area_proposed_hectare,
    area_notified_hectare,
    area_acquired_hectare,
    compensation_assessed,
    compensation_paid,
    affected_family_count,
    displaced_family_count,
    rr_completed_family_count,
    parcels_in_possession,
    parcels_total
FROM analytics_snapshots
WHERE project_id = :project_id
ORDER BY snapshot_date DESC;
SELECT
    p.project_id,
    p.project_code,
    p.project_name,
    p.project_category,
    s.state_name,
    d.district_name,
    p.project_location,
    p.proposed_area_hectare,
    p.project_status,
    p.planned_start_date,
    p.planned_end_date
FROM projects p
JOIN states s ON s.state_id = p.state_id
LEFT JOIN districts d ON d.district_id = p.district_id
WHERE p.is_public = TRUE
  AND (:state_id IS NULL OR p.state_id = :state_id)
ORDER BY p.project_name;
SELECT
    lp.parcel_id,
    lp.survey_number,
    lp.area_hectare,
    lp.acquisition_status,
    ST_AsGeoJSON(lp.geom)::json AS geometry
FROM land_parcels lp
JOIN projects p ON p.project_id = lp.project_id
WHERE p.project_id = :project_id
  AND p.is_public = TRUE
  AND lp.geom IS NOT NULL;
SELECT
    (SELECT COUNT(*) FROM projects) AS total_projects,
    (SELECT COUNT(*) FROM land_parcels) AS total_parcels,
    (SELECT COALESCE(SUM(area_hectare), 0) FROM land_parcels)
        AS total_parcel_area_hectare,
    (SELECT COUNT(*) FROM land_parcels
     WHERE acquisition_status = 'ACQUIRED')
        AS acquired_parcels,
    (SELECT COUNT(*) FROM affected_families)
        AS affected_families,
    (SELECT COUNT(*) FROM affected_families
     WHERE displacement_status = 'DISPLACED')
        AS displaced_families,
    (SELECT COALESCE(SUM(amount_paid), 0)
     FROM compensation_payments
     WHERE payment_status = 'COMPLETED')
        AS compensation_paid;
SELECT project_status, COUNT(*) AS project_count
FROM projects
GROUP BY project_status
ORDER BY project_count DESC;
SELECT
    pr.proposal_review_id,
    pr.proposal_id,
    p.project_name,
    pr.review_level,
    pr.decision,
    pr.reviewed_at
FROM proposal_reviews pr
JOIN proposals prop ON prop.proposal_id = pr.proposal_id
JOIN projects p ON p.project_id = prop.project_id
WHERE pr.decision IN ('SUBMITTED', 'UNDER_REVIEW')
ORDER BY pr.reviewed_at ASC;
SELECT
    cp.payment_id,
    p.project_name,
    po.owner_name,
    cp.amount_paid,
    cp.payment_date,
    cp.payment_status
FROM compensation_payments cp
JOIN compensation_assessments ca
  ON ca.compensation_assessment_id = cp.compensation_assessment_id
JOIN awards a ON a.award_id = ca.award_id
JOIN projects p ON p.project_id = a.project_id
JOIN parcel_owners po ON po.owner_id = ca.owner_id
WHERE cp.payment_status <> 'COMPLETED'
ORDER BY cp.payment_date NULLS FIRST;
SELECT
    rb.rr_benefit_id,
    af.project_id,
    af.family_id,
    af.family_reference_code,
    af.head_of_family_name,
    rb.benefit_type,
    rb.benefit_status
FROM rr_benefits rb
JOIN affected_families af ON af.family_id = rb.family_id
WHERE rb.benefit_status <> 'COMPLETED'
ORDER BY af.project_id, af.family_id;
SELECT
    lp.parcel_id,
    p.project_name,
    lp.survey_number,
    lp.area_hectare,
    lp.acquisition_status
FROM land_parcels lp
JOIN projects p ON p.project_id = lp.project_id
WHERE lp.acquisition_status = 'ACQUIRED'
  AND NOT EXISTS (
      SELECT 1
      FROM possession_records pr
      WHERE pr.parcel_id = lp.parcel_id
        AND pr.possession_status = 'COMPLETED'
  )
ORDER BY p.project_name, lp.survey_number;
