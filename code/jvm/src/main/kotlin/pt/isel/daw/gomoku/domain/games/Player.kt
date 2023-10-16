package pt.isel.daw.gomoku.domain.games

import pt.isel.daw.gomoku.domain.users.User

/**
 * Represents a player of the game.
 * @property userId  the id of the user that is playing.
 * @property piece the piece that the player is using.
 */

data class Player(val userId: Int, val piece: Piece)
enum class Piece { BLACK, WHITE }
