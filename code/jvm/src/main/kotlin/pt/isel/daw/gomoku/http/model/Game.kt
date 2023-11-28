package pt.isel.daw.gomoku.http.model

import jakarta.validation.constraints.NotBlank
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingStatus


data class GamePlayInputModel(val row: Int, val column: Int)

data class GameStartInputModel(val userBlack: Int,val userWhite: Int,@field:NotBlank val variant: String)

data class GameRoundOutputModel(val game: GameOutputModel, val state: String)

data class GameOutputModel(
    val id: Int,
    val board: Board,
    val userBlack: User,
    val userWhite: User,
    val state: String,
    val variant: String,
    val created: String
)

data class GameGetByIdOutputModel(val game: GameOutputModel)

data class GameMatchmakingInputModel(@field:NotBlank val variant: String)

data class GameMatchmakingStatusOutputModel(val status: MatchmakingStatus)

data class GameGetAllOutputModel(val games: List<GameOutputModel>)

data class GameGetAllByUserOutputModel(val games: List<GameOutputModel>)

