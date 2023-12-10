package pt.isel.daw.gomoku.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.Nested
import org.jdbi.v3.core.statement.Update
import org.postgresql.util.PGobject
import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.GameCreationModel
import pt.isel.daw.gomoku.domain.games.board.Board
import pt.isel.daw.gomoku.domain.games.board.BoardDim
import pt.isel.daw.gomoku.domain.games.variants.*
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.utils.Id
import pt.isel.daw.gomoku.repository.util.GamesRepository

class JdbiGamesRepository(
    private val handle: Handle
) : GamesRepository {

    override fun getGame(id: Int) =
        handle.createQuery(
            """
            select games.id,
               games.state,
               games.board,
               games.created,
               games.updated,
               games.deadline,
               games.variant,
               users_black.id                  as playerBlack_id,
               users_black.username            as playerBlack_username,
               users_black.email               as playerBlack_email,
               users_black.password_validation as playerBlack_password_validation,
               users_white.id                  as playerWhite_id,
               users_white.email               as playerWhite_email,
               users_white.username            as playerWhite_username,
               users_white.password_validation as playerWhite_password_validation
            from dbo.Games games
                     inner join dbo.Users users_black on games.player_black = users_black.id
                     inner join dbo.Users users_white on games.player_white = users_white.id
            where games.id = :id
        """.trimIndent()
        )
            .bind("id", id)
            .mapTo<GameDbModel>()
            .singleOrNull()
            ?.run {
                toGame()
            }


    override fun updateGame(game: Game) {
        handle.createUpdate(
            """
            update dbo.Games
            set state=:state, board=:board, updated=:updated, deadline=:deadline
            where id=:id
        """
        )
            .bind("id", game.id.value)
            .bind("state", game.state)
            .bindBoard("board", game.board)
            .bind("updated", game.updated.epochSeconds)
            .bind("deadline", game.deadline?.epochSeconds)
            .execute()
    }

    override fun createGame(game: GameCreationModel): Int =
        handle.createUpdate(
            """
                insert into dbo.Games(state, board, created, updated, deadline, player_black, player_white, variant)
                values (:state, :board, :created, :updated, :deadline, :player_black , :player_white, :variant)
            """.trimIndent()
        )
            .bind("state", game.state)
            .bindBoard("board", game.board)
            .bind("created", game.created.epochSeconds)
            .bind("updated", game.updated.epochSeconds)
            .bind("deadline", game.deadline?.epochSeconds)
            .bind("player_black", game.playerBLACK.id.value)
            .bind("player_white", game.playerWHITE.id.value)
            .bind("variant", game.variant.name)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()


    override fun deleteGame(id: Int) {
        handle.createUpdate(
            """
            delete from dbo.Games
            where id=:id
        """
        )
            .bind("id", id)
            .execute()
    }

    override fun getGamesByUser(userId: Int): List<Game> =
        handle.createQuery(
            """
                select games.id,
                       games.state,
                       games.board,
                       games.created,
                       games.updated,
                       games.deadline,
                       games.variant,
                       users_black.id                  as playerBlack_id,
                       users_black.username            as playerBlack_username,
                       users_black.email               as playerBlack_email,
                       users_black.password_validation as playerBlack_password_validation,
                       users_white.id                  as playerWhite_id,
                       users_white.username            as playerWhite_username,
                       users_white.email               as playerWhite_email,
                       users_white.password_validation as playerWhite_password_validation
                from dbo.Games games
                         inner join dbo.Users users_black on games.player_black = users_black.id
                         inner join dbo.Users users_white on games.player_white = users_white.id
                where games.player_black = :userId or games.player_white = :userId
                order by games.created desc
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<GameDbModel>()
            .list()
            .map {
                it.toGame()
            }

    override fun getAll(): List<Game> =
        handle.createQuery(
            """
                select games.id,
                       games.state,
                       games.board,
                       games.created,
                       games.updated,
                       games.deadline,
                       games.variant,
                       users_black.id                  as playerBlack_id,
                       users_black.username            as playerBlack_username,
                       users_black.email               as playerBlack_email,
                       users_black.password_validation as playerBlack_password_validation,
                       users_white.id                  as playerWhite_id,
                       users_white.username            as playerWhite_username,
                       users_white.email               as playerWhite_email,
                       users_white.password_validation as playerWhite_password_validation
                from dbo.Games games
                         inner join dbo.Users users_black on games.player_black = users_black.id
                         inner join dbo.Users users_white on games.player_white = users_white.id
                order by games.created desc
            """.trimIndent()
        )
            .mapTo<GameDbModel>()
            .list()
            .map {
                it.toGame()
            }

    override fun isGameStoredById(id: Int): Boolean =
        handle.createQuery(
            """
                select count(*) from dbo.Games where id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .mapTo<Int>()
            .single() == 1

    override fun getMatchmakingEntry(entryId: Int): MatchmakingEntry? =
        handle.createQuery(
            """
                select m.id, m.user_id, m.game_id, m.variant, m.status, m.created
                from dbo.matchmaking m
                         inner join dbo.Users users on m.user_id = users.id
                where m.id = :entryId
                for update skip locked
            """.trimIndent()
        )
            .bind("entryId", entryId)
            .mapTo<MatchmakingEntryDbModel>()
            .singleOrNull()
            ?.run {
                toMatchmakingEntry()
            }

    override fun getAMatch(userId: Int): MatchmakingEntry? =
        handle.createQuery(
            """
                select m.id, m.user_id, m.game_id, m.variant, m.status, m.created
                from dbo.matchmaking m
                         inner join dbo.Users users on m.user_id = users.id
                where users.id != :userId and m.status = 'PENDING'
                  order by m.created    
                limit 1
                for update skip locked
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<MatchmakingEntryDbModel>()
            .singleOrNull()
            ?.run {
                toMatchmakingEntry()
            }

    override fun updateMatchmakingEntry(id: Int, status: MatchmakingStatus, gameId: Int) =
        handle.createUpdate(
            """
                update dbo.matchmaking
                set status = :status, game_id = :gameId
                where id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .bind("gameId", gameId)
            .bind("status", status)
            .execute()

    override fun storeMatchmakingEntry(userId: Int, variant: String, status: MatchmakingStatus, created: Instant): Int =
        handle.createUpdate(
            """
                insert into dbo.matchmaking(user_id, variant, status, created)
                values (:user_id, :variant, :status, :created);
            """.trimIndent()
        )
            .bind("user_id", userId)
            .bind("variant", variant)
            .bind("status", status)
            .bind("created", created.epochSeconds)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun isUserInMatchmakingQueue(userId: Int): Boolean =
        handle.createQuery(
            """
                select count(*) from dbo.matchmaking where user_id = :userId and status = 'PENDING'
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Int>()
            .single() == 1

    override fun exitMatchmakingQueue(id: Int) =
        handle.createUpdate(
            """
                delete from dbo.matchmaking where id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .execute()

    override fun getVariant(variant: String): Variants? =
        handle.createQuery(
            """
                select v.variant_name from dbo.Variant v where v.variant_name = :variant
            """.trimIndent()
        )
            .bind("variant", variant)
            .mapTo<VariantsDbModel>()
            .singleOrNull()?.run {
                toVariant()
            }

    override fun getAllVariants(): List<GameVariant> =
        handle.createQuery(
            """
                select v.variant_name, v.board_dim, v.opening_rule, v.play_rule, v.points
                from dbo.Variant v
            """.trimIndent()
        )
            .mapTo<GameVariantDbModel>()
            .list()
            .map {
                it.toGameVariant()
            }

    companion object {
        private fun Update.bindBoard(name: String, board: Board) = run {
            bind(
                name,
                PGobject().apply {
                    type = "jsonb"
                    value = serializeBoardToJson(board)
                }
            )
        }

        private fun serializeBoardToJson(board: Board): String = BoardSerializer.serialize(board)

        fun deserializeBoardFromJson(json: String) = BoardSerializer.deserialize(json)
    }
}

// Class that represents the game in the database
class GameDbModel(
    val id: Int,
    val state: Game.State,
    val board: Board,
    val created: Instant,
    val updated: Instant,
    val deadline: Instant?,
    val variant: String,
    @Nested("playerBlack")
    val playerBlack: User,
    @Nested("playerWhite")
    val playerWhite: User
) {
    fun toGame(): Game =
        Game(Id(id), state, board, created, updated, deadline, playerBlack, playerWhite, variant.toVariant())
}

// Class that represents the matchmaking entry in the database
class MatchmakingEntryDbModel(
    val id: Int,
    val user_id: Int,
    val game_id: Int?,
    val variant: String,
    val status: String,
    val created: Instant
) {
    fun toMatchmakingEntry(): MatchmakingEntry =
        MatchmakingEntry(id, user_id, game_id, variant, MatchmakingStatus.valueOf(status), created)
}

// Class that represents the variant in the database
class VariantsDbModel(
    val variant_name: String
) {
    fun toVariant(): Variants = Variants.valueOf(variant_name)
}

// Class that represents the game variant in the database
class GameVariantDbModel(
    val variant_name: String,
    val board_dim: Int,
    val opening_rule: String,
    val play_rule: String,
    val points: Int
) {
    fun toGameVariant(): GameVariant =
        GameVariant(
            variant_name,
            BoardDim.fromInt(board_dim),
            PlayingRule.valueOf(play_rule),
            OpeningRule.valueOf(opening_rule),
            points
        )
}