package pt.isel.daw.gomoku.http.model

data class UserCreateTokenInputModel(
    val username: String,
    val password: String
)
