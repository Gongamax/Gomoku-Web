package pt.isel.daw.gomoku.domain.games

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import pt.isel.daw.gomoku.domain.games.board.*
import pt.isel.daw.gomoku.domain.games.variants.OpeningRule
import pt.isel.daw.gomoku.domain.games.variants.Variants
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.utils.Id

/**
 * This class is responsible for the game domain logic.
 * It is responsible for creating a game and applying rounds.
 * @property clock The clock used to get the current time.
 * @property config The configuration of the game.
 *
 * This implementation is based on the one provided by the teacher
 * in gitHub repository: https://github.com/isel-leic-daw/s2223i-51d-51n-public
 * */

@Component
class GameDomain(
    private val clock: Clock,
    private val config: GamesDomainConfig
) {
    fun createGameModel(
        playerBlack: User,
        playerWhite: User,
        variant: Variants,
    ): GameCreationModel {
        val now = clock.now()
        return GameCreationModel(
            state = Game.State.NEXT_PLAYER_BLACK,
            board = Board.createBoard(piece = Piece.BLACK),
            created = now,
            updated = now,
            deadline = now + config.timeout,
            playerBLACK = playerBlack,
            playerWHITE = playerWhite,
            variant = variant
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
            Game.State.SWAPPING_PIECES -> swapPieces(game, round, now)
            Game.State.PLAYER_BLACK_WON -> RoundResult.GameAlreadyEnded
            Game.State.PLAYER_WHITE_WON -> RoundResult.GameAlreadyEnded
            Game.State.DRAW -> RoundResult.GameAlreadyEnded
            Game.State.NEXT_PLAYER_BLACK -> playRound(game, round, now, PLAYER_BLACK_LOGIC)
            Game.State.NEXT_PLAYER_WHITE -> playRound(game, round, now, PLAYER_WHITE_LOGIC)
        }
    }

    private fun swapPieces(game: Game, round: Round, now: Instant): RoundResult {
        if (round.player.userId != game.playerWHITE.id) {
            return RoundResult.NotYourTurn
        }
        if (game.deadline != null && now > game.deadline) {
            val newGame = game.copy(state = PLAYER_WHITE_LOGIC.otherWon, deadline = null)
            return RoundResult.TooLate(newGame)
        }
        val newGame = if(round.wantsToSwap)
            game.copy(
            state = PLAYER_WHITE_LOGIC.nextPlayer,
            deadline = now + config.timeout,
            playerBLACK = game.playerWHITE,
            playerWHITE = game.playerBLACK,
        ) else game.copy(
            state = PLAYER_WHITE_LOGIC.nextPlayer,
            deadline = now + config.timeout,
        )
        return RoundResult.OthersTurn(newGame)
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
            if (game.board.canPlayOn(round.cell, game.variant)) {
                when (val newBoard = game.board.playRound(round.cell, nextPiece(game, round.player), game.variant)) {
                    is BoardOpen -> {
                        val newGame = when(game.variant.openingRule) {
                            OpeningRule.STANDARD -> game.copy(
                                board = newBoard,
                                state = aux.nextPlayer,
                                deadline = now + config.timeout,
                            )
                            OpeningRule.SWAP -> game.copy(
                                board = newBoard,
                                state = Game.State.SWAPPING_PIECES,
                                deadline = now + config.timeout,
                            )
                        }
                        RoundResult.OthersTurn(newGame)
                    }
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
                            deadline = now + config.timeout,
                        )
                        RoundResult.OthersTurn(newGame)
                    }
                }
            } else {
                RoundResult.PositionNotAvailable
            }
        }
    }

    private fun nextPiece(game: Game, player: Player): Piece =
        if (player.userId == game.playerBLACK.id) Piece.WHITE
        else Piece.BLACK

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

private fun Game.isPlayerBLACK(player: Id) = run {
    this.playerBLACK.id == player
}

private fun Game.isPlayerWHITE(player: Id) = run {
    this.playerWHITE.id == player
}