create schema dbo;

create table dbo.Users
(
    id                  int generated always as identity primary key,
    username            VARCHAR(64) unique not null,
    password_validation VARCHAR(256)       not null
);

create table dbo.Tokens
(
    token_validation VARCHAR(256) primary key,
    user_id          int references dbo.Users (id),
    created_at       bigint not null,
    last_used_at     bigint not null
);

create table dbo.Games
(
    id           UUID        not null,
    state        VARCHAR(64) not null,
    board        jsonb       not null,
    created      int         not null,
    updated      int         not null,
    deadline     int,
    player_black int references dbo.Users (id),
    player_white int references dbo.Users (id),

    UNIQUE (id)
);

create table dbo.Game_State
(
    state      VARCHAR(64) not null,
    game_id    UUID references dbo.Games (id),
    turn       int references dbo.Users (id),
    winner     int references dbo.Users (id),
    created_at int         not null,
    ended_at   int         not null,

    UNIQUE (game_id)
);

create table dbo.Players
(
    id      SERIAL PRIMARY KEY,
    game    UUID NOT NULL REFERENCES dbo.Games (id),
    user_id INT NOT NULL REFERENCES dbo.Users (id),

    UNIQUE (game, user_id)
);

create table dbo.Pieces
(
    id     SERIAL PRIMARY KEY,
    player INT        NOT NULL REFERENCES dbo.Players (id),
    col    CHAR       NOT NULL,
    row    INT        NOT NULL,

    UNIQUE (player, col, row),

    CONSTRAINT col_is_valid CHECK ( col >= 'A' AND col <= 'S' ),
    CONSTRAINT row_is_valid CHECK ( row >= 1 AND row <= 19 )
);

-- Missing table for Game_Config to allow different game modes