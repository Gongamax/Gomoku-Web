package pt.isel.daw.gomoku.domain.games

import kotlinx.datetime.Instant
import pt.isel.daw.gomoku.domain.games.board.Board
import pt.isel.daw.gomoku.domain.games.variants.Variants
import pt.isel.daw.gomoku.domain.users.User

data class GameCreationModel(
    val state: Game.State,
    val board : Board,
    val created: Instant,
    val updated: Instant,
    val deadline: Instant?,
    val playerBLACK : User,
    val playerWHITE : User,
    val variant: Variants,
)
