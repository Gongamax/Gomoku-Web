package pt.isel.daw.gomoku.http.model

data class UserCreateInputModel(
    val username: String,
    val password: String
)

data class UserCreateTokenInputModel(
    val username: String,
    val password: String
)

class UserGetByIdOutputModel(
    val id: Int,
    val username: String
    /** More properties to come **/
)

class UserHomeOutputModel(
    val id: Int,
    val username: String
)

data class UserTokenCreateOutputModel(
    val token: String
)