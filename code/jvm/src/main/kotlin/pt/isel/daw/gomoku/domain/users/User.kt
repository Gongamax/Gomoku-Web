package pt.isel.daw.gomoku.domain.users

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo
)