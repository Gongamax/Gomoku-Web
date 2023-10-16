package pt.isel.daw.gomoku.services.games

import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.utils.Either

sealed class GameCreationError {
    object GameAlreadyExists : GameCreationError()
    object UserDoesNotExist : GameCreationError()
}

typealias GameCreationResult = Either<GameCreationError, Game>