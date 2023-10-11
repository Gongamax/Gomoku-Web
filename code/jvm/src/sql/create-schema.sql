create schema dbo;

-- Table Dropping

drop table if exists dbo.tokens cascade;
drop table if exists dbo.users cascade;
drop table if exists dbo.game_config cascade;
drop table if exists dbo.games cascade;

-- Table Creation

create table dbo.Users
(
    id                  int generated always as identity primary key,
    username            VARCHAR(64) unique,
    password_validation VARCHAR(256) not null
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
    id           UUID primary key,
    state        VARCHAR(64) not null,
    board        jsonb       not null,
    created      int         not null,
    updated      int         not null,
    deadline     int,
    player_black int references dbo.Users (id),
    player_white int references dbo.Users (id)
);

-- Objetivo adicional para o Game_Config:
-- Inserir na base de dados dinamicamente, dar possibilidade aos users de inserir
-- as proprias variantes e regras de abertura
create table dbo.Game_Config
(
    game_id      UUID references dbo.Games (id),
    board_size   int   not null,
    variant      jsonb not null,
    opening_rule jsonb not null
);

create table dbo.Statistics
(
    user_id      int references dbo.Users (id),
    wins         int not null,
    losses       int not null,
    rank         int not null,
    games_played int not null
);

-- Trigger for incrementing the games played by a user

create or replace function increment_games_played()
    returns trigger as
$$
begin
    update dbo.Statistics
    set games_played = games_played + 1
    where user_id = new.player_black
       or user_id = new.player_white;
    return new;
end;
$$ language plpgsql;

create trigger increment_games_played
    after insert
    on dbo.Games
    for each row
execute procedure increment_games_played();

-- Trigger for incrementing the wins and losses of a user

create or replace function increment_wins_losses()
    returns trigger as
$$
begin
    if new.state = 'BLACK_WON' then
        update dbo.Statistics
        set wins = wins + 1
        where user_id = new.player_black;
        update dbo.Statistics
        set losses = losses + 1
        where user_id = new.player_white;
    elsif new.state = 'WHITE_WON' then
        update dbo.Statistics
        set wins = wins + 1
        where user_id = new.player_white;
        update dbo.Statistics
        set losses = losses + 1
        where user_id = new.player_black;
    end if;
    return new;
end;
$$ language plpgsql;

create trigger increment_wins_losses
    after update
    on dbo.Games
    for each row
execute procedure increment_wins_losses();

-- Trigger for updating the rank of a user
-- Rank is calculated by the formula:
-- Rank = 1000 + 100 * (Wins - Losses) + 10 * (Games Played)
-- The rank is updated after every game
create or replace function update_rank()
    returns trigger as
$$
begin
    with ranked_users
             as (select user_id,
                        row_number() over (order by 1000 + 100 * (Wins - Losses) + 10 * (games_played) desc) as rank
                 from dbo.Statistics)
    update dbo.Statistics as s
    set rank = ru.rank
    from ranked_users as ru
    where s.user_id = ru.user_id;

    return new;
end;
$$ language plpgsql;

create trigger update_rank
    after update
    on dbo.Games
    for each row
    when ( new.state = 'Game is Over' )
execute procedure update_rank();


