BEGIN;
SELECT
    u.user_id, u.full_name, u.official_email, u.password_hash,
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
    full_name, official_email, password_hash, role_id,
    organization_id, administrative_level, state_id, district_id,
    account_status
)
VALUES (
    :full_name, :official_email, :password_hash, :role_id,
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
FROM organizations o
LEFT JOIN states s ON s.state_id = o.state_id
LEFT JOIN districts d ON d.district_id = o.district_id
WHERE o.is_active = TRUE
ORDER BY o.organization_name;
INSERT INTO projects (
    project_code, project_name, description, project_category,
    implementing_organization_id, created_by, state_id, district_id,
    project_location, proposed_area_hectare,
    planned_start_date, planned_end_date, is_public
)
VALUES (
    :project_code, :project_name, :description, :project_category,
    :implementing_organization_id, :created_by, :state_id, :district_id,
    :project_location, :proposed_area_hectare,
    :planned_start_date, :planned_end_date, :is_public
)
RETURNING project_id;
SELECT
    p.project_id, p.project_code, p.project_name,
    p.project_category, p.project_status,
    p.proposed_area_hectare,
    s.state_name, d.district_name,
    o.organization_name AS implementing_organization,
    p.planned_start_date, p.planned_end_date,
    p.actual_start_date, p.actual_end_date,
    pw.workflow_status,
    ws.stage_name AS current_stage
FROM projects p
JOIN states s ON s.state_id = p.state_id
LEFT JOIN districts d ON d.district_id = p.district_id
JOIN organizations o ON o.organization_id = p.implementing_organization_id
LEFT JOIN project_workflows pw ON pw.project_id = p.project_id
LEFT JOIN workflow_stages ws ON ws.workflow_stage_id = pw.current_stage_id
WHERE (:state_id IS NULL OR p.state_id = :state_id)
  AND (:district_id IS NULL OR p.district_id = :district_id)
  AND (:project_status IS NULL OR p.project_status = :project_status)
ORDER BY p.updated_at DESC;
SELECT
    p.*,
    s.state_name,
    d.district_name,
    o.organization_name AS implementing_organization,
    u.full_name AS created_by_name
FROM projects p
JOIN states s ON s.state_id = p.state_id
LEFT JOIN districts d ON d.district_id = p.district_id
JOIN organizations o ON o.organization_id = p.implementing_organization_id
JOIN users u ON u.user_id = p.created_by
WHERE p.project_id = :project_id;
UPDATE projects
SET project_name = :project_name,
    description = :description,
    project_category = :project_category,
    district_id = :district_id,
    project_location = :project_location,
    proposed_area_hectare = :proposed_area_hectare,
    project_status = :project_status,
    planned_start_date = :planned_start_date,
    planned_end_date = :planned_end_date,
    actual_start_date = :actual_start_date,
    actual_end_date = :actual_end_date,
    is_public = :is_public,
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = :project_id;
SELECT
    pm.project_member_id,
    pm.user_id,
    u.full_name,
    u.official_email,
    pm.project_role,
    pm.assigned_at
FROM project_members pm
JOIN users u ON u.user_id = pm.user_id
WHERE pm.project_id = :project_id
ORDER BY u.full_name;
INSERT INTO project_members (
    project_id, user_id, project_role, assigned_by
)
VALUES (:project_id, :user_id, :project_role, :assigned_by)
ON CONFLICT (project_id, user_id, project_role) DO NOTHING
RETURNING project_member_id;
INSERT INTO proposals (
    project_id, proposal_version, proposal_title,
    proposal_description, proposed_area_hectare, submitted_by
)
VALUES (
    :project_id,
    COALESCE(
        (SELECT MAX(proposal_version) + 1
         FROM proposals WHERE project_id = :project_id), 1
    ),
    :proposal_title, :proposal_description,
    :proposed_area_hectare, :submitted_by
)
RETURNING proposal_id, proposal_version;
SELECT
    pr.proposal_id, pr.proposal_version, pr.proposal_title,
    pr.proposal_description, pr.proposed_area_hectare,
    pr.proposal_status, pr.submitted_at,
    u.full_name AS submitted_by_name
FROM proposals pr
LEFT JOIN users u ON u.user_id = pr.submitted_by
WHERE pr.project_id = :project_id
ORDER BY pr.proposal_version DESC;
UPDATE proposals
SET proposal_status = 'SUBMITTED',
    submitted_by = :submitted_by,
    submitted_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE proposal_id = :proposal_id
RETURNING proposal_id, proposal_status, submitted_at;
INSERT INTO proposal_reviews (
    proposal_id, reviewer_id, review_level,
    decision, remarks
)
VALUES (
    :proposal_id, :reviewer_id, :review_level,
    :decision, :remarks
)
RETURNING proposal_review_id;
SELECT
    pr.proposal_review_id,
    pr.review_level,
    pr.decision,
    pr.remarks,
    pr.reviewed_at,
    u.full_name AS reviewer_name
FROM proposal_reviews pr
JOIN users u ON u.user_id = pr.reviewer_id
WHERE pr.proposal_id = :proposal_id
ORDER BY pr.reviewed_at DESC;
SELECT
    wd.workflow_definition_id,
    wd.workflow_name,
    wd.workflow_version,
    ws.workflow_stage_id,
    ws.stage_code,
    ws.stage_name,
    ws.stage_order,
    r.role_name AS responsible_role,
    ws.target_days,
    ws.is_approval_stage
FROM workflow_definitions wd
JOIN workflow_stages ws
  ON ws.workflow_definition_id = wd.workflow_definition_id
LEFT JOIN roles r ON r.role_id = ws.responsible_role_id
WHERE wd.workflow_definition_id = :workflow_definition_id
ORDER BY ws.stage_order;
INSERT INTO project_workflows (
    project_id, workflow_definition_id,
    current_stage_id, workflow_status, started_at
)
SELECT
    :project_id,
    :workflow_definition_id,
    MIN(ws.workflow_stage_id),
    'IN_PROGRESS',
    CURRENT_TIMESTAMP
FROM workflow_stages ws
WHERE ws.workflow_definition_id = :workflow_definition_id
RETURNING project_workflow_id;
SELECT
    pw.project_workflow_id,
    pw.workflow_status,
    pw.started_at,
    pw.completed_at,
    ws.stage_code,
    ws.stage_name,
    ws.stage_order,
    ws.target_days,
    r.role_name AS responsible_role
FROM project_workflows pw
LEFT JOIN workflow_stages ws
    ON ws.workflow_stage_id = pw.current_stage_id
LEFT JOIN roles r
    ON r.role_id = ws.responsible_role_id
WHERE pw.project_id = :project_id;
INSERT INTO workflow_actions (
    project_workflow_id, workflow_stage_id,
    performed_by, action_type, action_status, remarks
)
VALUES (
    :project_workflow_id, :workflow_stage_id,
    :performed_by, :action_type, :action_status, :remarks
)
RETURNING workflow_action_id;
UPDATE project_workflows
SET current_stage_id = :next_stage_id,
    workflow_status = :workflow_status,
    completed_at =
        CASE WHEN :workflow_status = 'COMPLETED'
             THEN CURRENT_TIMESTAMP ELSE completed_at END
WHERE project_workflow_id = :project_workflow_id;
SELECT
    wa.workflow_action_id,
    ws.stage_name,
    wa.action_type,
    wa.action_status,
    wa.remarks,
    u.full_name AS performed_by_name,
    wa.acted_at
FROM workflow_actions wa
LEFT JOIN workflow_stages ws
    ON ws.workflow_stage_id = wa.workflow_stage_id
LEFT JOIN users u ON u.user_id = wa.performed_by
WHERE wa.project_workflow_id = :project_workflow_id
ORDER BY wa.acted_at DESC;
SELECT
    milestone_id, milestone_name, description,
    planned_date, actual_date, milestone_status,
    responsible_user_id
FROM project_milestones
WHERE project_id = :project_id
ORDER BY planned_date NULLS LAST, milestone_id;
INSERT INTO project_milestones (
    project_id, milestone_name, description,
    planned_date, responsible_user_id
)
VALUES (
    :project_id, :milestone_name, :description,
    :planned_date, :responsible_user_id
)
RETURNING milestone_id;
UPDATE project_milestones
SET actual_date = COALESCE(:actual_date, CURRENT_DATE),
    milestone_status = 'COMPLETED'
WHERE milestone_id = :milestone_id;
SELECT
    pm.milestone_id, pm.project_id,
    p.project_name, pm.milestone_name,
    pm.planned_date,
    CURRENT_DATE - pm.planned_date AS overdue_days,
    pm.responsible_user_id
FROM project_milestones pm
JOIN projects p ON p.project_id = pm.project_id
WHERE pm.planned_date < CURRENT_DATE
  AND pm.milestone_status <> 'COMPLETED'
ORDER BY overdue_days DESC;
INSERT INTO land_parcels (
    project_id, village_id, survey_number,
    subdivision_number, area_hectare,
    land_type, ownership_type, acquisition_status,
    geom, centroid
)
VALUES (
    :project_id, :village_id, :survey_number,
    :subdivision_number, :area_hectare,
    :land_type, :ownership_type, :acquisition_status,
    CASE WHEN :wkt IS NULL THEN NULL
         ELSE ST_SetSRID(ST_GeomFromText(:wkt), 4326) END,
    CASE WHEN :wkt IS NULL THEN NULL
         ELSE ST_Centroid(ST_SetSRID(ST_GeomFromText(:wkt), 4326)) END
)
RETURNING parcel_id;
SELECT
    lp.parcel_id,
    lp.project_id,
    lp.village_id,
    v.village_name,
    d.district_name,
    s.state_name,
    lp.survey_number,
    lp.subdivision_number,
    lp.area_hectare,
    lp.land_type,
    lp.ownership_type,
    lp.acquisition_status
FROM land_parcels lp
JOIN villages v ON v.village_id = lp.village_id
JOIN districts d ON d.district_id = v.district_id
JOIN states s ON s.state_id = d.state_id
WHERE lp.project_id = :project_id
  AND (:acquisition_status IS NULL
       OR lp.acquisition_status = :acquisition_status)
ORDER BY v.village_name, lp.survey_number, lp.subdivision_number;
SELECT
    lp.parcel_id,
    lp.project_id,
    lp.village_id,
    v.village_name,
    d.district_name,
    s.state_name,
    lp.survey_number,
    lp.subdivision_number,
    lp.area_hectare,
    lp.land_type,
    lp.ownership_type,
    lp.acquisition_status,
    ST_AsGeoJSON(lp.geom)::json AS geometry,
    ST_AsGeoJSON(lp.centroid)::json AS centroid
FROM land_parcels lp
JOIN villages v ON v.village_id = lp.village_id
JOIN districts d ON d.district_id = v.district_id
JOIN states s ON s.state_id = d.state_id
WHERE lp.parcel_id = :parcel_id;
UPDATE land_parcels
SET acquisition_status = :acquisition_status,
    updated_at = CURRENT_TIMESTAMP
WHERE parcel_id = :parcel_id;
SELECT
    lp.parcel_id, lp.project_id, p.project_name,
    v.village_name, lp.survey_number,
    lp.subdivision_number, lp.area_hectare,
    lp.acquisition_status
FROM land_parcels lp
JOIN projects p ON p.project_id = lp.project_id
JOIN villages v ON v.village_id = lp.village_id
WHERE lp.village_id = :village_id
  AND lp.survey_number ILIKE :survey_number_pattern
ORDER BY lp.survey_number;
SELECT
    lp.parcel_id,
    lp.project_id,
    lp.survey_number,
    lp.area_hectare,
    lp.acquisition_status,
    ST_AsGeoJSON(lp.geom)::json AS geometry
FROM land_parcels lp
WHERE lp.geom IS NOT NULL
  AND ST_Intersects(
      lp.geom,
      ST_SetSRID(ST_GeomFromText(:map_wkt), 4326)
  );
SELECT
    lp.parcel_id,
    lp.project_id,
    lp.survey_number,
    lp.area_hectare,
    ST_Distance(
        lp.geom::geography,
        ST_SetSRID(ST_Point(:longitude, :latitude), 4326)::geography
    ) AS distance_meters
FROM land_parcels lp
WHERE lp.geom IS NOT NULL
  AND ST_DWithin(
      lp.geom::geography,
      ST_SetSRID(ST_Point(:longitude, :latitude), 4326)::geography,
      :radius_meters
  )
ORDER BY distance_meters;
SELECT json_build_object(
    'type', 'FeatureCollection',
    'features', COALESCE(
        json_agg(
            json_build_object(
                'type', 'Feature',
                'geometry', ST_AsGeoJSON(lp.geom)::json,
                'properties', json_build_object(
                    'parcel_id', lp.parcel_id,
                    'survey_number', lp.survey_number,
                    'subdivision_number', lp.subdivision_number,
                    'area_hectare', lp.area_hectare,
                    'acquisition_status', lp.acquisition_status
                )
            )
        ) FILTER (WHERE lp.geom IS NOT NULL),
        '[]'::json
    )
)
FROM land_parcels lp
WHERE lp.project_id = :project_id;
INSERT INTO parcel_owners (
    owner_name, phone, email, address,
    identity_reference_encrypted
)
VALUES (
    :owner_name, :phone, :email, :address,
    :identity_reference_encrypted
)
RETURNING owner_id;
INSERT INTO parcel_ownerships (
    parcel_id, owner_id, ownership_percent,
    ownership_start_date, record_source
)
VALUES (
    :parcel_id, :owner_id, :ownership_percent,
    :ownership_start_date, :record_source
)
RETURNING parcel_ownership_id;
SELECT
    po.owner_id,
    po.owner_name,
    po.phone,
    po.email,
    po.address,
    pos.ownership_percent,
    pos.ownership_start_date,
    pos.ownership_end_date,
    pos.verified_status
FROM parcel_ownerships pos
JOIN parcel_owners po ON po.owner_id = pos.owner_id
WHERE pos.parcel_id = :parcel_id
ORDER BY po.owner_name;
SELECT
    lp.parcel_id,
    p.project_name,
    lp.survey_number,
    lp.subdivision_number,
    lp.area_hectare,
    pos.ownership_percent,
    lp.acquisition_status
FROM parcel_ownerships pos
JOIN land_parcels lp ON lp.parcel_id = pos.parcel_id
JOIN projects p ON p.project_id = lp.project_id
WHERE pos.owner_id = :owner_id
ORDER BY p.project_name, lp.survey_number;
INSERT INTO gis_layers (
    project_id, parcel_id, layer_name,
    layer_type, geom, properties
)
VALUES (
    :project_id, :parcel_id, :layer_name,
    :layer_type,
    ST_SetSRID(ST_GeomFromText(:wkt), 4326),
    CAST(:properties_json AS jsonb)
)
RETURNING gis_layer_id;
SELECT
    gis_layer_id,
    layer_name,
    layer_type,
    ST_AsGeoJSON(geom)::json AS geometry,
    properties
FROM gis_layers
WHERE project_id = :project_id
ORDER BY created_at;
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
INSERT INTO compensation_assessments (
    award_id, owner_id, assessed_amount,
    approved_amount, assessment_basis, assessed_by
)
VALUES (
    :award_id, :owner_id, :assessed_amount,
    :approved_amount, :assessment_basis, :assessed_by
)
RETURNING compensation_assessment_id;
SELECT
    ca.compensation_assessment_id,
    a.award_number,
    lp.parcel_id,
    lp.survey_number,
    po.owner_id,
    po.owner_name,
    ca.assessed_amount,
    ca.approved_amount,
    ca.assessment_status,
    ca.assessed_at,
    ca.approved_at
FROM compensation_assessments ca
JOIN awards a ON a.award_id = ca.award_id
JOIN land_parcels lp ON lp.parcel_id = a.parcel_id
JOIN parcel_owners po ON po.owner_id = ca.owner_id
WHERE a.project_id = :project_id
ORDER BY ca.assessed_at DESC;
UPDATE compensation_assessments
SET approved_amount = :approved_amount,
    assessment_status = 'APPROVED',
    approved_by = :approved_by,
    approved_at = CURRENT_TIMESTAMP
WHERE compensation_assessment_id = :compensation_assessment_id;
INSERT INTO compensation_payments (
    compensation_assessment_id, amount_paid,
    payment_date, transaction_reference,
    payment_method, payment_status, recorded_by
)
VALUES (
    :compensation_assessment_id, :amount_paid,
    :payment_date, :transaction_reference,
    :payment_method, :payment_status, :recorded_by
)
RETURNING payment_id;
SELECT
    cp.payment_id,
    cp.amount_paid,
    cp.payment_date,
    cp.transaction_reference,
    cp.payment_method,
    cp.payment_status,
    cp.created_at
FROM compensation_payments cp
WHERE cp.compensation_assessment_id = :compensation_assessment_id
ORDER BY cp.payment_date DESC NULLS LAST;
SELECT
    COALESCE(SUM(ca.assessed_amount), 0) AS total_assessed,
    COALESCE(SUM(ca.approved_amount), 0) AS total_approved,
    COALESCE((
        SELECT SUM(cp.amount_paid)
        FROM compensation_payments cp
        JOIN compensation_assessments ca2
          ON ca2.compensation_assessment_id = cp.compensation_assessment_id
        JOIN awards a2 ON a2.award_id = ca2.award_id
        WHERE a2.project_id = :project_id
    ), 0) AS total_paid
FROM compensation_assessments ca
JOIN awards a ON a.award_id = ca.award_id
WHERE a.project_id = :project_id;
INSERT INTO affected_families (
    project_id, parcel_id, family_reference_code,
    head_of_family_name, household_size,
    displacement_status, vulnerability_category
)
VALUES (
    :project_id, :parcel_id, :family_reference_code,
    :head_of_family_name, :household_size,
    :displacement_status, :vulnerability_category
)
RETURNING family_id;
SELECT
    af.family_id,
    af.family_reference_code,
    af.head_of_family_name,
    af.household_size,
    af.displacement_status,
    af.vulnerability_category,
    af.assessment_status,
    lp.survey_number
FROM affected_families af
LEFT JOIN land_parcels lp ON lp.parcel_id = af.parcel_id
WHERE af.project_id = :project_id
ORDER BY af.family_id;
INSERT INTO family_members (
    family_id, full_name, relationship_to_head,
    age_years, livelihood, is_displaced
)
VALUES (
    :family_id, :full_name, :relationship_to_head,
    :age_years, :livelihood, :is_displaced
)
RETURNING family_member_id;
SELECT
    family_member_id, full_name,
    relationship_to_head, age_years,
    livelihood, is_displaced
FROM family_members
WHERE family_id = :family_id
ORDER BY family_member_id;
INSERT INTO rr_assessments (
    family_id, assessment_date,
    assessed_by, assessment_status, remarks
)
VALUES (
    :family_id, :assessment_date,
    :assessed_by, :assessment_status, :remarks
)
RETURNING rr_assessment_id;
INSERT INTO rr_benefits (
    family_id, benefit_type,
    benefit_description, assessed_value
)
VALUES (
    :family_id, :benefit_type,
    :benefit_description, :assessed_value
)
RETURNING rr_benefit_id;
UPDATE rr_benefits
SET approved_value = :approved_value,
    benefit_status = 'APPROVED',
    approved_by = :approved_by,
    approved_at = CURRENT_TIMESTAMP
WHERE rr_benefit_id = :rr_benefit_id;
SELECT
    rb.rr_benefit_id,
    rb.benefit_type,
    rb.benefit_description,
    rb.assessed_value,
    rb.approved_value,
    rb.benefit_status
FROM rr_benefits rb
WHERE rb.family_id = :family_id
ORDER BY rb.rr_benefit_id;
INSERT INTO rr_deliveries (
    rr_benefit_id, delivery_date,
    delivery_status, delivery_reference,
    remarks, recorded_by
)
VALUES (
    :rr_benefit_id, :delivery_date,
    :delivery_status, :delivery_reference,
    :remarks, :recorded_by
)
RETURNING rr_delivery_id;
SELECT
    rd.rr_delivery_id,
    rb.benefit_type,
    rd.delivery_date,
    rd.delivery_status,
    rd.delivery_reference,
    rd.remarks
FROM rr_deliveries rd
JOIN rr_benefits rb ON rb.rr_benefit_id = rd.rr_benefit_id
WHERE rb.family_id = :family_id
ORDER BY rd.delivery_date DESC NULLS LAST;
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
INSERT INTO documents (
    project_id, parcel_id, document_type,
    document_title, uploaded_by,
    current_version, is_confidential
)
VALUES (
    :project_id, :parcel_id, :document_type,
    :document_title, :uploaded_by,
    1, :is_confidential
)
RETURNING document_id;
INSERT INTO document_versions (
    document_id, version_number,
    file_path, file_hash, file_size_bytes,
    uploaded_by, version_notes
)
VALUES (
    :document_id,
    :version_number,
    :file_path, :file_hash, :file_size_bytes,
    :uploaded_by, :version_notes
)
RETURNING document_version_id;
UPDATE documents
SET current_version = :version_number
WHERE document_id = :document_id;
SELECT
    d.document_id,
    d.document_type,
    d.document_title,
    d.current_version,
    d.is_confidential,
    u.full_name AS uploaded_by_name,
    d.created_at
FROM documents d
JOIN users u ON u.user_id = d.uploaded_by
WHERE d.project_id = :project_id
ORDER BY d.created_at DESC;
SELECT
    d.document_id,
    d.document_type,
    d.document_title,
    d.current_version,
    d.is_confidential,
    d.created_at
FROM documents d
WHERE d.parcel_id = :parcel_id
ORDER BY d.created_at DESC;
SELECT
    dv.document_version_id,
    dv.version_number,
    dv.file_path,
    dv.file_hash,
    dv.file_size_bytes,
    u.full_name AS uploaded_by_name,
    dv.uploaded_at,
    dv.version_notes
FROM document_versions dv
JOIN users u ON u.user_id = dv.uploaded_by
WHERE dv.document_id = :document_id
ORDER BY dv.version_number DESC;
INSERT INTO document_reviews (
    document_id, reviewer_id,
    review_status, remarks
)
VALUES (
    :document_id, :reviewer_id,
    :review_status, :remarks
)
RETURNING document_review_id;
SELECT
    dr.document_review_id,
    dr.review_status,
    dr.remarks,
    dr.reviewed_at,
    u.full_name AS reviewer_name
FROM document_reviews dr
JOIN users u ON u.user_id = dr.reviewer_id
WHERE dr.document_id = :document_id
ORDER BY dr.reviewed_at DESC;
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
COMMIT;
