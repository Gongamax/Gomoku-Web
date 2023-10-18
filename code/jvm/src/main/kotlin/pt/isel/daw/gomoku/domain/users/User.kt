package pt.isel.daw.gomoku.domain.users

/**
 * Represents a user.
 * @property id the id of the user.
 * @property username the username of the user.
 * @property email the email of the user.
 * @property passwordValidation the password validation info of the user.
* */

data class User(
    val id: Int,
    val username: String,
    val email : Email,
    val passwordValidation: PasswordValidationInfo
)