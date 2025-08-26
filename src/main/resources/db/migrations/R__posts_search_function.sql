DROP FUNCTION IF EXISTS search_posts_function;

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
    created TIMESTAMPTZ,
    updated TIMESTAMPTZ,
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
    scores AS (
        SELECT v.post_id,
               SUM(CASE v.type WHEN 'POSITIVE' THEN 1
                               WHEN 'NEGATIVE' THEN -1 ELSE 0 END) AS score
        FROM votes v
        GROUP BY v.post_id
    ),
    answers AS (
        SELECT p2.parent_id AS post_id, COUNT(*) AS answer_count
        FROM posts p2
        WHERE p2.parent_id IS NOT NULL
        GROUP BY p2.parent_id
    ),
    tags_agg AS (
        SELECT pt.post_id,
               jsonb_agg(DISTINCT jsonb_build_object('id', t.id, 'name', t.name, 'postId', pt.post_id)) AS tags
        FROM posts_tags pt
        JOIN tags t ON pt.tag_id = t.id
        GROUP BY pt.post_id
    ),
    user_votes AS (
        SELECT v2.post_id, v2.type
        FROM votes v2
        WHERE v2.author_id = user_id_param
    ),
    total AS (
        SELECT COUNT(*)::BIGINT AS cnt
        FROM posts p
        CROSS JOIN tsq
        WHERE p.search_vector @@ tsq.q_union
    ),
    post_ids AS (
        SELECT p.id,
               ts_rank_cd(p.search_vector, tsq.q_union, 32) AS rank_val
        FROM posts p
        CROSS JOIN tsq
        WHERE p.search_vector @@ tsq.q_union
        ORDER BY
            CASE WHEN sort_by = 'RANK'    AND sort_dir = 'ASC'  THEN ts_rank_cd(p.search_vector, tsq.q_union, 32) END ASC,
            CASE WHEN sort_by = 'RANK'    AND sort_dir = 'DESC' THEN ts_rank_cd(p.search_vector, tsq.q_union, 32) END DESC,
            CASE WHEN sort_by = 'CREATED' AND sort_dir = 'ASC'  THEN p.created END ASC,
            CASE WHEN sort_by = 'CREATED' AND sort_dir = 'DESC' THEN p.created END DESC,
            CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'ASC'  THEN 0 END ASC NULLS FIRST,
            CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'DESC' THEN 0 END DESC NULLS LAST,
            ts_rank_cd(p.search_vector, tsq.q_union, 32) DESC
        LIMIT limit_param OFFSET offset_param
    )
    SELECT
        p.id,
        p.title,
        p.body,
        p.post_type,
        p.author_id,
        u.external_id,
        u.internal_id,
        u.user_type,
        u.name AS author_name,
        p.parent_id,
        p.answer_id,
        p.created,
        p.updated,
        COALESCE(s.score, 0) AS score,
        COALESCE(a.answer_count, 0) AS answer_count,
        uv.type AS vote_type,
        t.tags,
        total.cnt AS total_count
    FROM post_ids ids
    JOIN posts p ON p.id = ids.id
    LEFT JOIN users u ON p.author_id = u.id
    LEFT JOIN scores s ON s.post_id = p.id
    LEFT JOIN answers a ON a.post_id = p.id
    LEFT JOIN tags_agg t ON t.post_id = p.id
    LEFT JOIN user_votes uv ON uv.post_id = p.id
    CROSS JOIN total
    ORDER BY
        CASE WHEN sort_by = 'RANK'    AND sort_dir = 'ASC'  THEN ids.rank_val END ASC,
        CASE WHEN sort_by = 'RANK'    AND sort_dir = 'DESC' THEN ids.rank_val END DESC,
        CASE WHEN sort_by = 'CREATED' AND sort_dir = 'ASC'  THEN p.created END ASC,
        CASE WHEN sort_by = 'CREATED' AND sort_dir = 'DESC' THEN p.created END DESC,
        CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'ASC'  THEN s.score END ASC NULLS FIRST,
        CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'DESC' THEN s.score END DESC NULLS LAST,
        ids.rank_val DESC;
END;
$$ LANGUAGE plpgsql;