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
