package pt.isel.daw.gomoku.repository.jdbi

import kotlinx.datetime.Instant

data class MatchmakingEntry (
    val id: Int,
    val userId: Int,
    val gameId: Int?,
    val variant: String,
    val status : MatchmakingStatus,
    val created : Instant
)

enum class MatchmakingStatus {
    PENDING,
    MATCHED
}