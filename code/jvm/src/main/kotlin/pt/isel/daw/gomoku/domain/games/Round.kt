package pt.isel.daw.gomoku.domain.games

import pt.isel.daw.gomoku.domain.games.board.Cell

/**
 * Represents a round of the game.
 * @property cell the cell where the player played.
 * @property player the player that played.
 */

class Round (
    val cell: Cell,
    val player: Player,
    val wantsToSwap: Boolean = false,
)