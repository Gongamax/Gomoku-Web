package pt.isel.daw.gomoku.http

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.services.games.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success

@RestController
class GameController(
    private val gameService: GamesService
) {
    @GetMapping(Uris.Games.GET_GAME_BY_ID) //user: AuthenticatedUser
    fun getGameById(@PathVariable id: Int): ResponseEntity<*> {
        return when (val game = gameService.getGameById(id)) {
            is Success -> ResponseEntity.ok(
                GameGetByIdOutputModel(
                    GameOutputModel(game.value.id.value, game.value.board, game.value.playerBLACK, game.value.playerWHITE)
                )
            )

            is Failure -> when (game.value) {
                GameGetError.GameDoesNotExist -> Problem.response(404, Problem.gameDoesNotExists)
            }
        }
    }

    @PostMapping(Uris.Games.CREATE_GAME) //user: AuthenticatedUser
    fun create(@Valid @RequestBody inputModel: GameStartInputModel): ResponseEntity<*> {
        return when (val res = gameService.createGame(inputModel.userBlack, inputModel.userWhite, inputModel.variant)) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.Games.byId(res.value).toASCIIString()
                ).build<Unit>()

            is Failure -> when (res.value) {
                GameCreationError.GameAlreadyExists -> Problem.response(422, Problem.gameAlreadyExists)
                GameCreationError.UserDoesNotExist -> Problem.response(404, Problem.userDoesNotExists)
                GameCreationError.VariantDoesNotExist -> Problem.response(400, Problem.variantDoesNotExists)
            }
        }
    }

    @PutMapping(Uris.Games.PLAY) //user: AuthenticatedUser
    fun play(
        @PathVariable id: Int,
        @Valid @RequestBody inputModel: GamePlayInputModel
    ): ResponseEntity<*> {
        val res = gameService.play(id, inputModel.userId, inputModel.row, inputModel.column)
        return when (res) {
            is Success -> ResponseEntity.ok(
                GameRoundOutputModel(
                    GameOutputModel(
                        res.value.id.value,
                        res.value.board,
                        res.value.playerBLACK,
                        res.value.playerWHITE
                    ), res.value.state.toString()
                )
            )

            is Failure -> when (res.value) {
                GamePlayError.GameDoesNotExist -> Problem.response(404, Problem.gameDoesNotExists)
                GamePlayError.InvalidUser -> Problem.response(401, Problem.invalidUser)
                GamePlayError.InvalidState -> Problem.response(422, Problem.invalidState)
                GamePlayError.InvalidTime -> Problem.response(422, Problem.invalidTime)
                GamePlayError.InvalidTurn -> Problem.response(422, Problem.invalidTurn)
                GamePlayError.InvalidPosition -> Problem.response(422, Problem.invalidPosition)
            }
        }
    }

//    @PostMapping(Uris.Games.MATCHMAKING) //user: AuthenticatedUser

    @GetMapping(Uris.Games.GET_ALL_GAMES)
    fun getAllGames(): ResponseEntity<List<GameGetByIdOutputModel>> {
        val games = gameService.getAll()
        return games.let {
            ResponseEntity.ok(it.map { game ->
                GameGetByIdOutputModel(
                    GameOutputModel(
                        game.id.value,
                        game.board,
                        game.playerBLACK,
                        game.playerWHITE
                    )
                )
            })
        }
    }

    @PutMapping(Uris.Games.LEAVE)
    fun leave(@PathVariable id: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when(val res = gameService.leaveGame(id, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok().build<Unit>()
            is Failure -> when(res.value) {
                LeaveGameError.GameAlreadyEnded -> Problem.response(422, Problem.gameAlreadyEnded)
                LeaveGameError.GameDoesNotExist -> Problem.response(404, Problem.gameDoesNotExists)
                LeaveGameError.InvalidUser -> Problem.response(401, Problem.invalidUser)
            }
        }
    }

//    @GetMapping(Uris.Games.GAME_STATE)
//    fun gameState(@PathVariable id: String): ResponseEntity<GameStateGetByIdOutputModel> {
//        val state = gameService.getGameStateById(UUID.fromString(id))
//        return state?.let {
//            ResponseEntity.ok(GameStateGetByIdOutputModel(it))
//        } ?: ResponseEntity.notFound().build()
//    }
}