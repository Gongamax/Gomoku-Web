-- Inserting default values for the user table
INSERT INTO dbo.users
(username, password_validation, email)
VALUES
    ('alice', '$2a$10$rfB5mueMNJFZlFA1RTZbNOUy48WJn27gK8JInlEIwtjxAB2zaF81q', 'alicepereira@gmail.com'),
    ('bob', '$2a$10$HiAG1gbNntnVCVJlXU.k7OMnkVaO22hIIQLrQBGxesoosntZ4TWW.', 'boboconstrutor@hotmail.com'),
    ('charlie', '$2a$10$g46B9qqo3spqc4sMoGDFwuf/cwrjR99od.EDL9C6WD1xfE./.6YSu', 'charliebrown@yahoo.com');

-- Inserting default values for the token table
INSERT INTO dbo.tokens
(token_validation, user_id, created_at, last_used_at)
VALUES
    ('token1', 1, 1634260800, 1634260810),
    ('token2', 2, 1634260820, 1634260830),
    ('token3', 3, 1634260840, 1634260850);

-- Inserting default values for the variant table
INSERT INTO dbo.variant
(variant_name, board_dim, play_rule, opening_rule, points)
VALUES
    ('STANDARD', 15, 'STANDARD', 'STANDARD', 110),
    ('SWAP', 15, 'STANDARD', 'SWAP', 140),
    ('RENJU', 15, 'THREE_AND_THREE', 'STANDARD', 150),
    ('CARO', 15, 'STANDARD', 'STANDARD', 120),
    ('PENTE', 19, 'STANDARD', 'STANDARD', 130),
    ('OMOK', 19, 'THREE_AND_THREE', 'STANDARD',170),
    ('NINUKI_RENJU', 15, 'THREE_AND_THREE', 'STANDARD',160);

-- Inserting default values for the games table
INSERT INTO dbo.games
(id, state, board, created, updated, deadline, player_black, player_white, variant)
VALUES
    ('99999', 'NEXT_PLAYER_WHITE',
     '{
       "kind": "Run:STANDARD",
       "piece": "BLACK",
       "moves": {
         "1A": "BLACK",
         "2B": "WHITE",
         "3C": "BLACK"
       }
     }', 1634260800, 1634260820, 1634260840, 1, 2, 'STANDARD');