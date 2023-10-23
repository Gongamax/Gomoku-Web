package pt.isel.daw.gomoku.services.users

import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.users.UserStatistics
import pt.isel.daw.gomoku.utils.Either

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
    object InsecureEmail : UserCreationError()
}

typealias UserCreationResult = Either<UserCreationError, Int>

sealed class UserGetError {
    object UserDoesNotExist : UserGetError()

    object UserIsNotAuthenticated : UserGetError()

    object InvalidToken : UserGetError()

    object TokenExpired : UserGetError()
}

typealias UserGetByIdResult = Either<UserGetError, User>


sealed class UserStatsError {
    object UserStatsDoesNotExist : UserStatsError()
    object UserDoesNotExist: UserStatsError()
}

typealias UserStatsResult = Either<UserStatsError, UserStatistics>