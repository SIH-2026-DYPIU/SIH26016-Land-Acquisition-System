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
