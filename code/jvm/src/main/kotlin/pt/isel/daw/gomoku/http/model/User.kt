package pt.isel.daw.gomoku.http.model

import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.utils.RankingEntry

data class UserCreateInputModel(
    val username: String,
    val email: String,
    val password: String
)

data class UserCreateTokenInputModel(
    val email: String,
    val username: String,
    val password: String
)

class UserGetByIdOutputModel(
    val id: Int,
    val username: String,
    val email: String
)

class StatsGetByIdOutputModel(
    val id: Int,
    val username: String,
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val rank: Int,
    val points: Int
)

class UserHomeOutputModel(
    val id: Int,
    val username: String
)

data class UserTokenCreateOutputModel(
    val token: String
)
data class RankingInfoOutputModel(
    val rankingTable: List<RankingEntry>
)

data class UserTokenRemoveOutputModel(
    val message: String
)