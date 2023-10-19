package pt.isel.daw.gomoku.http.model

import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.User
import java.util.*

data class GamePlayInputModel(val userId : UUID, val round: Round)

data class GameStartInputModel(val userBlack: User, val userWhite: User, val variant: Variant)

data class GameRoundOutputModel(val result : RoundResult)

data class GameOutputModel(val id : UUID, val board : Board, val userBlack: User, val userWhite: User)

data class GameGetByIdOutputModel(val game: GameOutputModel)

data class GameStateGetByIdOutputModel(val gameState: Game.State)

