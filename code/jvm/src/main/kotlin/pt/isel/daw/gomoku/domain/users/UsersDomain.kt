package pt.isel.daw.gomoku.domain.users

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.isel.daw.gomoku.domain.utils.Token
import pt.isel.daw.gomoku.domain.utils.TokenEncoder
import pt.isel.daw.gomoku.domain.utils.TokenValidationInfo
import java.security.SecureRandom
import java.util.*

/**
 * Domain layer for users.
 * It is responsible for the creation of tokens and password validation information.
 * @property passwordEncoder Encoder for passwords.
 * @property tokenEncoder Encoder for tokens.
 * @property config Configuration for the domain.
 * */

@Component
class UsersDomain(
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val config: UsersDomainConfig
) {

    fun generateTokenValue(): String =
        ByteArray(config.tokenSizeInBytes).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == config.tokenSizeInBytes
    } catch (ex: IllegalArgumentException) {
        false
    }

    fun validatePassword(password: String, validationInfo: PasswordValidationInfo) = passwordEncoder.matches(
        password,
        validationInfo.validationInfo
    )

    fun createPasswordValidationInformation(password: String) = PasswordValidationInfo(
        validationInfo = passwordEncoder.encode(password)
    )

    fun isTokenTimeValid(
        clock: Clock,
        token: Token
    ): Boolean {
        val now = clock.now()
        return token.createdAt <= now &&
                (now - token.createdAt) <= config.tokenTtl &&
                (now - token.lastUsedAt) <= config.tokenRollingTtl
    }

    fun getTokenExpiration(token: Token): Instant {
        val absoluteExpiration = token.createdAt + config.tokenTtl
        val rollingExpiration = token.lastUsedAt + config.tokenRollingTtl
        return if (absoluteExpiration < rollingExpiration) {
            absoluteExpiration
        } else {
            rollingExpiration
        }
    }

    fun createTokenValidationInformation(token: String): TokenValidationInfo =
        tokenEncoder.createValidationInformation(token)

    fun isSafePassword(password: String): Boolean =
        password.length in 8..64
                && password.contains(Regex("[a-z]"))
                && password.contains(Regex("[A-Z]"))
                && password.contains(Regex("[0-9]"))

    fun isSafeEmail(email: Email): Boolean =
        email.value.length in 3..64
                && email.value.contains(Regex("[a-zA-Z0-9]"))
                && email.value.contains('@')
                && email.value.contains('.')

    val maxNumberOfTokensPerUser get() = config.maxTokensPerUser
}