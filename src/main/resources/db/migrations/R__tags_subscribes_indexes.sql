CREATE INDEX IF NOT EXISTS idx_tags_subscribes_user_tag ON tags_subscribes(user_id, tag_id);
