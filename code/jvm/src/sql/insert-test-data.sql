INSERT INTO dbo.Users (username, password_validation)
VALUES ('alice', 'hash1'),
       ('bob', 'hash2'),
       ('charlie', 'hash3');

INSERT INTO dbo.Tokens (token_validation, user_id, created_at, last_used_at)
VALUES ('token1', 1, 1634260800, 1634260810),
       ('token2', 2, 1634260820, 1634260830),
       ('token3', 3, 1634260840, 1634260850);

INSERT INTO dbo.Games (id, state, board, created, updated, deadline, player_black, player_white)
VALUES ('4c7159cc-a146-46c4-8158-920b4e6d7ddb', 'NEXT_PLAYER_WHITE',
        '{
          "kind": "Run:31",
          "moves": {
            "A1": "BLACK",
            "B2": "WHITE",
            "C3": "BLACK"
          }
        }', 1634260800, 1634260820, 1634260840, 1, 2);


INSERT INTO dbo.Game_Config (game_id, board_size, variant, opening_rule)
VALUES ('4c7159cc-a146-46c4-8158-920b4e6d7ddb', 19, '{
  "variant_data": "your_variant_json_data"
}', '{
  "opening_rule_data": "your_opening_rule_json_data"
}');
