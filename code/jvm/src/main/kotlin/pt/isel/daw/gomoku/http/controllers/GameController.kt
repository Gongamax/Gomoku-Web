package pt.isel.daw.gomoku.http.controllers

import jakarta.validation.Valid
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.media.Problem
import pt.isel.daw.gomoku.http.media.siren.siren
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Rels
import pt.isel.daw.gomoku.http.util.Uris
import pt.isel.daw.gomoku.services.games.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.PositiveValue
import pt.isel.daw.gomoku.utils.Success
import java.net.URI

@RestController
class GameController(
    private val gameService: GamesService
) {
    @GetMapping(Uris.Games.GET_GAME_BY_ID)
    fun getGameById(@PathVariable id: Int, user: AuthenticatedUser): ResponseEntity<*> {
        return when (val game = gameService.getGameById(id)) {
            is Success -> ResponseEntity.ok(
                siren(
                    GameGetByIdOutputModel(
                        GameOutputModel(
                            game.value.id.value,
                            game.value.board,
                            game.value.playerBLACK,
                            game.value.playerWHITE,
                            game.value.state.toString(),
                            game.value.variant.toString(),
                            game.value.created.toString()
                        )
                    )
                ) {
                    clazz("game")
                    link(Uris.Games.byId(id), Rels.SELF)
                    action(
                        "play",
                        Uris.Games.play(id),
                        HttpMethod.POST,
                        "application/x-www-form-urlencoded"
                    ) {
                        numberField("row")
                        numberField("column")
                        requireAuth(true)
                    }
                    action(
                        "leave-game",
                        Uris.Games.leave(id),
                        HttpMethod.PUT,
                        "application/json"
                    ) {
                        requireAuth(true)
                    }
                    requireAuth(true)
                }
            )

            is Failure -> when (game.value) {
                GameGetError.GameDoesNotExist -> Problem.gameDoesNotExists(instance = Uris.Games.byId(id), gid = id)
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
                siren(
                    GameRoundOutputModel(
                        GameOutputModel(
                            res.value.id.value,
                            res.value.board,
                            res.value.playerBLACK,
                            res.value.playerWHITE,
                            res.value.state.toString(),
                            res.value.variant.toString(),
                            res.value.created.toString()
                        ),
                        res.value.state.toString()
                    )
                ) {
                    clazz("play")
                    link(Uris.Games.play(id), Rels.SELF)
                    link(Uris.Games.byId(id), Rels.GAME)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                GamePlayError.GameDoesNotExist -> Problem.gameDoesNotExists(instance = Uris.Games.play(id), gid = id)

                GamePlayError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.play(id), userId = authenticatedUser.user.id.value
                )

                GamePlayError.InvalidState -> Problem.invalidState(instance = Uris.Games.play(id), gid = id)

                GamePlayError.InvalidTime -> Problem.invalidTime(instance = Uris.Games.play(id), gid = id)

                GamePlayError.InvalidTurn -> Problem.invalidTurn(instance = Uris.Games.play(id), gid = id)

                GamePlayError.InvalidPosition -> Problem.invalidPosition(instance = Uris.Games.play(id), gid = id)
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
                    ).body(
                        siren(
                            GameMatchmakingOutputModel(
                                "Match found",
                                "gid",
                                res.value.id
                            )
                        ) {
                            clazz("matchmaking")
                            link(Uris.Games.byId(res.value.id), Rels.SELF)
                            link(Uris.Games.byId(res.value.id), Rels.GAME)
                            entity(
                                "{ status: MATCHED }",
                                Rels.MATCHMAKING_STATUS
                            ) {
                                clazz("matchmaking-status")
                                requireAuth(true)
                            }
                            requireAuth(true)
                        }
                    )

                is MatchmakingSuccess.OnWaitingQueue -> ResponseEntity.ok().body(
                    siren(
                        GameMatchmakingOutputModel(
                            "User on waiting queue",
                            "mid",
                            res.value.id
                        )
                    ) {
                        clazz("matchmaking")
                        link(Uris.Games.matchmaking(), Rels.SELF)
                        entity(
                            "{ status: PENDING }",
                            Rels.MATCHMAKING_STATUS
                        ) {
                            clazz("matchmaking-status")
                            requireAuth(true)
                        }
                        action(
                            "leave-matchmaking",
                            Uris.Games.exitMatchmakingQueue(res.value.id),
                            HttpMethod.DELETE,
                            "application/json"
                        ) {
                            requireAuth(true)
                        }
                        requireAuth(true)
                    }
                )
            }

            is Failure -> when (res.value) {
                MatchmakingError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.matchmaking(),
                    userId = authenticatedUser.user.id.value
                )

                MatchmakingError.VariantDoesNotExist -> Problem.variantDoesNotExists(
                    instance = Uris.Games.matchmaking(),
                    variantName = inputModel.variant
                )

                MatchmakingError.UserAlreadyInQueue -> Problem.userAlreadyInQueue(
                    instance = Uris.Games.matchmaking(),
                    uid = authenticatedUser.user.id.value
                )
            }
        }
    }

    @GetMapping(Uris.Games.GET_MATCHMAKING_STATUS)
    fun getMatchmakingStatus(authenticatedUser: AuthenticatedUser, @PathVariable id: Int): ResponseEntity<*> {
        return when (val res = gameService.getMatchmakingStatus(id, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok(
                siren(
                    GameMatchmakingStatusOutputModel(
                        res.value.id,
                        res.value.userId,
                        res.value.gameId,
                        res.value.status.toString(),
                        res.value.variant,
                        res.value.created.toString()
                    )
                ) {
                    clazz("matchmaking-status")
                    link(Uris.Games.getMatchmakingStatus(id), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                MatchmakingStatusError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.getMatchmakingStatus(id),
                    userId = authenticatedUser.user.id.value
                )

                MatchmakingStatusError.MatchDoesNotExist -> Problem.matchNotFound(
                    instance = Uris.Games.getMatchmakingStatus(id),
                    matchEntryId = id
                )
            }
        }
    }

    @DeleteMapping(Uris.Games.EXIT_MATCHMAKING_QUEUE)
    fun exitMatchmakingQueue(authenticatedUser: AuthenticatedUser, @PathVariable id: Int): ResponseEntity<*> {
        return when (val res = gameService.exitMatchmakingQueue(id, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok(
                siren(
                    "User left matchmaking queue"
                ) {
                    clazz("leave-matchmaking")
                    link(Uris.Games.exitMatchmakingQueue(id), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                LeaveMatchmakingError.MatchDoesNotExist -> Problem.matchNotFound(
                    instance = Uris.Games.exitMatchmakingQueue(id),
                    matchEntryId = id
                )

                LeaveMatchmakingError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.exitMatchmakingQueue(id),
                    userId = authenticatedUser.user.id.value
                )
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_GAMES)
    fun getAllGames(@RequestParam page: String): ResponseEntity<*> =
        when (val games = gameService.getAll(PositiveValue(page.toInt()))) {
            is Failure ->
                Problem(
                    typeUri = Problem.gamesNotFound,
                    title = "No games",
                    status = 404,
                    detail = "There are no games",
                    instance = Uris.Games.getAllGames()
                ).toResponse()

            is Success ->
                ResponseEntity.ok(
                    siren(
                        GameGetAllOutputModel(
                            games.value.content.map { game ->
                                GameOutputModel(
                                    game.id.value,
                                    game.board,
                                    game.playerBLACK,
                                    game.playerWHITE,
                                    game.state.toString(),
                                    game.variant.toString(),
                                    game.created.toString(),
                                )
                            }
                        )
                    ) {
                        clazz("game-list")
                        link(Uris.Games.getAllGames(), Rels.SELF)
                        entity(
                            "{gid, board, playerBlack, playerWhite, state, variant, created}",
                            Rels.GAME
                        ) {
                            clazz("game")
                            link(URI(Uris.Games.GET_GAME_BY_ID), Rels.GAME)
                            requireAuth(true)
                        }
                        requireAuth(true)
                    }
                )
        }


    @PutMapping(Uris.Games.LEAVE)
    fun leave(@PathVariable id: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = gameService.leaveGame(id, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok(
                siren(
                    "User can leave the game"
                ) {
                    clazz("leave-game")
                    link(Uris.Games.leave(id), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                LeaveGameError.GameAlreadyEnded -> Problem.gameAlreadyEnded(instance = Uris.Games.leave(id), gid = id)
                LeaveGameError.GameDoesNotExist -> Problem.gameDoesNotExists(instance = Uris.Games.leave(id), gid = id)
                LeaveGameError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.leave(id),
                    userId = authenticatedUser.user.id.value
                )
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_GAMES_BY_USER)
    fun getAllGamesByUser(
        authenticatedUser: AuthenticatedUser,
        @PathVariable uid: String?,
        @RequestParam page: String
    ): ResponseEntity<*> {
        return when (
            val games = gameService.getGamesOfUser(
                uid?.toInt() ?: authenticatedUser.user.id.value, PositiveValue(page.toInt())
            )
        ) {
            is Success -> ResponseEntity.ok(
                siren(
                    GameGetAllByUserOutputModel(
                        games.value.content.map { game ->
                            GameOutputModel(
                                game.id.value,
                                game.board,
                                game.playerBLACK,
                                game.playerWHITE,
                                game.state.toString(),
                                game.variant.toString(),
                                game.created.toString(),
                            )
                        }
                    )
                ) {
                    clazz("game-list-of-user")
                    link(Uris.Games.getAllGamesByUser(uid?.toInt() ?: authenticatedUser.user.id.value), Rels.SELF)
                    entity(
                        "{gid, board, playerBlack, playerWhite, state, variant, created}",
                        Rels.GAME
                    ) {
                        clazz("game")
                        link(URI(Uris.Games.GET_GAME_BY_ID), Rels.GAME)
                        requireAuth(true)
                    }
                    requireAuth(true)
                }
            )

            is Failure -> when (games.value) {
                GameListError.UserDoesNotExist -> Problem.userDoesNotExists(
                    instance = Uris.Games.getAllGamesByUser(
                        uid?.toInt() ?: authenticatedUser.user.id.value
                    ),
                    uid = uid?.toInt() ?: authenticatedUser.user.id.value
                )

                GameListError.GamesNotFound -> Problem.gamesNotFound(
                    instance = Uris.Games.getAllGamesByUser(
                        uid?.toInt() ?: authenticatedUser.user.id.value
                    )
                )
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_VARIANTS)
    fun getAllVariants(): ResponseEntity<*> {
        return when (val res = gameService.getAllVariants()) {
            is Success -> ResponseEntity.ok(
                siren(
                    GameGetAllVariantsOutputModel(
                        res.value.map { variant ->
                            VariantOutputModel(
                                variant.name,
                                variant.boardDim.toInt(),
                                variant.playingRule.toString(),
                                variant.openingRule.toString()
                            )
                        }
                    )
                ) {
                    clazz("variant-list")
                    link(Uris.Games.getAllVariants(), Rels.SELF)
                    entity(
                        "{vid, name, description}",
                        Rels.VARIANT
                    ) {
                        clazz("variant")
                        link(URI(Uris.Games.GET_VARIANT_BY_NAME), Rels.VARIANT)
                        requireAuth(true)
                    }
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                VariantListError.VariantsNotFound -> Problem.variantsNotFound(instance = Uris.Games.getAllVariants())
            }
        }
    }
}