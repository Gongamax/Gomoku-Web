package pt.isel.daw.gomoku.domain.users

data class UserStatistics(
    val user: User,
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val rank: Int,
    val points: Int
)