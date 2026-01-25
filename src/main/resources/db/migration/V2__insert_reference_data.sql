-- =====================================================
-- REFERENCE DATA (Location & Amenities)
-- =====================================================

-- Insert Vietnam
INSERT INTO countries (country_id, name, code) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'Vietnam', 'VNM');

-- Insert Major Provinces in Vietnam
INSERT INTO provinces (province_id, name, country_id, code) VALUES
                                                                ('550e8400-e29b-41d4-a716-446655440001', 'Ho Chi Minh City', '550e8400-e29b-41d4-a716-446655440000', 'SG'),
                                                                ('550e8400-e29b-41d4-a716-446655440002', 'Hanoi', '550e8400-e29b-41d4-a716-446655440000', 'HN'),
                                                                ('550e8400-e29b-41d4-a716-446655440003', 'Da Nang', '550e8400-e29b-41d4-a716-446655440000', 'DDN');

-- Insert Wards for Ho Chi Minh City (Sample)
INSERT INTO wards (name, province_id, code) VALUES
                                                ('Ben Nghe', '550e8400-e29b-41d4-a716-446655440001', 'BN'),
                                                ('Ben Thanh', '550e8400-e29b-41d4-a716-446655440001', 'BT'),
                                                ('Nguyen Thai Binh', '550e8400-e29b-41d4-a716-446655440001', 'NTB'),
                                                ('Pham Ngu Lao', '550e8400-e29b-41d4-a716-446655440001', 'PNL'),
                                                ('Thao Dien', '550e8400-e29b-41d4-a716-446655440001', 'TD'),
                                                ('An Phu', '550e8400-e29b-41d4-a716-446655440001', 'AP');

-- Insert Common Amenities
INSERT INTO amenities (amenity_name, amenity_category, icon_url) VALUES
                                                                     -- Security
                                                                     ('24/7 Security', 'security', 'icons/security.svg'),
                                                                     ('CCTV Camera', 'security', 'icons/cctv.svg'),
                                                                     ('Access Card System', 'security', 'icons/access-card.svg'),
                                                                     ('Security Guard', 'security', 'icons/guard.svg'),

                                                                     -- Facilities
                                                                     ('Swimming Pool', 'facilities', 'icons/pool.svg'),
                                                                     ('Gym/Fitness Center', 'facilities', 'icons/gym.svg'),
                                                                     ('Playground', 'facilities', 'icons/playground.svg'),
                                                                     ('BBQ Area', 'facilities', 'icons/bbq.svg'),
                                                                     ('Garden', 'facilities', 'icons/garden.svg'),
                                                                     ('Rooftop Terrace', 'facilities', 'icons/terrace.svg'),
                                                                     ('Meeting Room', 'facilities', 'icons/meeting.svg'),
                                                                     ('Sauna', 'facilities', 'icons/sauna.svg'),

                                                                     -- Parking
                                                                     ('Car Parking', 'parking', 'icons/car-parking.svg'),
                                                                     ('Motorcycle Parking', 'parking', 'icons/bike-parking.svg'),
                                                                     ('Basement Parking', 'parking', 'icons/basement-parking.svg'),

                                                                     -- Utilities
                                                                     ('Elevator', 'utilities', 'icons/elevator.svg'),
                                                                     ('Air Conditioning', 'utilities', 'icons/ac.svg'),
                                                                     ('Heating', 'utilities', 'icons/heating.svg'),
                                                                     ('Water Heater', 'utilities', 'icons/water-heater.svg'),
                                                                     ('Balcony', 'utilities', 'icons/balcony.svg'),
                                                                     ('Furnished', 'utilities', 'icons/furnished.svg'),
                                                                     ('Kitchen', 'utilities', 'icons/kitchen.svg'),
                                                                     ('Washing Machine', 'utilities', 'icons/washing-machine.svg'),
                                                                     ('Refrigerator', 'utilities', 'icons/refrigerator.svg'),
                                                                     ('Internet/Wifi', 'utilities', 'icons/wifi.svg'),
                                                                     ('Cable TV', 'utilities', 'icons/tv.svg'),

                                                                     -- Nearby
                                                                     ('Near School', 'nearby', 'icons/school.svg'),
                                                                     ('Near Hospital', 'nearby', 'icons/hospital.svg'),
                                                                     ('Near Supermarket', 'nearby', 'icons/supermarket.svg'),
                                                                     ('Near Metro', 'nearby', 'icons/metro.svg'),
                                                                     ('Near Park', 'nearby', 'icons/park.svg');