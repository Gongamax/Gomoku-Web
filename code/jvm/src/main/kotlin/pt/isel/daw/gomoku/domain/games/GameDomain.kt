package pt.isel.daw.gomoku.domain.games

import pt.isel.daw.gomoku.domain.users.User
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.*

/**
 * This class is responsible for the game domain logic.
 * It is responsible for creating a game and applying rounds.
 * @property clock The clock used to get the current time.
 * @property timeout The timeout for each round.
 *
 * This implementation is based on the one provided by the teacher
 * in gitHub repository: https://github.com/isel-leic-daw/s2223i-51d-51n-public
 * */

class GameDomain(
    private val clock: Clock,
    private val timeout: Duration
) {
    fun createGame(
        playerBLACK: User,
        playerWHITE: User
    ): Game {
        val now = clock.instant()
        return Game(
            id = UUID.randomUUID(),
            state = Game.State.NEXT_PLAYER_BLACK,
            board = Board.createBoard(player = Player(playerBLACK, Piece.BLACK)),
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
        if (round.player.user.id != game.playerWHITE.id && round.player.user.id != game.playerBLACK.id) {
            return RoundResult.NotAPlayer
        }
        val now = clock.instant()
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
    ): RoundResult = if (!aux.isTurn(game, round.player.user)) {
        RoundResult.NotYourTurn
    } else {
        if (now > game.deadline) {
            val newGame = game.copy(state = aux.otherWon, deadline = null)
            RoundResult.TooLate(newGame)
        } else {
            if (game.board.canPlayOn(round.cell)) {
                val newBoard = game.board.playRound(round.cell)
                if (newBoard is BoardWin) {
                    val newGame =
                        game.copy(board = newBoard, state = aux.iWon, deadline = null)
                    RoundResult.YouWon(newGame)
                } else {
                    if (newBoard is BoardDraw) {
                        val newGame = game.copy(
                            board = newBoard,
                            state = Game.State.DRAW,
                            deadline = null,
                        )
                        RoundResult.Draw(newGame)
                    } else {
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

private fun Game.isPlayerBLACK(player: User) = this.playerBLACK.id == player.id

private fun Game.isPlayerWHITE(player: User) = this.playerBLACK.id == player.id