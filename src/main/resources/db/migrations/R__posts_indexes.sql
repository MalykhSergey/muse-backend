CREATE INDEX IF NOT EXISTS idx_posts_search_vector ON posts USING GIN (search_vector);
CREATE INDEX idx_posts_parent_id ON posts(parent_id);
CREATE INDEX idx_posts_created ON posts(created);
-- Спорно, подлежит тестированию
CREATE INDEX idx_posts_parent_id_created ON posts(parent_id, created DESC);
