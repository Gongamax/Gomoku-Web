package pt.isel.daw.gomoku.domain.users

class AuthenticatedUser(
    val user: User,
    val token: String
)