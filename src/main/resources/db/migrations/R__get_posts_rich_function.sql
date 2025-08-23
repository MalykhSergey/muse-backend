DROP FUNCTION IF EXISTS get_posts_rich;

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
    is_notification BOOLEAN,
    tags JSONB,
    total_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    WITH scores AS (
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
        WHERE
            (parent_id_param IS NULL AND p.parent_id IS NULL) -- CHECK TYPE
            OR (parent_id_param IS NOT NULL AND p.parent_id = parent_id_param)
    ),
    post_ids AS (
        SELECT p.id
        FROM posts p
        LEFT JOIN scores s ON s.post_id = p.id
        WHERE
            (parent_id_param IS NULL AND p.post_type = 'QUESTION')
            OR (parent_id_param IS NOT NULL AND p.parent_id = parent_id_param)
        ORDER BY
            CASE WHEN sort_by = 'CREATED' AND sort_dir = 'ASC'  THEN p.created END ASC,
            CASE WHEN sort_by = 'CREATED' AND sort_dir = 'DESC' THEN p.created END DESC,
            CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'ASC'  THEN s.score END ASC NULLS FIRST,
            CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'DESC' THEN s.score END DESC NULLS LAST,
            p.created DESC
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
        is_notifications,
        t.tags,
        total.cnt AS total_count
    FROM post_ids ids
    JOIN posts p ON p.id = ids.id
    LEFT JOIN users u ON p.author_id = u.id
    LEFT JOIN scores s ON s.post_id = p.id
    LEFT JOIN answers a ON a.post_id = p.id
    LEFT JOIN tags_agg t ON t.post_id = p.id
    LEFT JOIN user_votes uv ON uv.post_id = p.id
    LEFT JOIN posts_subscribes ps ON ps.post_id = p.id AND ps.user_id = user_id_param
    CROSS JOIN total
    ORDER BY
        CASE WHEN sort_by = 'CREATED' AND sort_dir = 'ASC'  THEN p.created END ASC,
        CASE WHEN sort_by = 'CREATED' AND sort_dir = 'DESC' THEN p.created END DESC,
        CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'ASC'  THEN s.score END ASC NULLS FIRST,
        CASE WHEN sort_by = 'SCORE'   AND sort_dir = 'DESC' THEN s.score END DESC NULLS LAST,
        p.created DESC;
END;
$$ LANGUAGE plpgsql;
