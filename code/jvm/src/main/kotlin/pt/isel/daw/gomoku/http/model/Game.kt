package pt.isel.daw.gomoku.http.model

import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.User
import java.util.*

data class GamePlayInputModel(val userId : Int, val row : Int, val column : Int)

data class GameStartInputModel(val userBlack: Int, val userWhite: Int, val variant: String)

data class GameRoundOutputModel(val game : GameOutputModel, val state : String)

data class GameOutputModel(val id : UUID?, val board : Board?, val userBlack: User?, val userWhite: User?)

data class GameGetByIdOutputModel(val game: GameOutputModel)

data class GameStateGetByIdOutputModel(val gameState: Game.State)

data class GameGetAllOutputModel(val games : List<GameOutputModel>)

data class GameGetAllByUserOutputModel(val games : List<GameOutputModel>)

