package pt.isel.daw.gomoku.http.model

import jakarta.validation.constraints.NotBlank
import pt.isel.daw.gomoku.domain.games.board.Board
import pt.isel.daw.gomoku.domain.users.User


data class GamePlayInputModel(val row: Int, val column: Int)

data class GameRoundOutputModel(val game: GameOutputModel)

data class GameOutputModel(
    val id: Int,
    val board: Board,
    val userBlack: User,
    val userWhite: User,
    val state: String,
    val variant: VariantOutputModel,
    val created: String
)


data class GameGetByIdOutputModel(val game: GameOutputModel, val pollingTimeOut: Int)

data class GameMatchmakingInputModel(@field:NotBlank val variant: String)

data class GameMatchmakingOutputModel(val message: String, val idType: String, val id: Int)

data class ExitMatchmakingOrLeaveGameOutputModel(val message: String)

data class GameMatchmakingStatusOutputModel(
    val mid: Int,
    val uid: Int,
    val gid: Int?,
    val state: String,
    val variant: String,
    val created: String,
    val pollingTimeOut: Int
)

data class GameGetAllOutputModel(
    val page: Int,
    val pageSize: Int
)

data class GameGetAllByUserOutputModel(
    val uid: Int,
    val page: Int,
    val pageSize: Int
)

data class GameGetAllVariantsOutputModel(val variants: List<VariantOutputModel>)

data class VariantOutputModel(
    val name: String,
    val boardDim: Int,
    val playRule: String,
    val openingRule: String,
    val points: Int
)

data class GameExitMatchmakingQueueOutputModel(val message: String)

