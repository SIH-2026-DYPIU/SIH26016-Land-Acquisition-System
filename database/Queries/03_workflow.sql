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
