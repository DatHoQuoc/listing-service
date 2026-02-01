CREATE EXTENSION IF NOT EXISTS postgis;
-- =====================================================
-- LISTING SERVICE - DATABASE SCHEMA
-- Aligned with Real Estate Platform Schema
-- =====================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- Country
WITH inserted_country AS (
INSERT INTO countries (country_id, name, code, created_at, updated_at)
VALUES (gen_random_uuid(), 'Vietnam', 'VNM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING country_id
    ),
    inserted_province AS (
INSERT INTO provinces (province_id, name, code, country_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Ho Chi Minh City', 'HCMC', country_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM inserted_country
    RETURNING province_id
    )
INSERT INTO wards (ward_id, name, code, province_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Ben Nghe Ward', 'BNW', province_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM inserted_province;

--Amenity and POI
INSERT INTO amenities (amenity_id, amenity_name, amenity_category, icon_url, created_at)
VALUES
    (gen_random_uuid(), '24/7 Security', 'SECURITY', 'https://cdn-icons/security.png', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Swimming Pool', 'FACILITIES', 'https://cdn-icons/pool.png', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Basement Parking', 'PARKING', 'https://cdn-icons/car.png', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'High-speed Wi-Fi', 'UTILITIES', 'https://cdn-icons/wifi.png', CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'Gym Center', 'FACILITIES', 'https://cdn-icons/gym.png', CURRENT_TIMESTAMP);


WITH target_listing AS (
    SELECT listing_id FROM listings WHERE title = 'Luxury Apartment Landmark 81' LIMIT 1
    )
INSERT INTO points_of_interest (
    poi_id,
    listing_id,
    name,
    category,
    geolocation,
    distance_meters,
    created_at
)
SELECT
    gen_random_uuid(),
    listing_id,
    'Vincom Center',
    'Shopping Mall',
    ST_SetSRID(ST_MakePoint(106.7218, 10.7948), 4326), -- Longitude, Latitude
    150,
    CURRENT_TIMESTAMP
FROM target_listing
UNION ALL
SELECT
    gen_random_uuid(),
    listing_id,
    'Central Park',
    'Park',
    ST_SetSRID(ST_MakePoint(106.7200, 10.7930), 4326),
    300,
    CURRENT_TIMESTAMP
FROM target_listing;
