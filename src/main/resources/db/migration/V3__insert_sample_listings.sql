-- =====================================================
-- SAMPLE LISTING DATA FOR TESTING
-- =====================================================

DO $$
DECLARE
sample_user_id_1 UUID := 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11';
    sample_user_id_2 UUID := 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22';
    hcmc_province_id UUID := '550e8400-e29b-41d4-a716-446655440001';
    vietnam_country_id UUID := '550e8400-e29b-41d4-a716-446655440000';
    ward_ben_nghe_id UUID;
    ward_thao_dien_id UUID;
    listing_1_id UUID;
    listing_2_id UUID;
BEGIN
    -- Get ward IDs
SELECT ward_id INTO ward_ben_nghe_id FROM wards WHERE name = 'Ben Nghe' LIMIT 1;
SELECT ward_id INTO ward_thao_dien_id FROM wards WHERE name = 'Thao Dien' LIMIT 1;

-- Sample Listing 1: Published Apartment for Sale
listing_1_id := uuid_generate_v4();
INSERT INTO listings (
    listing_id,
    user_id,
    title,
    description,
    listing_type,
    property_type,
    status,
    is_free_post,
    price,
    price_currency,
    area_sqm,
    bedrooms,
    bathrooms,
    floors,
    floor_number,
    year_built,
    ward_id,
    province_id,
    country_id,
    street_address,
    building_name,
    geolocation,
    featured_image_url,
    images_json,
    view_count,
    save_count,
    published_at,
    created_at,
    updated_at
) VALUES (
             listing_1_id,
             sample_user_id_1,
             'Luxury 2BR Apartment with City View - District 1',
             'Stunning modern apartment with panoramic city view, fully furnished with high-end appliances. Walking distance to metro station and shopping centers.',
             'sale',
             'apartment',
             'published',
             TRUE,
             5500000000.00,
             'VND',
             85.5,
             2,
             2,
             25,
             18,
             2020,
             ward_ben_nghe_id,
             hcmc_province_id,
             vietnam_country_id,
             '123 Nguyen Hue Street',
             'Vinhomes Golden River',
             POINT(106.7009, 10.7769),
             'https://minio/listings/featured/listing1.jpg',
             '[
                 {"url": "https://minio/listings/img1.jpg", "order": 1, "caption": "Living room"},
                 {"url": "https://minio/listings/img2.jpg", "order": 2, "caption": "Bedroom"}
             ]'::jsonb,
             156,
             23,
             CURRENT_TIMESTAMP - INTERVAL '5 days',
             CURRENT_TIMESTAMP - INTERVAL '7 days',
             CURRENT_TIMESTAMP - INTERVAL '5 days'
         );

-- Add amenities to listing 1
INSERT INTO listing_amenities (listing_id, amenity_id)
SELECT listing_1_id, amenity_id FROM amenities
WHERE amenity_name IN ('Swimming Pool', 'Gym/Fitness Center', '24/7 Security', 'Elevator', 'Car Parking')
    LIMIT 5;

-- Sample Listing 2: Draft House for Rent
listing_2_id := uuid_generate_v4();
INSERT INTO listings (
    listing_id,
    user_id,
    title,
    description,
    listing_type,
    property_type,
    status,
    is_free_post,
    price,
    price_currency,
    price_period,
    area_sqm,
    bedrooms,
    bathrooms,
    ward_id,
    province_id,
    country_id,
    street_address,
    geolocation,
    created_at,
    updated_at
) VALUES (
             listing_2_id,
             sample_user_id_2,
             'Modern 3BR Villa with Pool - Thao Dien',
             'Spacious villa in expat-friendly neighborhood, private pool and garden',
             'rent',
             'villa',
             'draft',
             FALSE,
             45000000.00,
             'VND',
             'monthly',
             250.0,
             3,
             3,
             ward_thao_dien_id,
             hcmc_province_id,
             vietnam_country_id,
             '456 Thao Dien Road',
             POINT(106.7397, 10.8012),
             CURRENT_TIMESTAMP - INTERVAL '2 days',
             CURRENT_TIMESTAMP - INTERVAL '2 days'
         );

END $$;