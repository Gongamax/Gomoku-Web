package pt.isel.daw.gomoku.services.games

import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.http.util.PageResult
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingStatus
import pt.isel.daw.gomoku.utils.Either

sealed class GameCreationError {
    object GameAlreadyExists : GameCreationError()
    object UserDoesNotExist : GameCreationError()
    object VariantDoesNotExist : GameCreationError()
}

typealias GameCreationResult = Either<GameCreationError, Int>

sealed class GamePlayError {
    object GameDoesNotExist : GamePlayError()
    object InvalidUser : GamePlayError()
    object InvalidState : GamePlayError()
    object InvalidPosition : GamePlayError()
    object InvalidTurn : GamePlayError()
    object InvalidTime : GamePlayError()
}

typealias GamePlayResult = Either<GamePlayError, Game>

sealed class GameGetError {
    object GameDoesNotExist : GameGetError()
}

typealias GameGetResult = Either<GameGetError, Game>

sealed class GameListError {
    object UserDoesNotExist : GameListError()
    object GamesNotFound: GameListError()
}

typealias GameListResult = Either<GameListError, PageResult<Game>>

sealed class LeaveGameError {
    object GameDoesNotExist : LeaveGameError()
    object InvalidUser : LeaveGameError()
    object GameAlreadyEnded : LeaveGameError()
}

typealias LeaveGameResult = Either<LeaveGameError, Unit>

sealed class MatchmakingError {
    object InvalidUser : MatchmakingError()
    object VariantDoesNotExist : MatchmakingError()
}

sealed class MatchmakingSuccess(val id : Int) {
    class MatchFound(gameId : Int) : MatchmakingSuccess(gameId)

    class OnWaitingQueue(matchEntry : Int) : MatchmakingSuccess(matchEntry)
}

typealias MatchmakingResult = Either<MatchmakingError, MatchmakingSuccess>

sealed class LeaveMatchmakingError {
    object InvalidUser : LeaveMatchmakingError()
    object MatchDoesNotExist : LeaveMatchmakingError()
}

typealias LeaveMatchmakingResult = Either<LeaveMatchmakingError, Unit>

sealed class MatchmakingStatusError {
    object MatchDoesNotExist : MatchmakingStatusError()

    object InvalidUser : MatchmakingStatusError()
}

typealias MatchmakingStatusResult = Either<MatchmakingStatusError, MatchmakingStatus>