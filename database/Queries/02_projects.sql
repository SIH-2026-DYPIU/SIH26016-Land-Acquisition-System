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
