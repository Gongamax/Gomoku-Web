package pt.isel.daw.gomoku.services.users

import pt.isel.daw.gomoku.utils.Either

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
    object InsecureEmail : UserCreationError()
}

typealias UserCreationResult = Either<UserCreationError, Int>