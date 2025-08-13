CREATE OR REPLACE VIEW post_base_view AS
SELECT
    p.id,
    p.title,
    p.body,
    p.post_type,
    p.author_id,
    u.external_id,
    u.internal_id,
    u.user_type,
    u.name,
    p.parent_id,
    p.answer_id,
    p.created,
    p.updated,
    p.search_vector,
    COALESCE((
        SELECT SUM(CASE type
            WHEN 'POSITIVE' THEN 1
            WHEN 'NEGATIVE' THEN -1
            ELSE 0 END)
        FROM votes
        WHERE post_id = p.id
    ), 0) AS score,
    COALESCE((
        SELECT COUNT(*)
        FROM posts a
        WHERE a.parent_id = p.id
    ), 0) AS answer_count
FROM posts p
LEFT JOIN users u ON p.author_id = u.id;

CREATE OR REPLACE FUNCTION get_posts_rich(
    user_id_param BIGINT,
    parent_id_param BIGINT,
    limit_param INTEGER,
    offset_param BIGINT
)
RETURNS TABLE (
    id BIGINT,
    title VARCHAR,
    body TEXT,
    post_type post_type,
    author_id BIGINT,
    external_id BIGINT,
    internal_id UUID,
    user_type user_type,
    name VARCHAR,
    parent_id BIGINT,
    answer_id BIGINT,
    created TIMESTAMP,
    updated TIMESTAMP,
    score BIGINT,
    answer_count BIGINT,
    vote_type vote_type,
    total_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    WITH filtered_posts AS (
        SELECT
                pbv.id,
                pbv.title,
                pbv.body,
                pbv.post_type,
                pbv.author_id,
                pbv.external_id,
                pbv.internal_id,
                pbv.user_type,
                pbv.name,
                pbv.parent_id,
                pbv.answer_id,
                pbv.created,
                pbv.updated,
                pbv.score,
                pbv.answer_count,
                v.type
        FROM post_base_view pbv
        LEFT JOIN votes v ON pbv.id = v.post_id AND user_id_param = v.author_id
        WHERE
            (parent_id_param IS NULL AND pbv.parent_id IS NULL)
            OR (parent_id_param IS NOT NULL AND pbv.parent_id = parent_id_param)
    )
    SELECT
        *,
        COUNT(*) OVER() AS total_count
    FROM filtered_posts
    ORDER BY created DESC
    LIMIT limit_param OFFSET offset_param;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_post_rich(
    user_id_param BIGINT,
    post_id_param BIGINT
)
RETURNS TABLE (
    id BIGINT,
    title VARCHAR,
    body TEXT,
    post_type post_type,
    author_id BIGINT,
    external_id BIGINT,
    internal_id UUID,
    user_type user_type,
    name VARCHAR,
    parent_id BIGINT,
    answer_id BIGINT,
    created TIMESTAMP,
    updated TIMESTAMP,
    score BIGINT,
    answer_count BIGINT,
    vote_type vote_type
) AS $$
BEGIN
    RETURN QUERY
    SELECT
            pbv.id,
            pbv.title,
            pbv.body,
            pbv.post_type,
            pbv.author_id,
            pbv.external_id,
            pbv.internal_id,
            pbv.user_type,
            pbv.name,
            pbv.parent_id,
            pbv.answer_id,
            pbv.created,
            pbv.updated,
            pbv.score,
            pbv.answer_count,
            v.type
    FROM post_base_view pbv
    LEFT JOIN votes v ON pbv.id = v.post_id AND user_id_param = v.author_id
    WHERE pbv.id = post_id_param;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION search_posts_function(
    search_query TEXT,
    user_id_param BIGINT,
    limit_param INTEGER,
    offset_param BIGINT
)
RETURNS TABLE (
    id BIGINT,
    title VARCHAR,
    body TEXT,
    post_type post_type,
    author_id BIGINT,
    external_id BIGINT,
    internal_id UUID,
    user_type user_type,
    name VARCHAR,
    parent_id BIGINT,
    answer_id BIGINT,
    created TIMESTAMP,
    updated TIMESTAMP,
    score BIGINT,
    answer_count BIGINT,
    vote_type vote_type,
    total_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    WITH tsq AS (
        SELECT (websearch_to_tsquery('english', search_query) || websearch_to_tsquery('russian', search_query)) AS q_union
    )
    SELECT
            pbv.id,
            pbv.title,
            pbv.body,
            pbv.post_type,
            pbv.author_id,
            pbv.external_id,
            pbv.internal_id,
            pbv.user_type,
            pbv.name,
            pbv.parent_id,
            pbv.answer_id,
            pbv.created,
            pbv.updated,
            pbv.score,
            pbv.answer_count,
            v.type,
            COUNT(*) OVER() AS total_count
    FROM post_base_view as pbv
    LEFT JOIN votes v ON pbv.id = v.post_id AND user_id_param = v.author_id
    CROSS JOIN tsq
    WHERE pbv.search_vector @@ tsq.q_union
    ORDER BY ts_rank_cd(pbv.search_vector, tsq.q_union, 32) DESC
    LIMIT limit_param OFFSET offset_param;
END;
$$ LANGUAGE plpgsql;