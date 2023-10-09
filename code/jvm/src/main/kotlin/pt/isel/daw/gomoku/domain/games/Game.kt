package pt.isel.daw.gomoku.domain.games

import kotlinx.datetime.Instant
import java.util.UUID

// Just for testing, not definitive yet
data class Game(
    val id : UUID,
    val board : Board,
    val created: Instant,
    val updated: Instant,
    val deadline: Instant?,
    val localPlayer : Player,
    val remotePlayer : Player,
)


