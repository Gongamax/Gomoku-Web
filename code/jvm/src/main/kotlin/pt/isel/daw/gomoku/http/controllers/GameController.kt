package pt.isel.daw.gomoku.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Uris
import pt.isel.daw.gomoku.services.games.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success

@RestController
class GameController(
    private val gameService: GamesService
) {
    @GetMapping(Uris.Games.GET_GAME_BY_ID)
    fun getGameById(@PathVariable id: Int, user: AuthenticatedUser): ResponseEntity<*> {
        return when (val game = gameService.getGameById(id)) {
            is Success -> ResponseEntity.ok(
                GameGetByIdOutputModel(
                    GameOutputModel(
                        game.value.id.value,
                        game.value.board,
                        game.value.playerBLACK,
                        game.value.playerWHITE
                    )
                )
            )

            is Failure -> when (game.value) {
                GameGetError.GameDoesNotExist -> Problem(
                    typeUri = Problem.gameDoesNotExists,
                    title = "Game does not exist",
                    status = 404,
                    detail = "Game with id $id does not exist",
                    instance = Uris.Games.byId(id)
                ).toResponse()
            }
        }
    }

    @PostMapping(Uris.Games.PLAY)
    fun play(
        @PathVariable id: Int,
        @Valid @RequestBody inputModel: GamePlayInputModel,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        return when (val res =
            gameService.play(id, authenticatedUser.user.id.value, inputModel.row, inputModel.column)) {
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
                GamePlayError.GameDoesNotExist -> Problem(
                    typeUri = Problem.gameDoesNotExists,
                    title = "Game does not exist",
                    status = 404,
                    detail = "Game with id $id does not exist",
                    instance = Uris.Games.byId(id)
                ).toResponse()
                GamePlayError.InvalidUser -> Problem(
                    typeUri = Problem.invalidUser,
                    title = "Invalid user",
                    status = 401,
                    detail = "User with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Games.play(id)
                ).toResponse()
                GamePlayError.InvalidState -> Problem(
                    typeUri = Problem.invalidState,
                    title = "Invalid state",
                    status = 422,
                    detail = "Game with id $id is not in the correct state to play",
                    instance = Uris.Games.play(id)
                ).toResponse()
                GamePlayError.InvalidTime -> Problem(
                    typeUri = Problem.invalidTime,
                    title = "Invalid time",
                    status = 422,
                    detail = "Game with id $id is not in the correct time to play",
                    instance = Uris.Games.play(id)
                ).toResponse()
                GamePlayError.InvalidTurn -> Problem(
                    typeUri = Problem.invalidTurn,
                    title = "Invalid turn",
                    status = 422,
                    detail = "Game with id $id is not in the correct turn to play",
                    instance = Uris.Games.play(id)
                ).toResponse()
                GamePlayError.InvalidPosition -> Problem(
                    typeUri = Problem.invalidPosition,
                    title = "Invalid position",
                    status = 422,
                    detail = "Game with id $id is not in the correct position to play",
                    instance = Uris.Games.play(id)
                ).toResponse()
            }
        }
    }

    @PostMapping(Uris.Games.MATCHMAKING)
    fun matchmaking(
        @Valid @RequestBody inputModel: GameMatchmakingInputModel,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        return when (val res = gameService.tryMatchmaking(authenticatedUser.user.id.value, inputModel.variant)) {
            is Success -> when (res.value) {
                is MatchmakingSuccess.MatchFound -> ResponseEntity.status(201)
                    .header(
                        "Location",
                        Uris.Games.byId(res.value.id).toASCIIString()
                    ).build<Unit>()

                is MatchmakingSuccess.OnWaitingQueue -> ResponseEntity.ok().body("On waiting queue")
            }

            is Failure -> when (res.value) {
                MatchmakingError.InvalidUser -> Problem(
                    typeUri = Problem.invalidUser,
                    title = "Invalid user",
                    status = 401,
                    detail = "User with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Games.matchmaking()
                ).toResponse()
                MatchmakingError.VariantDoesNotExist -> Problem(
                    typeUri = Problem.variantDoesNotExists,
                    title = "Variant does not exist",
                    status = 400,
                    detail = "Variant ${inputModel.variant} does not exist",
                    instance = Uris.Games.matchmaking()
                ).toResponse()
            }
        }
    }

    //TODO: PASSAR A ENVIAR UM MATCHMAKING ENTRY, ADICIOANR O GAMEID À TABELA TAMBÉM
    //TODO: ADICIONAR ROTA GETVARIANTS
    @GetMapping(Uris.Games.MATCHMAKING)
    fun getMatchmakingStatus(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = gameService.getMatchmakingStatus(authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok(GameMatchmakingStatusOutputModel(res.value))

            is Failure -> when (res.value) {
                MatchmakingStatusError.InvalidUser -> Problem(
                    typeUri = Problem.invalidUser,
                    title = "Invalid user",
                    status = 401,
                    detail = "User with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Games.getMatchmakingStatus()
                ).toResponse()
                MatchmakingStatusError.MatchDoesNotExist -> Problem(
                    typeUri = Problem.matchNotFound,
                    title = "Match not found",
                    status = 404,
                    detail = "Match for user with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Games.getMatchmakingStatus()
                ).toResponse()
            }
        }
    }

    @DeleteMapping(Uris.Games.EXIT_MATCHMAKING_QUEUE)
    fun exitMatchmakingQueue(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = gameService.exitMatchmakingQueue(authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok().build<Unit>()

            is Failure -> when (res.value) {
                LeaveMatchmakingError.MatchDoesNotExist -> Problem(
                    typeUri = Problem.matchNotFound,
                    title = "Match not found",
                    status = 404,
                    detail = "Match for user with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Games.exitMatchmakingQueue()
                ).toResponse()
                LeaveMatchmakingError.InvalidUser -> Problem(
                    typeUri = Problem.invalidUser,
                    title = "Invalid user",
                    status = 401,
                    detail = "User with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Games.exitMatchmakingQueue()
                ).toResponse()
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_GAMES)
    fun getAllGames(): ResponseEntity<GameGetAllOutputModel> {
        val games = gameService.getAll()
        return games.let {
            ResponseEntity.ok(GameGetAllOutputModel(it.map { game ->
                GameOutputModel(
                    game.id.value,
                    game.board,
                    game.playerBLACK,
                    game.playerWHITE
                )
            }))
        }
    }


    @PutMapping(Uris.Games.LEAVE)
    fun leave(@PathVariable id: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = gameService.leaveGame(id, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok().build<Unit>()
            is Failure -> when (res.value) {
                LeaveGameError.GameAlreadyEnded -> Problem(
                    typeUri = Problem.gameAlreadyEnded,
                    title = "Game already ended",
                    status = 422,
                    detail = "Game with id $id already ended",
                    instance = Uris.Games.leave(id)
                ).toResponse()
                LeaveGameError.GameDoesNotExist -> Problem(
                    typeUri = Problem.gameDoesNotExists,
                    title = "Game does not exist",
                    status = 404,
                    detail = "Game with id $id does not exist",
                    instance = Uris.Games.leave(id)
                ).toResponse()
                LeaveGameError.InvalidUser -> Problem(
                    typeUri = Problem.invalidUser,
                    title = "Invalid user",
                    status = 401,
                    detail = "User with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Games.leave(id)
                ).toResponse()
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_GAMES_BY_USER)
    fun getAllGamesByUser(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val games = gameService.getGamesOfUser(authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok(
                GameGetAllByUserOutputModel(games.value.map { game ->
                    GameOutputModel(
                        game.id.value,
                        game.board,
                        game.playerBLACK,
                        game.playerWHITE
                    )
                })
            )

            is Failure -> when (games.value) {
                GameListError.UserDoesNotExist -> Problem(
                    typeUri = Problem.userDoesNotExists,
                    title = "Problem.userDoesNotExists",
                    status = 404,
                    detail = "User with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Users.getUsersById(authenticatedUser.user.id.value)
                ).toResponse()
            }
        }
    }
}