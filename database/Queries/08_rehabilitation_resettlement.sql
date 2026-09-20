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
