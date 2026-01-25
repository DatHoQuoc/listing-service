-- =====================================================
-- SAMPLE DATA FOR TESTING
-- =====================================================

-- Sample User IDs (these should come from User Service in production)
DO $$
DECLARE
sample_user_id_1 UUID := 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11';
    sample_user_id_2 UUID := 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22';
BEGIN

    -- Sample Listing 1: Published Apartment for Sale
INSERT INTO post (
    post_id,
    user_id,
    title,
    description,
    property_type,
    listing_type,
    price,
    price_unit,
    area_sqm,
    bedrooms,
    bathrooms,
    address_json,
    amenities_json,
    images_json,
    post_status,
    is_free_post,
    view_count,
    favorite_count,
    published_at,
    created_at,
    updated_at
) VALUES (
             uuid_generate_v4(),
             sample_user_id_1,
             'Modern 2BR Apartment in District 1',
             'Beautiful modern apartment with city view, fully furnished, near metro station',
             'APARTMENT',
             'SALE',
             5500000000.00,
             'VND',
             85.5,
             2,
             2,
             '{"street": "123 Nguyen Hue", "ward": "Ben Nghe", "district": "District 1", "city": "Ho Chi Minh City", "coordinates": {"lat": 10.7769, "lng": 106.7009}}'::jsonb,
             '["Parking", "Gym", "Swimming Pool", "Security 24/7"]'::jsonb,
             '[{"url": "https://example.com/image1.jpg", "type": "regular", "order": 1}, {"url": "https://example.com/360_1.jpg", "type": "360", "order": 2}]'::jsonb,
             'PUBLISHED',
             TRUE,
             156,
             23,
             CURRENT_TIMESTAMP - INTERVAL '5 days',
             CURRENT_TIMESTAMP - INTERVAL '7 days',
             CURRENT_TIMESTAMP - INTERVAL '5 days'
         );

-- Sample Listing 2: Draft House for Rent
INSERT INTO post (
    post_id,
    user_id,
    title,
    description,
    property_type,
    listing_type,
    price,
    price_unit,
    price_period,
    area_sqm,
    bedrooms,
    bathrooms,
    address_json,
    post_status,
    is_free_post,
    created_at,
    updated_at
) VALUES (
             uuid_generate_v4(),
             sample_user_id_1,
             'Spacious 3BR House in District 2',
             'Family-friendly house with garden, quiet neighborhood',
             'HOUSE',
             'RENT',
             25000000.00,
             'VND',
             'MONTHLY',
             150.0,
             3,
             3,
             '{"street": "456 Thao Dien", "ward": "Thao Dien", "district": "District 2", "city": "Ho Chi Minh City", "coordinates": {"lat": 10.8012, "lng": 106.7397}}'::jsonb,
             'DRAFT',
             FALSE,
             CURRENT_TIMESTAMP - INTERVAL '2 days',
             CURRENT_TIMESTAMP - INTERVAL '2 days'
         );

-- Sample Listing 3: Pending Review - Villa for Sale
INSERT INTO post (
    post_id,
    user_id,
    title,
    description,
    property_type,
    listing_type,
    price,
    price_unit,
    area_sqm,
    bedrooms,
    bathrooms,
    address_json,
    amenities_json,
    post_status,
    is_free_post,
    credits_locked,
    submitted_at,
    created_at,
    updated_at
) VALUES (
             uuid_generate_v4(),
             sample_user_id_2,
             'Luxury Villa with Pool in District 7',
             'Modern villa with private pool, garden, and premium finishes',
             'VILLA',
             'SALE',
             15000000000.00,
             'VND',
             300.0,
             5,
             4,
             '{"street": "789 Nguyen Van Linh", "ward": "Tan Phong", "district": "District 7", "city": "Ho Chi Minh City", "coordinates": {"lat": 10.7329, "lng": 106.7196}}'::jsonb,
             '["Swimming Pool", "Garden", "Smart Home", "Security", "Garage"]'::jsonb,
             'PENDING',
             FALSE,
             10,
             CURRENT_TIMESTAMP - INTERVAL '1 day',
             CURRENT_TIMESTAMP - INTERVAL '3 days',
             CURRENT_TIMESTAMP - INTERVAL '1 day'
         );

-- Add credit lock for pending listing
INSERT INTO post_credit_lock (
    lock_id,
    post_id,
    user_id,
    credits_locked,
    lock_reason,
    locked_at
)
SELECT
    uuid_generate_v4(),
    post_id,
    sample_user_id_2,
    10,
    'POST_SUBMISSION',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM post
WHERE user_id = sample_user_id_2
  AND post_status = 'PENDING'
    LIMIT 1;

END $$;