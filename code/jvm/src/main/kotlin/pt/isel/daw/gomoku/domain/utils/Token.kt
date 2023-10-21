package pt.isel.daw.gomoku.domain.utils

import kotlinx.datetime.Instant

/**
 *  This class represents the token.
 *  @property tokenValidationInfo The validation info.
 *  @property userId The user id.
 *  @property createdAt The creation date.
 *  @property lastUsedAt The last used date.
 * */

class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Id,
    val createdAt: Instant,
    val lastUsedAt: Instant
)