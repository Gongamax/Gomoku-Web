package pt.isel.daw.gomoku.http.model

import pt.isel.daw.gomoku.domain.games.Board
import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.Round
import pt.isel.daw.gomoku.domain.games.RoundResult
import pt.isel.daw.gomoku.domain.users.User
import java.util.*

data class GamePlayInputModel(val userId : UUID, val round: Round)

data class GameStartInputModel(val userBlack: User, val userWhite: User)

data class GameRoundOutputModel(val result : RoundResult)

data class GameOutputModel(val id : UUID, val board : Board, val userBlack: User, val userWhite: User)

data class GameGetByIdOutputModel(val game: GameOutputModel)

data class GameStateGetByIdOutputModel(val gameState: Game.State)

