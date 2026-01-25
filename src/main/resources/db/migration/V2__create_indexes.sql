-- =====================================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- =====================================================

-- POST Table Indexes
CREATE INDEX idx_post_user_id ON post(user_id);
CREATE INDEX idx_post_status ON post(post_status);
CREATE INDEX idx_post_property_type ON post(property_type);
CREATE INDEX idx_post_listing_type ON post(listing_type);
CREATE INDEX idx_post_created_at ON post(created_at DESC);
CREATE INDEX idx_post_published_at ON post(published_at DESC) WHERE published_at IS NOT NULL;
CREATE INDEX idx_post_submitted_at ON post(submitted_at DESC) WHERE submitted_at IS NOT NULL;

-- Composite indexes for common queries
CREATE INDEX idx_post_user_status ON post(user_id, post_status);
CREATE INDEX idx_post_status_submitted ON post(post_status, submitted_at DESC)
    WHERE post_status = 'PENDING';

-- GIN index for JSONB columns (for JSON queries)
CREATE INDEX idx_post_address_json ON post USING GIN(address_json);
CREATE INDEX idx_post_amenities_json ON post USING GIN(amenities_json);

-- POST_REVIEW_HISTORY Indexes
CREATE INDEX idx_review_post_id ON post_review_history(post_id);
CREATE INDEX idx_review_reviewer_id ON post_review_history(reviewer_id);
CREATE INDEX idx_review_created_at ON post_review_history(created_at DESC);

-- POST_CREDIT_LOCK Indexes
CREATE INDEX idx_credit_lock_post_id ON post_credit_lock(post_id);
CREATE INDEX idx_credit_lock_user_id ON post_credit_lock(user_id);
CREATE INDEX idx_credit_lock_locked_at ON post_credit_lock(locked_at DESC);
CREATE INDEX idx_credit_lock_unlocked_at ON post_credit_lock(unlocked_at)
    WHERE unlocked_at IS NOT NULL;

-- Index for finding active locks
CREATE INDEX idx_credit_lock_active ON post_credit_lock(user_id)
    WHERE unlocked_at IS NULL;