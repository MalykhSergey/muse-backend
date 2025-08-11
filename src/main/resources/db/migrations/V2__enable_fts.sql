ALTER TABLE posts
  ADD COLUMN search_vector tsvector
    GENERATED ALWAYS AS (
      setweight(to_tsvector('english', coalesce(title,'')), 'A') ||
      setweight(to_tsvector('english', coalesce(body,'')),  'B') ||
      setweight(to_tsvector('russian',  coalesce(title,'')), 'A') ||
      setweight(to_tsvector('russian',  coalesce(body,'')),  'B')
    ) STORED;

CREATE INDEX idx_posts_search_vector ON posts USING GIN (search_vector);
