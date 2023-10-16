package pt.isel.daw.gomoku.domain.games

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.daw.gomoku.domain.users.User
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration

/**
 * This class is responsible for the game domain logic.
 * It is responsible for creating a game and applying rounds.
 * @property clock The clock used to get the current time.
 * @property timeout The timeout for each round.
 *
 * This implementation is based on the one provided by the teacher
 * in gitHub repository: https://github.com/isel-leic-daw/s2223i-51d-51n-public
 * */

@Component
class GameDomain(
    private val clock: Clock,
    private val timeout: Duration
) {
    fun createGame(
        playerBLACK: User,
        playerWHITE: User
    ): Game {
        val now = clock.now()
        return Game(
            id = UUID.randomUUID(),
            state = Game.State.NEXT_PLAYER_BLACK,
            board = Board.createBoard(player = Player(playerBLACK.id, Piece.BLACK)),
            created = now,
            updated = now,
            deadline = now.plus(timeout),
            playerBLACK = playerBLACK,
            playerWHITE = playerWHITE
        )
    }

    fun playRound(
        game: Game,
        round: Round,
    ): RoundResult {
        if (round.player.userId != game.playerWHITE.id && round.player.userId != game.playerBLACK.id) {
            return RoundResult.NotAPlayer
        }
        val now = clock.now()
        return when (game.state) {
            Game.State.PLAYER_BLACK_WON -> RoundResult.GameAlreadyEnded
            Game.State.PLAYER_WHITE_WON -> RoundResult.GameAlreadyEnded
            Game.State.DRAW -> RoundResult.GameAlreadyEnded
            Game.State.NEXT_PLAYER_BLACK -> playRound(game, round, now, PLAYER_BLACK_LOGIC)
            Game.State.NEXT_PLAYER_WHITE -> playRound(game, round, now, PLAYER_WHITE_LOGIC)
        }
    }

    private fun playRound(
        game: Game,
        round: Round,
        now: Instant,
        aux: PlayerDomain,
    ): RoundResult = if (!aux.isTurn(game, round.player.userId)) {
        RoundResult.NotYourTurn
    } else {
        if (game.deadline != null && now > game.deadline) {
            val newGame = game.copy(state = aux.otherWon, deadline = null)
            RoundResult.TooLate(newGame)
        } else {
            if (game.board.canPlayOn(round.cell)) {
                when (val newBoard = game.board.playRound(round.cell, nextPlayer(game, round.player))) {
                    is BoardWin -> {
                        val newGame =
                            game.copy(board = newBoard, state = aux.iWon, deadline = null)
                        RoundResult.YouWon(newGame)
                    }

                    is BoardDraw -> {
                        val newGame = game.copy(
                            board = newBoard,
                            state = Game.State.DRAW,
                            deadline = null,
                        )
                        RoundResult.Draw(newGame)
                    }

                    is BoardRun -> {
                        val newGame = game.copy(
                            board = newBoard,
                            state = aux.nextPlayer,
                            deadline = now + timeout,
                        )
                        RoundResult.OthersTurn(newGame)
                    }
                }
            } else {
                RoundResult.PositionNotAvailable
            }
        }
    }

    private fun nextPlayer(game: Game, player: Player): Player =
        if (player.userId == game.playerBLACK.id) Player(game.playerWHITE.id, Piece.WHITE)
        else Player(game.playerBLACK.id, Piece.BLACK)

    companion object {
        private val PLAYER_BLACK_LOGIC = PlayerDomain(
            isTurn = { game, user -> game.isPlayerBLACK(user) },
            otherWon = Game.State.PLAYER_WHITE_WON,
            iWon = Game.State.PLAYER_BLACK_WON,
            nextPlayer = Game.State.NEXT_PLAYER_WHITE,
            boardState = Piece.BLACK,
        )
        private val PLAYER_WHITE_LOGIC = PlayerDomain(
            isTurn = { game, user -> game.isPlayerWHITE(user) },
            otherWon = Game.State.PLAYER_BLACK_WON,
            iWon = Game.State.PLAYER_WHITE_WON,
            nextPlayer = Game.State.NEXT_PLAYER_BLACK,
            boardState = Piece.WHITE,
        )
    }
}

private fun Game.isPlayerBLACK(player: Int) = this.playerBLACK.id == player

private fun Game.isPlayerWHITE(player: Int) = this.playerBLACK.id == player