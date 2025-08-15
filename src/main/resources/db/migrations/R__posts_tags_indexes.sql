CREATE INDEX IF NOT EXISTS  idx_posts_tags_post_id ON posts_tags(post_id);
CREATE INDEX IF NOT EXISTS  idx_posts_tags_tag_id ON posts_tags(tag_id);
