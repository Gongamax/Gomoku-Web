package pt.isel.daw.gomoku.domain.games

import kotlinx.datetime.Instant
import pt.isel.daw.gomoku.domain.games.board.Board
import pt.isel.daw.gomoku.domain.games.variants.Variants
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.utils.Id

/**
 * Represents a game.
 * @property id the id of the game.
 * @property state the state of the game.
 * @property board the board of the game.
 * @property created the date and time when the game was created.
 * @property updated the date and time when the game was last updated.
 * @property deadline the date and time when the game will be deleted.
 * @property playerBLACK the player that is using the black piece.
 * @property playerWHITE the player that is using the white piece.
 */
data class Game(
    val id : Id,
    val state: State,
    val board : Board,
    val created: Instant,
    val updated: Instant,
    val deadline: Instant?,
    val playerBLACK : User,
    val playerWHITE : User,
    val variant: Variants,
){
    enum class State {
        SWAPPING_PIECES,
        NEXT_PLAYER_BLACK,
        NEXT_PLAYER_WHITE,
        PLAYER_BLACK_WON,
        PLAYER_WHITE_WON,
        DRAW;
        val isEnded: Boolean
            get() = this == PLAYER_BLACK_WON || this == PLAYER_WHITE_WON || this == DRAW

        override fun toString(): String {
            return when (this) {
                SWAPPING_PIECES -> "SWAPPING_PIECES"
                NEXT_PLAYER_BLACK -> "NEXT_PLAYER_BLACK"
                NEXT_PLAYER_WHITE -> "NEXT_PLAYER_WHITE"
                PLAYER_BLACK_WON -> "PLAYER_BLACK_WON"
                PLAYER_WHITE_WON -> "PLAYER_WHITE_WON"
                DRAW -> "DRAW"
            }
        }
    }
}
