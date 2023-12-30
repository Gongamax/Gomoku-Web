package pt.isel.daw.gomoku.repository.util

import kotlinx.datetime.Instant
import pt.isel.daw.gomoku.domain.games.EndGameResult
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.utils.Token
import pt.isel.daw.gomoku.domain.utils.TokenValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.users.UserStatistics


interface UsersRepository {

    fun storeUser(
        username: String,
        email: Email,
        passwordValidation: PasswordValidationInfo
    ): Int

    fun getUserByUsername(username: String): User?

    fun getUserById(id: Int): User?

    fun updateUser(user: User): Int

    fun updateUserStats(id : Int, opponent : Int, result : EndGameResult) : Int

    fun getUserStatsById(id: Int): UserStatistics?

    fun getUserStatsByUsername(username: String): UserStatistics?

    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun isUserStoredByUsername(username: String): Boolean

    fun createToken(token: Token, maxTokens: Int)

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int

    fun getAllUsers(): List<User>

    fun getAllStats(): List<UserStatistics>

    fun getStatsByUsernameForRanking(username: String): List<UserStatistics>
}
