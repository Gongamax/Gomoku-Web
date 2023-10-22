package pt.isel.daw.gomoku.repository.jdbi

import kotlinx.datetime.Instant

data class MatchmakingEntry (
    val id: Int,
    val playerId: Int,
    val status : MatchmakingStatus,
    val created : Instant
)

enum class MatchmakingStatus {
    PENDING,
    MATCHED
}