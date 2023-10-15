package pt.isel.daw.gomoku.domain.games

/**
 *  Represents the result of a round.
 *  It can be:
 *  @property NotYourTurn when the player is not the one that should play
 *  @property GameAlreadyEnded when the game has already ended
 *  @property NotAPlayer when the user is not a player of the game
 *  @property PositionNotAvailable when the position is not available
 *  @property TooLate when the player took too long to play
 *  @property YouWon when the player won the game
 *  @property OthersTurn when it's the other player's turn
 *  @property Draw when the game ended in a draw
 * */

sealed class RoundResult {
    object NotYourTurn : RoundResult()
    object GameAlreadyEnded : RoundResult()
    object NotAPlayer : RoundResult()
    object PositionNotAvailable : RoundResult()
    data class TooLate(val game: Game) : RoundResult()
    data class YouWon(val game: Game) : RoundResult()
    data class OthersTurn(val game: Game) : RoundResult()
    data class Draw(val game: Game) : RoundResult()
}
