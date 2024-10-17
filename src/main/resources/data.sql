-- !! FOR DEVELOPMENT PURPOSES ONLY !!
-- Add default users. Password = username
INSERT INTO users (id, username, password, role, created_at, updated_at) VALUES
(1,'admin', '$2a$10$GtdckAs1J7wYR1c5YXVDIuLWA482JLigGSqJQJf4xaazUYchcZ2ua', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2,'user1', '$2a$10$9mjuqhgOuHQq34ENCdmZtOeI9sZ2ktWZERx1G7xFYYajIC6rzYNy.', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3,'user2', '$2a$10$/N.h2vKRRwmsMGe4r1vvX.wiWCAgcgq3aOwPYlnM1IiAChmdT1blS', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add some example data
INSERT INTO lost_items (id, item_name, quantity, place, created_at, updated_at) VALUES
(1, 'computer', 1, 'Cafe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'laptop', 6, 'Town hall', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'printer', 2, 'Bank', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lost_item_claims (id, user_id, lost_item_id, quantity, created_at, updated_at) VALUES
(1, 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);