INSERT INTO dbo.Users (username, password_validation, email)
VALUES ('alice', 'Hash1', 'alicepereira@gmail.com'),
       ('bob', 'Hash2', 'boboconstrutor@hotmail.com'),
       ('charlie', 'Hash3', 'charliebrown@yahoo.com');

INSERT INTO dbo.Tokens (token_validation, user_id, created_at, last_used_at)
VALUES ('token1', 1, 1634260800, 1634260810),
       ('token2', 2, 1634260820, 1634260830),
       ('token3', 3, 1634260840, 1634260850);

INSERT INTO dbo.Games (id, state, board, created, updated, deadline, player_black, player_white)
VALUES ('99999', 'NEXT_PLAYER_WHITE',
        '{
          "kind": "Run:STANDARD",
          "piece": "BLACK",
          "moves": {
            "1A": "BLACK",
            "2B": "WHITE",
            "3C": "BLACK"
          }
        }', 1634260800, 1634260820, 1634260840, 1, 2);


INSERT INTO dbo.Game_Config (game_id, timeout)
VALUES ('99999', 10);