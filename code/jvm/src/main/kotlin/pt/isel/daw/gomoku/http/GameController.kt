package pt.isel.daw.gomoku.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.services.*
import pt.isel.daw.gomoku.services.games.GameCreationError
import pt.isel.daw.gomoku.services.games.GamesService
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success
import java.util.*

@RestController
class GameController(
    private val gameService: GamesService
) {
    @GetMapping(Uris.Games.GET_GAME_BY_ID)
    fun getGameById(@PathVariable id: String): ResponseEntity<GameGetByIdOutputModel> {
        val game = gameService.getGameById(UUID.fromString(id))
        return game?.let {
            ResponseEntity.ok(GameGetByIdOutputModel(GameOutputModel(it.id, it.board, it.playerBLACK, it.playerWHITE)))
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping(Uris.Games.CREATE_GAME)
    fun create(@RequestBody inputModel: GameStartInputModel): ResponseEntity<*> {
        return when (val res = gameService.createGame(inputModel.userBlack, inputModel.userWhite)) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.Games.byId(res.value.id).toASCIIString()
                ).build<Unit>()
            is Failure -> when (res.value) {
                GameCreationError.GameAlreadyExists -> Problem.response(400, Problem.gameAlreadyExists)
                GameCreationError.UserDoesNotExist -> Problem.response(400, Problem.userDoesNotExists)
            }
        }

    }

    @PutMapping(Uris.Games.PLAY)
    fun play(@RequestBody inputModel: GamePlayInputModel): ResponseEntity<GameRoundOutputModel> {
        val res = gameService.play(inputModel.userId, inputModel.round)
        return res.let {
            ResponseEntity.ok(GameRoundOutputModel(it))
        }
    }

    @GetMapping(Uris.Games.GAME_STATE)
    fun gameState(@PathVariable id: String): ResponseEntity<GameStateGetByIdOutputModel> {
        val state = gameService.getGameStateById(UUID.fromString(id))
        return state?.let {
            ResponseEntity.ok(GameStateGetByIdOutputModel(it))
        } ?: ResponseEntity.notFound().build()
    }
}