package pt.isel.daw.gomoku.repository

import kotlinx.datetime.Instant
import pt.isel.daw.gomoku.domain.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.Token
import pt.isel.daw.gomoku.domain.TokenValidationInfo
import pt.isel.daw.gomoku.domain.User


interface UsersRepository {

    fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo
    ): Int

    fun getUserByUsername(username: String): User?

    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun isUserStoredByUsername(username: String): Boolean

    fun createToken(token: Token, maxTokens: Int)

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int
}
