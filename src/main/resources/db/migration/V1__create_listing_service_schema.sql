-- =====================================================
-- LISTING SERVICE - DATABASE SCHEMA
-- Aligned with Real Estate Platform Schema
-- =====================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- ENUMS
-- =====================================================

CREATE TYPE listing_status AS ENUM (
    'draft',
    'pending_review',
    'published',
    'rejected',
    'expired',
    'archived',
    'deleted'
);

CREATE TYPE listing_type AS ENUM (
    'sale',
    'rent',
    'lease'
);

CREATE TYPE document_type AS ENUM (
    'ownership_certificate',
    'land_use_certificate',
    'building_permit',
    'construction_permit',
    'house_certificate',
    'transfer_contract',
    'other'
);

CREATE TYPE feedback_severity AS ENUM (
    'critical',
    'high',
    'medium',
    'low',
    'info'
);

CREATE TYPE feedback_category AS ENUM (
    'price_mismatch',
    'fake_images',
    'missing_information',
    'duplicate_listing',
    'location_error',
    'invalid_documents',
    'spam',
    'other'
);

-- =====================================================
-- LOCATION TABLES (Reference Tables)
-- =====================================================

CREATE TABLE countries (
                           country_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name VARCHAR(100) NOT NULL UNIQUE,
                           code VARCHAR(3) UNIQUE,  -- ISO 3166-1 alpha-3
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE provinces (
                           province_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name VARCHAR(100) NOT NULL,
                           country_id UUID NOT NULL REFERENCES countries(country_id) ON DELETE CASCADE,
                           code VARCHAR(10),
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE(name, country_id)
);

CREATE TABLE wards (
                       ward_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name VARCHAR(100) NOT NULL,
                       province_id UUID NOT NULL REFERENCES provinces(province_id) ON DELETE CASCADE,
                       code VARCHAR(10),
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       UNIQUE(name, province_id)
);

-- =====================================================
-- CORE LISTING TABLE
-- =====================================================

CREATE TABLE listings (
                          listing_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Owner Information (from User Service)
                          user_id UUID NOT NULL,  -- Reference to User Service

    -- Basic Information
                          title VARCHAR(500) NOT NULL,
                          description TEXT NOT NULL,

    -- Listing Classification
                          listing_type listing_type NOT NULL,
                          property_type VARCHAR(50) NOT NULL,  -- 'apartment', 'house', 'villa', 'land', 'commercial'

    -- Status & Lifecycle
                          status listing_status DEFAULT 'draft' NOT NULL,
                          is_free_post BOOLEAN DEFAULT FALSE NOT NULL,

    -- Pricing
                          price DECIMAL(15,2) NOT NULL,
                          price_currency VARCHAR(3) DEFAULT 'VND',  -- ISO 4217
                          price_period VARCHAR(20),  -- 'monthly', 'quarterly', 'yearly', 'total' (for rent)
                          negotiable BOOLEAN DEFAULT TRUE,

    -- Property Details
                          area_sqm DECIMAL(10,2) NOT NULL,
                          bedrooms INTEGER,
                          bathrooms INTEGER,
                          floors INTEGER,
                          floor_number INTEGER,  -- Which floor (for apartments)
                          year_built INTEGER,

    -- Location (Relational)
                          ward_id UUID REFERENCES wards(ward_id),
                          province_id UUID REFERENCES provinces(province_id),
                          country_id UUID REFERENCES countries(country_id),
                          street_address VARCHAR(500),
                          building_name VARCHAR(255),
                          geolocation POINT,  -- PostGIS point (longitude, latitude)

    -- Media Storage (MinIO URLs)
                          featured_image_url TEXT,
                          images_json JSONB,  -- [{"url": "...", "order": 1, "caption": "..."}]

    -- Additional Information
                          additional_info_json JSONB,  -- Flexible field for extra data

    -- Metrics
                          view_count INTEGER DEFAULT 0,
                          save_count INTEGER DEFAULT 0,
                          contact_count INTEGER DEFAULT 0,

    -- Review & Moderation
                          rejection_reason TEXT,
                          admin_notes TEXT,

    -- Credits (for integration with Credit Service)
                          credits_locked INTEGER DEFAULT 0 NOT NULL,
                          credits_charged INTEGER DEFAULT 0 NOT NULL,
                          credits_refunded INTEGER DEFAULT 0 NOT NULL,

    -- Timestamps
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          submitted_at TIMESTAMP WITH TIME ZONE,
                          reviewed_at TIMESTAMP WITH TIME ZONE,
                          published_at TIMESTAMP WITH TIME ZONE,
                          expired_at TIMESTAMP WITH TIME ZONE,

    -- Constraints
                          CONSTRAINT chk_price_positive CHECK (price > 0),
                          CONSTRAINT chk_area_positive CHECK (area_sqm > 0),
                          CONSTRAINT chk_bedrooms_valid CHECK (bedrooms >= 0 OR bedrooms IS NULL),
                          CONSTRAINT chk_bathrooms_valid CHECK (bathrooms >= 0 OR bathrooms IS NULL)
);

COMMENT ON TABLE listings IS 'Core listing table for real estate properties';
COMMENT ON COLUMN listings.images_json IS 'Array of image objects with URLs, order, and captions';
COMMENT ON COLUMN listings.geolocation IS 'PostGIS point for map coordinates (lng, lat)';

-- =====================================================
-- AMENITIES (Many-to-Many)
-- =====================================================

CREATE TABLE amenities (
                           amenity_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           amenity_name VARCHAR(100) NOT NULL UNIQUE,
                           amenity_category VARCHAR(50),  -- 'security', 'facilities', 'utilities', 'nearby'
                           icon_url TEXT,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE listing_amenities (
                                   listing_id UUID REFERENCES listings(listing_id) ON DELETE CASCADE,
                                   amenity_id UUID REFERENCES amenities(amenity_id) ON DELETE CASCADE,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (listing_id, amenity_id)
);

COMMENT ON TABLE amenities IS 'Predefined amenities for listings (parking, gym, pool, etc.)';
COMMENT ON TABLE listing_amenities IS 'Many-to-many relationship between listings and amenities';

-- =====================================================
-- POINTS OF INTEREST (Nearby Places)
-- =====================================================

CREATE TABLE points_of_interest (
                                    poi_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    listing_id UUID REFERENCES listings(listing_id) ON DELETE CASCADE,
                                    name VARCHAR(255) NOT NULL,
                                    category VARCHAR(50) NOT NULL,  -- 'school', 'hospital', 'transport', 'shopping', 'restaurant'
                                    geolocation POINT,  -- Coordinates of the POI
                                    distance_meters INTEGER,  -- Distance from listing
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE points_of_interest IS 'Nearby places of interest (schools, hospitals, etc.)';

-- =====================================================
-- VIRTUAL TOURS (360° Tours)
-- =====================================================

CREATE TABLE virtual_tours (
                               tour_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               listing_id UUID UNIQUE NOT NULL REFERENCES listings(listing_id) ON DELETE CASCADE,
                               tour_url TEXT,  -- External tour URL (if using third-party service)
                               total_scenes INTEGER DEFAULT 0,
                               tour_provider VARCHAR(50),  -- 'matterport', 'custom', 'cloudinary'
                               tour_data_json JSONB,  -- Custom tour configuration
                               is_published BOOLEAN DEFAULT FALSE,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tour_scenes (
                             scene_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             tour_id UUID REFERENCES virtual_tours(tour_id) ON DELETE CASCADE,
                             scene_name VARCHAR(100) NOT NULL,
                             panorama_url TEXT NOT NULL,  -- MinIO URL for 360° image
                             scene_order INTEGER NOT NULL,

    -- 3D Position (for navigation)
                             position_x NUMERIC,
                             position_y NUMERIC,
                             position_z NUMERIC,

    -- Hotspots (links to other scenes or info points)
                             hotspots_json JSONB,  -- [{"type": "scene", "target_scene_id": "...", "position": {...}}]

                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE virtual_tours IS 'Virtual tour configuration for listings';
COMMENT ON TABLE tour_scenes IS 'Individual 360° scenes within a virtual tour';

-- =====================================================
-- LEGAL DOCUMENTS
-- =====================================================

CREATE TABLE legal_documents (
                                 document_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 listing_id UUID REFERENCES listings(listing_id) ON DELETE CASCADE,

    -- Document Details
                                 document_type document_type NOT NULL,
                                 file_url TEXT NOT NULL,  -- MinIO URL
                                 file_name VARCHAR(255) NOT NULL,
                                 file_size BIGINT,  -- Bytes
                                 mime_type VARCHAR(100),

    -- Legal Information
                                 document_number VARCHAR(100),  -- Certificate number
                                 issue_date DATE,
                                 issuing_authority VARCHAR(255),
                                 expiry_date DATE,

    -- Verification
                                 verified BOOLEAN DEFAULT FALSE,
                                 verified_by UUID,  -- User ID of verifier (admin)
                                 verified_at TIMESTAMP WITH TIME ZONE,
                                 verification_notes TEXT,

    -- Timestamps
                                 uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE legal_documents IS 'Legal documents for listings (ownership, permits, etc.)';

-- =====================================================
-- REVIEW & AUDIT
-- =====================================================

CREATE TABLE listings_reviews (
                                  review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                  listing_id UUID REFERENCES listings(listing_id) ON DELETE CASCADE,
                                  reviewer_id UUID NOT NULL,  -- Admin/Staff user ID

    -- Review Details
                                  previous_status VARCHAR(50) NOT NULL,
                                  new_status VARCHAR(50) NOT NULL,
                                  review_action VARCHAR(50) NOT NULL,  -- 'approve', 'reject', 'request_changes'
                                  review_notes TEXT,
                                  rejection_reason TEXT,
                                  changes_requested_json JSONB,

    -- Timestamp
                                  reviewed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE listing_audit_logs (
                                    audit_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    listing_id UUID REFERENCES listings(listing_id) ON DELETE CASCADE,
                                    changed_by UUID,  -- User ID who made the change
                                    actor_type VARCHAR(20) NOT NULL,  -- 'user', 'admin', 'system'

    -- Change Details
                                    change_type VARCHAR(50) NOT NULL,  -- 'create', 'update', 'status_change', 'delete'
                                    field_name VARCHAR(100),
                                    old_value_json JSONB,
                                    new_value_json JSONB,
                                    change_reason TEXT,

    -- Timestamp
                                    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE listings_reviews IS 'Review history for listing approvals/rejections';
COMMENT ON TABLE listing_audit_logs IS 'Complete audit trail of all listing changes';

-- =====================================================
-- FEEDBACK SYSTEM (AI + Staff Feedback)
-- =====================================================

CREATE TABLE feedback_reports (
                                  feedback_report_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                  listing_id UUID REFERENCES listings(listing_id) ON DELETE CASCADE,
                                  seller_user_id UUID NOT NULL,  -- User who received feedback

    -- Report Details
                                  check_type VARCHAR(50) NOT NULL,  -- 'pre_publish', 'periodic_check', 'user_report'
                                  overall_status VARCHAR(20) DEFAULT 'pending',  -- 'pending', 'in_progress', 'resolved', 'dismissed'
                                  ai_confidence_score NUMERIC(5,2),  -- 0.00 to 100.00

    -- Review Information
                                  reviewed_by_staff_id UUID,  -- Admin who reviewed
                                  reviewed_at TIMESTAMP WITH TIME ZONE,

    -- Resubmission Tracking
                                  is_resubmission BOOLEAN DEFAULT FALSE,
                                  previous_feedback_id UUID REFERENCES feedback_reports(feedback_report_id),

    -- Timestamps
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feedback_items (
                                feedback_item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                feedback_report_id UUID REFERENCES feedback_reports(feedback_report_id) ON DELETE CASCADE,

    -- Issue Details
                                category feedback_category NOT NULL,
                                severity feedback_severity NOT NULL,
                                field_name VARCHAR(100),  -- Which field has the issue
                                error_message TEXT NOT NULL,
                                suggestion TEXT,  -- AI suggestion for fix

    -- Detection & Resolution
                                detected_by VARCHAR(20) DEFAULT 'system',  -- 'system', 'ai', 'staff', 'user_report'
                                is_fixed BOOLEAN DEFAULT FALSE,
                                fixed_at TIMESTAMP WITH TIME ZONE,

    -- Timestamp
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE feedback_reports IS 'AI and staff feedback reports for listings';
COMMENT ON TABLE feedback_items IS 'Individual feedback issues within a report';

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Location indexes
CREATE INDEX idx_provinces_country ON provinces(country_id);
CREATE INDEX idx_wards_province ON wards(province_id);

-- Listing indexes
CREATE INDEX idx_listings_user_id ON listings(user_id);
CREATE INDEX idx_listings_status ON listings(status);
CREATE INDEX idx_listings_type ON listings(listing_type);
CREATE INDEX idx_listings_property_type ON listings(property_type);
CREATE INDEX idx_listings_ward ON listings(ward_id);
CREATE INDEX idx_listings_province ON listings(province_id);
CREATE INDEX idx_listings_country ON listings(country_id);
CREATE INDEX idx_listings_created_at ON listings(created_at DESC);
CREATE INDEX idx_listings_published_at ON listings(published_at DESC) WHERE published_at IS NOT NULL;
CREATE INDEX idx_listings_submitted_at ON listings(submitted_at DESC) WHERE submitted_at IS NOT NULL;
CREATE INDEX idx_listings_price ON listings(price);

-- Composite indexes for common queries
CREATE INDEX idx_listings_user_status ON listings(user_id, status);
CREATE INDEX idx_listings_status_submitted ON listings(status, submitted_at DESC) WHERE status = 'pending_review';
CREATE INDEX idx_listings_geolocation ON listings USING GIST(geolocation) WHERE geolocation IS NOT NULL;

-- Amenities indexes
CREATE INDEX idx_listing_amenities_listing ON listing_amenities(listing_id);
CREATE INDEX idx_listing_amenities_amenity ON listing_amenities(amenity_id);

-- POI indexes
CREATE INDEX idx_poi_listing ON points_of_interest(listing_id);
CREATE INDEX idx_poi_category ON points_of_interest(category);
CREATE INDEX idx_poi_geolocation ON points_of_interest USING GIST(geolocation) WHERE geolocation IS NOT NULL;

-- Virtual tour indexes
CREATE INDEX idx_virtual_tours_listing ON virtual_tours(listing_id);
CREATE INDEX idx_tour_scenes_tour ON tour_scenes(tour_id);
CREATE INDEX idx_tour_scenes_order ON tour_scenes(tour_id, scene_order);

-- Legal document indexes
CREATE INDEX idx_legal_documents_listing ON legal_documents(listing_id);
CREATE INDEX idx_legal_documents_type ON legal_documents(document_type);
CREATE INDEX idx_legal_documents_verified ON legal_documents(verified);

-- Review & Audit indexes
CREATE INDEX idx_listings_reviews_listing ON listings_reviews(listing_id);
CREATE INDEX idx_listings_reviews_reviewer ON listings_reviews(reviewer_id);
CREATE INDEX idx_listings_reviews_reviewed_at ON listings_reviews(reviewed_at DESC);
CREATE INDEX idx_audit_logs_listing ON listing_audit_logs(listing_id);
CREATE INDEX idx_audit_logs_changed_by ON listing_audit_logs(changed_by);
CREATE INDEX idx_audit_logs_changed_at ON listing_audit_logs(changed_at DESC);

-- Feedback indexes
CREATE INDEX idx_feedback_reports_listing ON feedback_reports(listing_id);
CREATE INDEX idx_feedback_reports_seller ON feedback_reports(seller_user_id);
CREATE INDEX idx_feedback_reports_status ON feedback_reports(overall_status);
CREATE INDEX idx_feedback_items_report ON feedback_items(feedback_report_id);
CREATE INDEX idx_feedback_items_category ON feedback_items(category);

-- =====================================================
-- TRIGGERS FOR UPDATED_AT
-- =====================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_countries_updated_at BEFORE UPDATE ON countries
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_provinces_updated_at BEFORE UPDATE ON provinces
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_wards_updated_at BEFORE UPDATE ON wards
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_listings_updated_at BEFORE UPDATE ON listings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_virtual_tours_updated_at BEFORE UPDATE ON virtual_tours
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_legal_documents_updated_at BEFORE UPDATE ON legal_documents
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_feedback_reports_updated_at BEFORE UPDATE ON feedback_reports
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();