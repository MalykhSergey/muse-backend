CREATE TABLE favourites(
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (user_id, post_id)
);