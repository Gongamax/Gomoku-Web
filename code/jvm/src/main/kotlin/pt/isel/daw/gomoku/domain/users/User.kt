package pt.isel.daw.gomoku.domain.users

import pt.isel.daw.gomoku.domain.utils.Id

/**
 * Represents a user.
 * @property id the id of the user.
 * @property username the username of the user.
 * @property email the email of the user.
 * @property passwordValidation the password validation info of the user.
* */

data class User(
    val id: Id,
    val username: String,
    val email : Email,
    val passwordValidation: PasswordValidationInfo
)