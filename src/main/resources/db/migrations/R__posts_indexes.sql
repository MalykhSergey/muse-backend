CREATE INDEX IF NOT EXISTS idx_posts_search_vector ON posts USING GIN (search_vector);
