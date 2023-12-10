package pt.isel.daw.gomoku.http.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

const val MIN_USERNAME_LENGTH = 3
const val MAX_USERNAME_LENGTH = 40

const val MIN_PASSWORD_LENGTH = 8
const val MAX_PASSWORD_LENGTH = 127

data class UserCreateInputModel(
    @field:Size(
        min = MIN_USERNAME_LENGTH,
        max = MAX_USERNAME_LENGTH,
        message = "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters long."
    )
    val username: String,
    @field:Email(message = "Email must be valid.")
    val email: String,
    @field:Size(
        min = MIN_PASSWORD_LENGTH,
        max = MAX_PASSWORD_LENGTH,
        message = "Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters long."
    )
    val password: String
)

data class UserCreateTokenInputModel(
    @field:Size(
        min = MIN_USERNAME_LENGTH,
        max = MAX_USERNAME_LENGTH,
        message = "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters long."
    )
    val username: String,
    @field:Size(
        min = MIN_PASSWORD_LENGTH,
        max = MAX_PASSWORD_LENGTH,
        message = "Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters long."
    )
    val password: String
)

class UserCreateOutputModel(
    val uid: Int
)

class UserGetByIdOutputModel(
    val uid: Int,
    val username: String,
    val email: String
)

class UserStatsOutputModel(
    val uid: Int,
    val username: String,
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val rank: Int,
    val points: Int
)

class UserHomeOutputModel(
    val uid: Int,
    val username: String,
    val message: String = "Welcome Player! Lets play."
)

data class UserTokenCreateOutputModel(
    val token: String
)
data class RankingInfoOutputModel(
    val page: Int,
    val pageSize: Int
)

data class UserTokenRemoveOutputModel(
    val message: String
)

data class UserUpdateInputModel(
    @field:Size(
        min = MIN_USERNAME_LENGTH,
        max = MAX_USERNAME_LENGTH,
        message = "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters long."
    )
    val username: String,
    @field:Email(message = "Email must be valid.")
    val email: String,
    @field:Size(
        min = MIN_PASSWORD_LENGTH,
        max = MAX_PASSWORD_LENGTH,
        message = "Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters long."
    )
    val password: String
)

data class UserUpdateOutputModel(
    val message: String
)