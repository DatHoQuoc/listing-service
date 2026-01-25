-- =====================================================
-- LISTING SERVICE - DATABASE SCHEMA
-- =====================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- ENUMS
-- =====================================================

CREATE TYPE post_status AS ENUM (
    'DRAFT',
    'PENDING',
    'PUBLISHED',
    'REJECTED',
    'EXPIRED',
    'DELETED'
);

CREATE TYPE property_type AS ENUM (
    'APARTMENT',
    'HOUSE',
    'VILLA',
    'LAND',
    'COMMERCIAL'
);

CREATE TYPE listing_type AS ENUM (
    'SALE',
    'RENT'
);

CREATE TYPE price_unit AS ENUM (
    'VND',
    'USD'
);

CREATE TYPE price_period AS ENUM (
    'MONTHLY',
    'QUARTERLY',
    'YEARLY',
    'TOTAL'
);

CREATE TYPE review_action AS ENUM (
    'APPROVE',
    'REJECT',
    'REQUEST_CHANGES'
);

-- =====================================================
-- POST (LISTING) TABLE
-- =====================================================

CREATE TABLE post (
                      post_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                      user_id UUID NOT NULL,

    -- Basic Information
                      title VARCHAR(500) NOT NULL,
                      description TEXT NOT NULL,

    -- Property Details
                      property_type property_type NOT NULL,
                      listing_type listing_type NOT NULL,
                      price DECIMAL(15,2) NOT NULL,
                      price_unit price_unit DEFAULT 'VND',
                      price_period price_period,
                      area_sqm DECIMAL(10,2) NOT NULL,
                      bedrooms INTEGER,
                      bathrooms INTEGER,

    -- Location & Media (JSON)
                      address_json JSONB NOT NULL,
                      amenities_json JSONB,
                      images_json JSONB,
                      documents_json JSONB,

    -- Status & Credits
                      post_status post_status DEFAULT 'DRAFT' NOT NULL,
                      is_free_post BOOLEAN DEFAULT FALSE NOT NULL,
                      credits_locked INTEGER DEFAULT 0 NOT NULL,
                      credits_charged INTEGER DEFAULT 0 NOT NULL,
                      credits_refunded INTEGER DEFAULT 0 NOT NULL,

    -- Review Information
                      rejection_reason TEXT,
                      admin_notes TEXT,

    -- Metrics
                      view_count INTEGER DEFAULT 0,
                      favorite_count INTEGER DEFAULT 0,

    -- Timestamps
                      submitted_at TIMESTAMP,
                      reviewed_at TIMESTAMP,
                      published_at TIMESTAMP,
                      expired_at TIMESTAMP,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Constraints
                      CONSTRAINT chk_price_positive CHECK (price > 0),
                      CONSTRAINT chk_area_positive CHECK (area_sqm > 0),
                      CONSTRAINT chk_bedrooms_positive CHECK (bedrooms >= 0 OR bedrooms IS NULL),
                      CONSTRAINT chk_bathrooms_positive CHECK (bathrooms >= 0 OR bathrooms IS NULL)
);

-- =====================================================
-- POST_REVIEW_HISTORY TABLE
-- =====================================================

CREATE TABLE post_review_history (
                                     review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                     post_id UUID NOT NULL REFERENCES post(post_id) ON DELETE CASCADE,
                                     reviewer_id UUID NOT NULL,  -- Admin/Staff user ID

    -- Review Details
                                     previous_status VARCHAR(50) NOT NULL,
                                     new_status VARCHAR(50) NOT NULL,
                                     action review_action NOT NULL,
                                     review_notes TEXT,
                                     rejection_reason TEXT,
                                     changes_requested_json JSONB,

    -- Timestamp
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- =====================================================
-- POST_CREDIT_LOCK TABLE
-- =====================================================

CREATE TABLE post_credit_lock (
                                  lock_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                  post_id UUID UNIQUE NOT NULL REFERENCES post(post_id) ON DELETE CASCADE,
                                  user_id UUID NOT NULL,

    -- Lock Details
                                  credits_locked INTEGER NOT NULL,
                                  lock_reason VARCHAR(255) NOT NULL,
                                  locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Unlock Details
                                  unlocked_at TIMESTAMP,
                                  unlock_reason VARCHAR(255),  -- 'APPROVED', 'REJECTED', 'EXPIRED'
                                  transaction_id UUID,  -- Reference to credit transaction (if applicable)

    -- Constraints
                                  CONSTRAINT chk_credits_locked_positive CHECK (credits_locked > 0)
);

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

CREATE TRIGGER update_post_updated_at
    BEFORE UPDATE ON post
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- COMMENTS (Documentation)
-- =====================================================

COMMENT ON TABLE post IS 'Main listing/post table for real estate properties';
COMMENT ON TABLE post_review_history IS 'Audit trail for listing reviews';
COMMENT ON TABLE post_credit_lock IS 'Credit locking mechanism for pending posts';

COMMENT ON COLUMN post.address_json IS 'JSON: {street, ward, district, city, coordinates: {lat, lng}}';
COMMENT ON COLUMN post.images_json IS 'JSON: [{url, type: "regular|360", order}]';
COMMENT ON COLUMN post.documents_json IS 'JSON: [{url, filename, type, uploadedAt}]';