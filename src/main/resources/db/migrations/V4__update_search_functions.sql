DROP FUNCTION get_posts_rich(BIGINT, BIGINT, INT, BIGINT);
DROP FUNCTION get_post_rich(BIGINT, BIGINT);
DROP FUNCTION search_posts_function(TEXT, BIGINT, INT, BIGINT);

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
    ), 0) AS answer_count,
    jsonb_agg(DISTINCT jsonb_build_object('id', t.id, 'name', t.name, 'postId', t.post_id)) FILTER (WHERE t.id IS NOT NULL) AS tags
FROM posts p
LEFT JOIN users u ON p.author_id = u.id
LEFT JOIN posts_tags pt ON p.id = pt.post_id
LEFT JOIN tags t ON pt.tag_id = t.id
GROUP BY
p.id, p.title, p.body, p.post_type, p.author_id,
u.external_id, u.internal_id, u.user_type, u.name,
p.parent_id, p.answer_id, p.created, p.updated, p.search_vector;

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
    author_name VARCHAR,
    parent_id BIGINT,
    answer_id BIGINT,
    created TIMESTAMP,
    updated TIMESTAMP,
    score BIGINT,
    answer_count BIGINT,
    vote_type vote_type,
    tags JSONB,
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
                v.type,
                pbv.tags
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

CREATE OR REPLACE FUNCTION get_posts_rich(
    user_id_param BIGINT,
    parent_id_param BIGINT,
    limit_param INTEGER,
    offset_param BIGINT,
    sort_by TEXT DEFAULT 'CREATED',
    sort_dir TEXT DEFAULT 'DESC'
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
    author_name VARCHAR,
    parent_id BIGINT,
    answer_id BIGINT,
    created TIMESTAMP,
    updated TIMESTAMP,
    score BIGINT,
    answer_count BIGINT,
    vote_type vote_type,
    tags JSONB,
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
                v.type,
                pbv.tags
        FROM post_base_view pbv
        LEFT JOIN votes v ON pbv.id = v.post_id AND user_id_param = v.author_id
        WHERE
            (parent_id_param IS NULL AND pbv.parent_id IS NULL)
            OR (parent_id_param IS NOT NULL AND pbv.parent_id = parent_id_param)
    )
    SELECT
        *,
        COUNT(*) OVER() AS total_count
    FROM filtered_posts fp
    ORDER BY
            CASE WHEN sort_by = 'CREATED' AND sort_dir = 'ASC'  THEN fp.created END ASC,
            CASE WHEN sort_by = 'CREATED' AND sort_dir = 'DESC' THEN fp.created END DESC,
            CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'ASC'  THEN fp.score   END ASC,
            CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'DESC' THEN fp.score   END DESC,
            fp.created DESC
    LIMIT limit_param OFFSET offset_param;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION search_posts_function(
    search_query TEXT,
    user_id_param BIGINT,
    limit_param INTEGER,
    offset_param BIGINT,
    sort_by TEXT DEFAULT 'RANK',
    sort_dir TEXT DEFAULT 'DESC'
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
    author_name VARCHAR,
    parent_id BIGINT,
    answer_id BIGINT,
    created TIMESTAMP,
    updated TIMESTAMP,
    score BIGINT,
    answer_count BIGINT,
    vote_type vote_type,
    tags JSONB,
    total_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    WITH tsq AS (
        SELECT (websearch_to_tsquery('english', search_query) || websearch_to_tsquery('russian', search_query)) AS q_union
    ),
    ranked_posts AS (
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
                pbv.tags,
                ts_rank_cd(pbv.search_vector, tsq.q_union, 32) AS rank_val
        FROM post_base_view as pbv
        LEFT JOIN votes v ON pbv.id = v.post_id AND user_id_param = v.author_id
        CROSS JOIN tsq
        WHERE pbv.search_vector @@ tsq.q_union
    )
    SELECT
            id,
            title,
            body,
            post_type,
            author_id,
            external_id,
            internal_id,
            user_type,
            name,
            parent_id,
            answer_id,
            created,
            updated,
            score,
            answer_count,
            vote_type,
            tags,
            COUNT(*) OVER() AS total_count
        FROM ranked_posts rp
    ORDER BY
        CASE WHEN sort_by = 'RANK'    AND sort_dir = 'ASC'  THEN rp.rank_val  END ASC,
        CASE WHEN sort_by = 'RANK'    AND sort_dir = 'DESC' THEN rp.rank_val  END DESC,
        CASE WHEN sort_by = 'CREATED' AND sort_dir = 'ASC'  THEN rp.created END ASC,
        CASE WHEN sort_by = 'CREATED' AND sort_dir = 'DESC' THEN rp.created END DESC,
        CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'ASC'  THEN rp.score END ASC,
        CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'DESC' THEN rp.score END DESC,
        rp.rank_val DESC
    LIMIT limit_param OFFSET offset_param;
END;
$$ LANGUAGE plpgsql;