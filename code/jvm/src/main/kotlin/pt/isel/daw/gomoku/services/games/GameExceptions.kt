package pt.isel.daw.gomoku.services.games

import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.RoundResult
import pt.isel.daw.gomoku.utils.Either

sealed class GameCreationError {
    object GameAlreadyExists : GameCreationError()
    object UserDoesNotExist : GameCreationError()
    object VariantDoesNotExist : GameCreationError()
}

typealias GameCreationResult = Either<GameCreationError, Game>

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

sealed class GameStateGetError {
    object GameDoesNotExist : GameStateGetError()
}

typealias GameStateGetResult = Either<GameStateGetError, Game.State>

sealed class GameListError {
    object UserDoesNotExist : GameListError()
}

typealias GameListResult = Either<GameListError, List<Game>>

sealed class LeaveGameError {
    object GameDoesNotExist : LeaveGameError()
    object InvalidUser : LeaveGameError()
    object GameAlreadyEnded : LeaveGameError()
}

typealias LeaveGameResult = Either<LeaveGameError, Unit>

sealed class MatchmakingError {
    object NoMatchFound : MatchmakingError()
    object InvalidUser : MatchmakingError()
}

typealias MatchmakingResult = Either<MatchmakingError, GameCreationResult>