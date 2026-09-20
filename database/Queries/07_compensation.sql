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
