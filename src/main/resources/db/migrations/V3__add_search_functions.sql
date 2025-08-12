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
            p.id,
            p.title,
            p.body,
            p.post_type,
            p.author_id,
            users.external_id,
            users.internal_id,
            users.user_type,
            users.name,
            p.parent_id,
            p.answer_id,
            p.created,
            p.updated,
            COALESCE((
                SELECT SUM(CASE v_inner.type
                    WHEN 'POSITIVE' THEN 1
                    WHEN 'NEGATIVE' THEN -1
                    ELSE 0
                END)
                FROM votes v_inner
                WHERE v_inner.post_id = p.id
            ), 0) AS score,
            COALESCE((
                SELECT COUNT(*)
                FROM posts a
                WHERE a.parent_id = p.id
            ), 0) AS answer_count,
            v.type
        FROM posts p
        LEFT JOIN users ON p.author_id = users.id
        LEFT JOIN votes v ON p.id = v.post_id AND user_id_param = v.author_id
        WHERE
            (parent_id_param IS NULL AND p.parent_id IS NULL)
            OR (parent_id_param IS NOT NULL AND p.parent_id = parent_id_param)
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
    post_id_param BIGINT,
    user_id_param BIGINT
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
    SELECT
        p.id,
        p.title,
        p.body,
        p.post_type,
        p.author_id,
        users.external_id,
        users.internal_id,
        users.user_type,
        users.name,
        p.parent_id,
        p.answer_id,
        p.created,
        p.updated,
        COALESCE((
            SELECT SUM(CASE v_inner.type
                WHEN 'POSITIVE' THEN 1
                WHEN 'NEGATIVE' THEN -1
                ELSE 0
            END)
            FROM votes v_inner
            WHERE v_inner.post_id = p.id
        ), 0) AS score,
        COALESCE((
            SELECT COUNT(*)
            FROM posts a
            WHERE a.parent_id = p.id
        ), 0) AS answer_count,
        v.type,
        1::BIGINT as total_count
    FROM posts p
    LEFT JOIN users ON p.author_id = users.id
    LEFT JOIN votes v ON p.id = v.post_id AND user_id_param = v.author_id
    WHERE p.id = post_id_param;
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
    SELECT p.id,
           p.title,
           p.body,
           p.post_type,
           p.author_id,
           users.external_id,
           users.internal_id,
           users.user_type,
           users.name,
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
               WHERE a.parent_id = p.id), 0) as answer_count,
           votes.type,
           COUNT(*) OVER() AS total_count
           FROM posts p
           LEFT JOIN users ON p.author_id = users.id
           LEFT JOIN votes ON p.id = votes.post_id AND user_id_param = votes.author_id
           CROSS JOIN tsq
           WHERE p.search_vector @@ tsq.q_union
           ORDER BY ts_rank_cd(p.search_vector, tsq.q_union, 32) DESC
           LIMIT limit_param OFFSET offset_param;
END;
$$ LANGUAGE plpgsql;