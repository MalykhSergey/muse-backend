CREATE INDEX IF NOT EXISTS idx_votes_post_id ON votes(post_id);

-- Интересный
CREATE INDEX IF NOT EXISTS idx_votes_author_post ON votes(author_id, post_id) INCLUDE (type);
