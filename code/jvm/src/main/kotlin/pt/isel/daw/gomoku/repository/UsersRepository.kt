package pt.isel.daw.gomoku.repository

import kotlinx.datetime.Instant
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.utils.Token
import pt.isel.daw.gomoku.domain.utils.TokenValidationInfo
import pt.isel.daw.gomoku.domain.users.User


interface UsersRepository {

    fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo
    ): Int

    fun getUserByUsername(username: String): User?

    fun getUserById(id: Int): User?

    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    fun isUserStoredByUsername(username: String): Boolean

    fun createToken(token: Token, maxTokens: Int)

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int
}
