package pt.isel.daw.gomoku.http.model

import pt.isel.daw.gomoku.domain.users.Email

data class UserCreateInputModel(
    val email: Email,
    val username: String,
    val password: String
)

data class UserCreateTokenInputModel(
    val username: String,
    val password: String
)

class UserGetByIdOutputModel(
    val id: Int,
    val username: String,
    val email: Email
)

class UserHomeOutputModel(
    val id: Int,
    val username: String
)

data class UserTokenCreateOutputModel(
    val token: String
)