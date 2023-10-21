package pt.isel.daw.gomoku.domain.games

import pt.isel.daw.gomoku.domain.utils.Id

/**
 * This class is responsible for the player domain logic.
 * @property isTurn a function that returns true if it is the player's turn.
 * @property otherWon the state of the game if the other player won.
 * @property iWon the state of the game if the player won.
 * @property nextPlayer the state of the game if it is the next player's turn.
 * @property boardState the piece that the player is using.
 * */

class PlayerDomain(
    val isTurn: (game: Game, userId: Id) -> Boolean,
    val otherWon: Game.State,
    val iWon: Game.State,
    val nextPlayer: Game.State,
    val boardState: Piece,
)

