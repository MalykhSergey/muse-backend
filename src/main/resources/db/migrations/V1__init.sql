CREATE TYPE post_type AS ENUM ('QUESTION', 'ANSWER');
CREATE TYPE vote_type AS ENUM ('POSITIVE', 'NEGATIVE');
CREATE TYPE user_type AS ENUM ('EXTERNAL', 'INTERNAL');


-- Таблица пользователей
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_type user_type NOT NULL,
    internal_id UUID UNIQUE,
    external_id BIGINT UNIQUE,
    name VARCHAR(255),
    CHECK (
        (user_type = 'INTERNAL' AND internal_id IS NOT NULL AND external_id IS NULL) OR
        (user_type = 'EXTERNAL' AND external_id IS NOT NULL AND internal_id IS NULL)
    )
);

-- Таблица постов
CREATE TABLE posts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255),
    body TEXT,
    post_type post_type,
    author_id BIGINT REFERENCES users(id),
    parent_id BIGINT REFERENCES posts(id),
    answer_id BIGINT REFERENCES posts(id),
    created TIMESTAMP,
    updated TIMESTAMP
);

-- Таблица комментариев
CREATE TABLE comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    body TEXT,
    author_id BIGINT REFERENCES users(id),
    post_id BIGINT REFERENCES posts(id),
    created TIMESTAMP,
    updated TIMESTAMP
);

-- Таблица голосов
CREATE TABLE votes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id BIGINT REFERENCES users(id),
    post_id BIGINT REFERENCES posts(id),
    created TIMESTAMP,
    type vote_type
);

-- Таблица тегов
CREATE TABLE tags (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    post_id BIGINT REFERENCES posts(id)
);

-- Таблица подписок на посты
CREATE TABLE posts_subscribes (
    post_id BIGINT REFERENCES posts(id),
    user_id BIGINT REFERENCES users(id),
    PRIMARY KEY (post_id, user_id)
);

-- Таблица подписок на теги
CREATE TABLE tags_subscribes (
    tag_id BIGINT REFERENCES tags(id),
    user_id BIGINT REFERENCES users(id),
    PRIMARY KEY (tag_id, user_id)
);

-- CREATE INDEX idx_posts_author_id ON posts(author_id);
-- CREATE INDEX idx_posts_parent_id ON posts(parent_id);
-- CREATE INDEX idx_posts_answer_id ON posts(answer_id);
-- CREATE INDEX idx_posts_created ON posts(created);
-- CREATE INDEX idx_posts_type ON posts(post_type);
--
-- CREATE INDEX idx_comments_author_id ON comments(author_id);
-- CREATE INDEX idx_comments_post_id ON comments(post_id);
-- CREATE INDEX idx_comments_created ON comments(created);
--
-- CREATE INDEX idx_votes_author_id ON votes(author_id);
-- CREATE INDEX idx_votes_post_id ON votes(post_id);
-- CREATE INDEX idx_votes_type ON votes(type);
--
-- CREATE INDEX idx_tags_name ON tags(name);
-- CREATE INDEX idx_tags_post_id ON tags(post_id);

COMMENT ON TABLE users IS 'Пользователи системы';
COMMENT ON COLUMN users.id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN users.name IS 'Имя пользователя';

COMMENT ON TABLE posts IS 'Посты (вопросы и ответы)';
COMMENT ON COLUMN posts.id IS 'Уникальный идентификатор поста';
COMMENT ON COLUMN posts.title IS 'Заголовок поста';
COMMENT ON COLUMN posts.body IS 'Содержимое поста';
COMMENT ON COLUMN posts.post_type IS 'Тип поста: QUESTION или ANSWER';
COMMENT ON COLUMN posts.author_id IS 'Идентификатор автора поста';
COMMENT ON COLUMN posts.parent_id IS 'Идентификатор родительского поста (для ответов)';
COMMENT ON COLUMN posts.answer_id IS 'Идентификатор принятого ответа (для вопросов)';
COMMENT ON COLUMN posts.created IS 'Дата и время создания';
COMMENT ON COLUMN posts.updated IS 'Дата и время последнего обновления';

COMMENT ON TABLE comments IS 'Комментарии к постам';
COMMENT ON COLUMN comments.id IS 'Уникальный идентификатор комментария';
COMMENT ON COLUMN comments.body IS 'Текст комментария';
COMMENT ON COLUMN comments.author_id IS 'Идентификатор автора комментария';
COMMENT ON COLUMN comments.post_id IS 'Идентификатор поста';
COMMENT ON COLUMN comments.created IS 'Дата и время создания';
COMMENT ON COLUMN comments.updated IS 'Дата и время последнего обновления';

COMMENT ON TABLE votes IS 'Голоса за посты';
COMMENT ON COLUMN votes.id IS 'Уникальный идентификатор голоса';
COMMENT ON COLUMN votes.author_id IS 'Идентификатор автора голоса';
COMMENT ON COLUMN votes.post_id IS 'Идентификатор поста';
COMMENT ON COLUMN votes.created IS 'Дата и время создания голоса';
COMMENT ON COLUMN votes.type IS 'Тип голоса: POSITIVE или NEGATIVE';

COMMENT ON TABLE tags IS 'Теги для категоризации постов';
COMMENT ON COLUMN tags.id IS 'Уникальный идентификатор тега';
COMMENT ON COLUMN tags.name IS 'Название тега';
COMMENT ON COLUMN tags.post_id IS 'Идентификатор поста с описанием тега';

COMMENT ON TABLE posts_subscribes IS 'Подписки пользователей на посты';
COMMENT ON COLUMN posts_subscribes.post_id IS 'Идентификатор поста';
COMMENT ON COLUMN posts_subscribes.user_id IS 'Идентификатор пользователя';

COMMENT ON TABLE tags_subscribes IS 'Подписки пользователей на теги';
COMMENT ON COLUMN tags_subscribes.tag_id IS 'Идентификатор тега';
COMMENT ON COLUMN tags_subscribes.user_id IS 'Идентификатор пользователя';