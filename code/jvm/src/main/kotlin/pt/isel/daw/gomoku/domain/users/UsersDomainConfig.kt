package pt.isel.daw.gomoku.domain.users

import kotlin.time.Duration

/**
 *  Represents the configuration of the user domain.
 *  @property tokenSizeInBytes The size of the authentication token in bytes.
 *  @property tokenTtl The time to live of the authentication token.
 *  @property tokenRollingTtl The rolling time to live of the authentication token.
 *  @property maxTokensPerUser The maximum number of authentication tokens per user.
 * */

data class UsersDomainConfig(
    val tokenSizeInBytes: Int,
    val tokenTtl: Duration,
    val tokenRollingTtl: Duration,
    val maxTokensPerUser: Int
) {
    init {
        require(tokenSizeInBytes > 0)
        require(tokenTtl.isPositive())
        require(tokenRollingTtl.isPositive())
        require(maxTokensPerUser > 0)
    }
}

