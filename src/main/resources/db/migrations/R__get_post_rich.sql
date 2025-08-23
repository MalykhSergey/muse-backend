DROP FUNCTION IF EXISTS get_post_rich;

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
    author_name VARCHAR,
    parent_id BIGINT,
    answer_id BIGINT,
    created TIMESTAMP,
    updated TIMESTAMP,
    score BIGINT,
    answer_count BIGINT,
    vote_type vote_type,
    is_notification BOOLEAN,
    tags JSONB
) AS $$
BEGIN
    RETURN QUERY
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
        v.type,
        is_notifications,
        COALESCE(
            (SELECT jsonb_agg(DISTINCT jsonb_build_object('id', t.id, 'name', t.name, 'postId', t.post_id))
             FROM posts_tags pt
             JOIN tags t ON pt.tag_id = t.id
             WHERE pt.post_id = p.id),
            '[]'::jsonb
        ) AS tags
    FROM posts p
    LEFT JOIN users u ON p.author_id = u.id
    LEFT JOIN votes v ON p.id = v.post_id AND user_id_param = v.author_id
    LEFT JOIN posts_subscribes ps ON ps.post_id = p.id AND ps.user_id = user_id_param
    WHERE p.id = post_id_param
    GROUP BY
        p.id, p.title, p.body, p.post_type, p.author_id,
        u.external_id, u.internal_id, u.user_type, u.name,
        p.parent_id, p.answer_id, p.created, p.updated,
        v.type;
END;
$$ LANGUAGE plpgsql;