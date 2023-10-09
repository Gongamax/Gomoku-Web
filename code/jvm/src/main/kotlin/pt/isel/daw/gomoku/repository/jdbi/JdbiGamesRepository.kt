package pt.isel.daw.gomoku.repository.jdbi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.Nested
import org.jdbi.v3.core.statement.Update
import org.postgresql.util.PGobject
import pt.isel.daw.gomoku.domain.games.Board
import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.Player
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.repository.GamesRepository
import java.util.*

class JdbiGamesRepository(
    private val handle: Handle
) : GamesRepository {

    //TODO: check if this is correct and complete query
    override fun getGame(gameId: UUID) =
        handle.createQuery("select id, board, player_black, player_white from dbo.games where id = :id")
            .bind("id", gameId)
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
            """.trimIndent()
        )
            .bind("id", game.id)
            .bind("state", "TODO: state")
            .bindBoard("board", game.board)
            .bind("created", game.created.epochSeconds)
            .bind("updated", game.updated.epochSeconds)
            .bind("deadline", game.deadline?.epochSeconds)
            .bind("player_black", game.localPlayer)
            .bind("player_white", game.remotePlayer)
            .execute()
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

class GameDbModel(
    val id: UUID,
    val board: Board,
    val created: Instant,
    val updated: Instant,
    val deadline: Instant?,
    @Nested("playerX")
    val playerBlack: Player, //User
    @Nested("playerO")
    val playerWhite: Player, //User
) {
    fun toGame() = Game(id, board, created, updated, deadline, playerBlack, playerWhite)
}