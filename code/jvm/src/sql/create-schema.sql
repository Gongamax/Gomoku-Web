-- Table and Trigger Dropping

drop trigger if exists check_points on dbo.statistics cascade;
drop trigger if exists add_user_to_statistics on dbo.users cascade;
drop trigger if exists update_points_and_rank on dbo.games cascade;
drop function if exists dbo.check_points() cascade;
drop function if exists dbo.add_user_to_statistics() cascade;
drop function if exists dbo.update_rank() cascade;
drop function if exists dbo.update_points() cascade;
drop function if exists dbo.update_point_and_rank() cascade;
drop table if exists dbo.variant cascade;
drop table if exists dbo.matchmaking cascade;
drop table if exists dbo.tokens cascade;
drop table if exists dbo.statistics cascade;
drop table if exists dbo.users cascade;
drop table if exists dbo.game_config cascade;
drop table if exists dbo.games cascade;
drop schema if exists dbo cascade;

-- Schema Creation

create schema dbo;

-- Table Creation

create table dbo.Users
(
    id                  int generated always as identity primary key,
    username            VARCHAR(64) unique,
    email               VARCHAR(64) unique,
    password_validation VARCHAR(256) not null
);

create table dbo.Tokens
(
    token_validation VARCHAR(256) primary key,
    user_id          int references dbo.Users (id),
    created_at       bigint not null,
    last_used_at     bigint not null
);

create table dbo.Variant
(
    variant_name VARCHAR(64) primary key,
    board_dim    int         not null,
    play_rule    VARCHAR(64) not null,
    opening_rule VARCHAR(64) not null,
    points       int         not null
);

create table dbo.Games
(
    id           serial primary key,
    state        VARCHAR(64) not null,
    board        jsonb       not null,
    created      int         not null,
    updated      int         not null,
    deadline     int,
    player_black int references dbo.Users (id),
    player_white int references dbo.Users (id),
    variant      VARCHAR(64) references dbo.Variant (variant_name)
);

create table dbo.Statistics
(
    user_id      int references dbo.Users (id),
    wins         int not null,
    losses       int not null,
    draws        int not null,
    rank         int not null,
    games_played int not null,
    points       int not null
);

-- Auxiliary table for matchmaking purposes, it works as a synchronizer
create table dbo.Matchmaking
(
    id      serial primary key,
    user_id int references dbo.Users (id),
    game_id int references dbo.Games (id), -- null if not in a game, otherwise the game id
    variant VARCHAR(64) references dbo.Variant (variant_name),
    status  VARCHAR(64) not null,
    created int         not null
);

-- Triggers

-- Trigger to add a new user to the statistics table

create or replace function dbo.add_user_to_statistics()
    returns trigger as
$$
begin
    insert into dbo.Statistics (user_id, wins, losses, draws, rank, games_played, points)
    values (new.id, 0, 0, 0, 0, 0, 0);
    return new;
end;
$$ language plpgsql;

create trigger add_user_to_statistics
    after insert
    on dbo.Users
    for each row
execute procedure dbo.add_user_to_statistics();

-- Trigger to ensure points dont go below 0

create or replace function dbo.check_points(points int)
    returns int as
$$
begin
    if points < 0 then
        points = 0;
    end if;
    return points;
end;
$$ language plpgsql;

-- Trigger for updating the rank of a user
-- Rank is calculated by the formula:
-- Who has more points is ranked higher
-- The rank is updated after every game
create or replace function dbo.update_rank()
    returns void as
$$
begin
    with ranked_users
             as (select user_id,
                        row_number() over (order by points desc) as rank
                 from dbo.Statistics)
    update dbo.Statistics as s
    set rank = ru.rank
    from ranked_users as ru
    where s.user_id = ru.user_id;
end;
$$ language plpgsql;

-- Trigger for updating the points of a user
-- Points are calculated by the formula:
-- If the player wins, he gets the points of the variant
-- If the player loses, he loses the points of the variant
-- If the player draws, stays the same
create or replace function dbo.update_points(new dbo.Games)
    returns void as
$$
begin
    if new.state = 'PLAYER_BLACK_WON' then
        update dbo.Statistics
        set points = points + (select points from dbo.Variant where variant_name = new.variant)
        where user_id = new.player_black;
        update dbo.Statistics
        set points = dbo.check_points (points - (select points from dbo.Variant where variant_name = new.variant))
        where user_id = new.player_white;
    elsif new.state = 'PLAYER_WHITE_WON' then
        update dbo.Statistics
        set points = points + (select points from dbo.Variant where variant_name = new.variant)
        where user_id = new.player_white;
        update dbo.Statistics
        set points = dbo.check_points (points - (select points from dbo.Variant where variant_name = new.variant))
        where user_id = new.player_black;
    end if;
end;
$$ language plpgsql;

create or replace function dbo.update_point_and_rank()
    returns trigger as
$$
begin
    perform dbo.update_points(new);
    perform dbo.update_rank();
    return new;
end;
$$ language plpgsql;

-- Trigger for updating the points and rank of a user
create trigger update_points_and_rank
    after update
    on dbo.Games
    for each row
    when ( new.state = 'PLAYER_BLACK_WON' or new.state = 'PLAYER_WHITE_WON' )
execute procedure dbo.update_point_and_rank();