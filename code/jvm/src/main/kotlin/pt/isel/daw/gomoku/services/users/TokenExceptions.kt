package pt.isel.daw.gomoku.services.users

import kotlinx.datetime.Instant
import pt.isel.daw.gomoku.utils.Either

data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: Instant
)

sealed class TokenCreationError {
    object UserOrPasswordAreInvalid : TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, TokenExternalInfo>