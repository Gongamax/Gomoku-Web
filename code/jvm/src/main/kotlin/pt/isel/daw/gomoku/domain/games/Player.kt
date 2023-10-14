package pt.isel.daw.gomoku.domain.games

import pt.isel.daw.gomoku.domain.users.User

/**
 * Represents a player of the game.
 * @property user the user that is playing.
 * @property piece the piece that the player is using.
 */

data class Player(val user: User, val piece: Piece){
    fun other() =
        if (this.piece == Piece.BLACK) Player(user, Piece.WHITE) else Player(user, Piece.BLACK)
}
enum class Piece { BLACK, WHITE }
