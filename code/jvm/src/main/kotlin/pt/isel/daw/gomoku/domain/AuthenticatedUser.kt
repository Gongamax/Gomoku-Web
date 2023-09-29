package pt.isel.daw.gomoku.domain

class AuthenticatedUser(
    val user: User,
    val token: String
)