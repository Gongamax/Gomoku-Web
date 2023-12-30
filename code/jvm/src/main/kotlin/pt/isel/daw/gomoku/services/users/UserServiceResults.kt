package pt.isel.daw.gomoku.services.users

import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.users.UserStatistics
import pt.isel.daw.gomoku.services.utils.PageResult
import pt.isel.daw.gomoku.utils.Either

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
    object InsecureEmail : UserCreationError()
}

typealias UserCreationResult = Either<UserCreationError, Int>

sealed class UserGetByIdError {
    object UserDoesNotExist : UserGetByIdError()
}

typealias UserGetByIdResult = Either<UserGetByIdError, User>

sealed class UserGetByTokenError {
    object UserDoesNotExist : UserGetByTokenError()

    object UserIsNotAuthenticated : UserGetByTokenError()

    object InvalidToken : UserGetByTokenError()

    object TokenExpired : UserGetByTokenError()
}

typealias UserGetByTokenResult = Either<UserGetByTokenError, User>

sealed class UserStatsError {
    object UserStatsDoesNotExist : UserStatsError()
    object UserDoesNotExist: UserStatsError()
}

typealias UserStatsResult = Either<UserStatsError, UserStatistics>

sealed class UserUpdateError {
    object UserDoesNotExist : UserUpdateError()
    object InsecurePassword : UserUpdateError()
    object InsecureEmail : UserUpdateError()
}

typealias UserUpdateResult = Either<UserUpdateError, Int>


sealed class RankingError {
    object InvalidPageNumber : RankingError()
}

typealias RankingResult = Either<RankingError, PageResult<UserStatistics>>

