-- Вставка пользователей
INSERT INTO users (user_type, external_id, name) VALUES
('EXTERNAL', 1001, 'John Doe'),
('EXTERNAL', 1002, 'Jane Smith'),
('EXTERNAL', 1003, 'Alex Johnson'),
('EXTERNAL', 1004, 'Emily Brown'),
('EXTERNAL', 1005, 'Michael Davis'),
('EXTERNAL', 2001, 'System Admin'),
('EXTERNAL', 2002, 'Moderator Bot'),
('EXTERNAL', 1006, 'Sarah Wilson'),
('EXTERNAL', 1007, 'David Miller'),
('EXTERNAL', 1008, 'Lisa Taylor');

-- Вставка вопросов (постов типа QUESTION)
INSERT INTO posts (title, body, post_type, author_id, created, updated) VALUES
('How to optimize SQL queries?', 'I have a complex query that takes too long to execute. What are some best practices for SQL optimization?', 'QUESTION', 1, '2023-01-15 10:00:00', '2023-01-15 10:00:00'),
('Difference between INNER JOIN and LEFT JOIN', 'Can someone explain the practical difference between these two join types with examples?', 'QUESTION', 2, '2023-01-16 11:30:00', '2023-01-16 11:30:00'),
('Best practices for database indexing', 'When should I create indexes and what columns should I include?', 'QUESTION', 3, '2023-01-17 09:15:00', '2023-01-17 09:15:00'),
('How to handle NULL values in PostgreSQL?', 'I''m getting unexpected results when comparing NULL values. What''s the proper way to handle them?', 'QUESTION', 4, '2023-01-18 14:20:00', '2023-01-18 14:20:00'),
('Understanding database transactions', 'Can someone explain ACID properties with real-world examples?', 'QUESTION', 5, '2023-01-19 16:45:00', '2023-01-19 16:45:00');

-- Вставка ответов (постов типа ANSWER)
INSERT INTO posts (body, post_type, author_id, parent_id, created, updated) VALUES
('For SQL optimization, consider: 1) Adding proper indexes, 2) Avoiding SELECT *, 3) Using EXPLAIN to analyze the query plan.', 'ANSWER', 2, 1, '2023-01-15 11:20:00', '2023-01-15 11:20:00'),
('INNER JOIN returns only matching rows from both tables, while LEFT JOIN returns all rows from the left table and matching rows from the right table (or NULLs if no match).', 'ANSWER', 1, 2, '2023-01-16 13:45:00', '2023-01-16 13:45:00'),
('Create indexes on columns used in WHERE, JOIN, and ORDER BY clauses. Avoid over-indexing as it slows down inserts/updates.', 'ANSWER', 4, 3, '2023-01-17 10:30:00', '2023-01-17 10:30:00'),
('In PostgreSQL, use IS NULL or IS NOT NULL for NULL comparisons. Regular comparison operators (=, !=) will not work as expected with NULL.', 'ANSWER', 3, 4, '2023-01-18 15:40:00', '2023-01-18 15:40:00'),
('ACID stands for Atomicity (all or nothing), Consistency (valid state transitions), Isolation (concurrent transactions don''t interfere), Durability (committed changes persist).', 'ANSWER', 5, 5, '2023-01-19 17:50:00', '2023-01-19 17:50:00'),
('Another optimization tip: consider rewriting subqueries as JOINs where possible, as they often perform better.', 'ANSWER', 6, 1, '2023-01-15 12:30:00', '2023-01-15 12:30:00');

-- Обновление вопросов с указанием принятых ответов
UPDATE posts SET answer_id = 6 WHERE id = 1;
UPDATE posts SET answer_id = 7 WHERE id = 2;
UPDATE posts SET answer_id = 9 WHERE id = 4;

-- Вставка комментариев
INSERT INTO comments (body, author_id, post_id, created, updated) VALUES
('Have you tried using EXPLAIN ANALYZE? It can show where bottlenecks are.', 3, 1, '2023-01-15 10:30:00', '2023-01-15 10:30:00'),
('Great question! I''ve been wondering this too.', 5, 2, '2023-01-16 12:00:00', '2023-01-16 12:00:00'),
('Don''t forget about composite indexes for queries that filter on multiple columns.', 1, 3, '2023-01-17 09:45:00', '2023-01-17 09:45:00'),
('NULL handling is tricky in most SQL databases, not just PostgreSQL.', 2, 4, '2023-01-18 15:00:00', '2023-01-18 15:00:00'),
('For transactions, think of a bank transfer - both accounts must update or neither does.', 4, 5, '2023-01-19 17:10:00', '2023-01-19 17:10:00'),
('This answer saved me hours of work, thank you!', 7, 6, '2023-01-15 13:00:00', '2023-01-15 13:00:00');

-- Вставка голосов
INSERT INTO votes (author_id, post_id, created, type) VALUES
(2, 1, '2023-01-15 10:05:00', 'POSITIVE'),
(3, 1, '2023-01-15 10:10:00', 'POSITIVE'),
(4, 2, '2023-01-16 11:35:00', 'POSITIVE'),
(5, 2, '2023-01-16 11:40:00', 'POSITIVE'),
(1, 3, '2023-01-17 09:20:00', 'POSITIVE'),
(2, 3, '2023-01-17 09:25:00', 'POSITIVE'),
(3, 4, '2023-01-18 14:25:00', 'POSITIVE'),
(5, 4, '2023-01-18 14:30:00', 'POSITIVE'),
(1, 5, '2023-01-19 16:50:00', 'POSITIVE'),
(4, 5, '2023-01-19 16:55:00', 'POSITIVE'),
(1, 6, '2023-01-15 11:25:00', 'POSITIVE'),
(3, 6, '2023-01-15 11:30:00', 'POSITIVE'),
(5, 6, '2023-01-15 11:35:00', 'POSITIVE'),
(2, 7, '2023-01-16 13:50:00', 'POSITIVE'),
(4, 7, '2023-01-16 13:55:00', 'POSITIVE'),
(1, 8, '2023-01-17 10:35:00', 'POSITIVE'),
(2, 8, '2023-01-17 10:40:00', 'POSITIVE'),
(5, 9, '2023-01-18 15:45:00', 'POSITIVE'),
(1, 9, '2023-01-18 15:50:00', 'POSITIVE'),
(2, 10, '2023-01-19 17:55:00', 'POSITIVE'),
(3, 10, '2023-01-19 18:00:00', 'POSITIVE'),
(4, 11, '2023-01-15 12:35:00', 'POSITIVE');

-- Вставка тегов
INSERT INTO tags (name, post_id) VALUES
('sql', 1),
('optimization', 1),
('database', 1),
('joins', 2),
('indexing', 3),
('postgresql', 4),
('null', 4),
('transactions', 5),
('acid', 5);

-- Вставка подписок на посты
INSERT INTO posts_subscribes (post_id, user_id) VALUES
(1, 2),
(1, 3),
(1, 5),
(2, 1),
(2, 4),
(3, 2),
(3, 5),
(4, 1),
(4, 3),
(5, 2),
(5, 4);

-- Вставка подписок на теги
INSERT INTO tags_subscribes (tag_id, user_id) VALUES
(1, 1),
(1, 3),
(1, 5),
(2, 2),
(3, 4),
(4, 1),
(4, 2),
(5, 3),
(6, 5),
(7, 1),
(8, 2),
(9, 3);