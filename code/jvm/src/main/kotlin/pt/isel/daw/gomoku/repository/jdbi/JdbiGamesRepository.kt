package pt.isel.daw.gomoku.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.Nested
import org.jdbi.v3.core.statement.Update
import org.postgresql.util.PGobject
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.repository.GamesRepository
import java.util.*

class JdbiGamesRepository(
    private val handle: Handle
) : GamesRepository {

    override fun getGame(id: UUID) =
        handle.createQuery(
            """
            select games.id,
               games.state,
               games.board,
               games.created,
               games.updated,
               games.deadline,
               users_black.id                  as playerBlakc_id,
               users_black.username            as playerBlack_username,
               users_black.password_validation as playerBlack_password_validation,
               users_white.id                  as playerWhite_id,
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
            .bind("id", game.id)
            .bindBoard("board", game.board)
            .bind("deadline", game.deadline?.epochSeconds)
            .execute()

    }

    override fun createGame(game: Game) {
        handle.createUpdate(
            """
                insert into dbo.Games(id, state, board, created, updated, deadline, player_black, player_white)
                values (:id, :state, :board, :created, :updated, :deadline, :player_black , :player_white)
            """
        )
            .bind("id", game.id)
            .bind("state", getGameState(game.board))
            .bindBoard("board", game.board)
            .bind("created", game.created.epochSeconds)
            .bind("updated", game.updated.epochSeconds)
            .bind("deadline", game.deadline?.epochSeconds)
            .bind("player_black", game.playerBLACK)
            .bind("player_white", game.playerWHITE)
            .execute()
    }

    override fun deleteGame(id: UUID) {
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
                       users_black.id                  as playerBlakc_id,
                       users_black.username            as playerBlack_username,
                       users_black.password_validation as playerBlack_password_validation,
                       users_white.id                  as playerWhite_id,
                       users_white.username            as playerWhite_username,
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
                       users_black.id                  as playerBlakc_id,
                       users_black.username            as playerBlack_username,
                       users_black.password_validation as playerBlack_password_validation,
                       users_white.id                  as playerWhite_id,
                       users_white.username            as playerWhite_username,
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

        private fun getGameState(board: Board) = when (board) {
            is BoardRun -> "Game Running"
            is BoardDraw -> "Game Draw"
            is BoardWin -> "Game is Over"
        }

        private fun serializeBoardToJson(board: Board): String = BoardSerializer.serialize(board)

        fun deserializeBoardFromJson(json: String) = BoardSerializer.deserialize(json)
    }
}

// Class that represents the game in the database
class GameDbModel(
    val id: UUID,
    val state: Game.State,
    val board: Board,
    val created: Instant,
    val updated: Instant,
    val deadline: Instant?,
    @Nested("playerBlack")
    val playerBlack: User,
    @Nested("playerWhite")
    val playerWhite: User
) {
    fun toGame() = Game(id, state, board, created, updated, deadline, playerBlack, playerWhite)
}