package pt.isel.daw.gomoku.http.controllers

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.media.Problem
import pt.isel.daw.gomoku.http.media.siren.SirenModel
import pt.isel.daw.gomoku.http.media.siren.siren
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Rels
import pt.isel.daw.gomoku.http.util.Uris
import pt.isel.daw.gomoku.services.games.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.PageValue
import pt.isel.daw.gomoku.utils.Success
import java.net.URI

@RestController
class GameController(
    private val gameService: GamesService
) {
    private val pollingTimeout = 3000

    @GetMapping(Uris.Games.GET_GAME_BY_ID)
    fun getGameById(@PathVariable gid: Int, user: AuthenticatedUser): ResponseEntity<*> {
        return when (val game = gameService.getGameById(gid)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    GameGetByIdOutputModel(
                        GameOutputModel(
                            game.value.id.value,
                            game.value.board,
                            game.value.playerBLACK,
                            game.value.playerWHITE,
                            game.value.state.toString(),
                            VariantOutputModel(
                                game.value.variant.name,
                                game.value.variant.boardDim.toInt(),
                                game.value.variant.playingRule.toString(),
                                game.value.variant.openingRule.toString(),
                                game.value.variant.points
                            ),
                            game.value.created.toString()
                        ),
                        pollingTimeout
                    )
                ) {
                    clazz("game")
                    link(Uris.Games.byId(gid), Rels.SELF)
                    action(
                        "play",
                        Uris.Games.play(gid),
                        HttpMethod.POST,
                        "application/x-www-form-urlencoded"
                    ) {
                        numberField("row")
                        numberField("column")
                        requireAuth(true)
                    }
                    action(
                        "leave-game",
                        Uris.Games.leave(gid),
                        HttpMethod.PUT,
                        "application/json"
                    ) {
                        requireAuth(true)
                    }
                    requireAuth(true)
                }
            )

            is Failure -> when (game.value) {
                GameGetError.GameDoesNotExist -> Problem.gameDoesNotExists(instance = Uris.Games.byId(gid), gid = gid)
            }
        }
    }

    @PostMapping(Uris.Games.PLAY)
    fun play(
        @PathVariable gid: Int,
        @Valid @RequestBody inputModel: GamePlayInputModel,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        return when (val res =
            gameService.play(gid, authenticatedUser.user.id.value, inputModel.row, inputModel.column)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    GameRoundOutputModel(
                        GameOutputModel(
                            res.value.id.value,
                            res.value.board,
                            res.value.playerBLACK,
                            res.value.playerWHITE,
                            res.value.state.toString(),
                            VariantOutputModel(
                                res.value.variant.name,
                                res.value.variant.boardDim.toInt(),
                                res.value.variant.playingRule.toString(),
                                res.value.variant.openingRule.toString(),
                                res.value.variant.points
                            ),
                            res.value.created.toString()
                        )
                    )
                ) {
                    clazz("play")
                    link(Uris.Games.play(gid), Rels.SELF)
                    link(Uris.Games.byId(gid), Rels.GAME)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                GamePlayError.GameDoesNotExist -> Problem.gameDoesNotExists(instance = Uris.Games.play(gid), gid = gid)

                GamePlayError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.play(gid), userId = authenticatedUser.user.id.value
                )

                GamePlayError.InvalidState -> Problem.invalidState(instance = Uris.Games.play(gid), gid = gid)

                GamePlayError.InvalidTime -> Problem.invalidTime(instance = Uris.Games.play(gid), gid = gid)

                GamePlayError.InvalidTurn -> Problem.invalidTurn(instance = Uris.Games.play(gid), gid = gid)

                GamePlayError.InvalidPosition -> Problem.invalidPosition(instance = Uris.Games.play(gid), gid = gid)
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
                    )
                    .header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
                    .body(
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
                            requireAuth(true)
                        }
                    )

                is MatchmakingSuccess.OnWaitingQueue -> ResponseEntity.ok()
                    .header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
                    .body(
                        siren(
                            GameMatchmakingOutputModel(
                                "User on waiting queue",
                                "mid",
                                res.value.id
                            )
                        ) {
                            clazz("matchmaking")
                            link(Uris.Games.matchmaking(), Rels.SELF)
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
    fun getMatchmakingStatus(authenticatedUser: AuthenticatedUser, @PathVariable mid: Int): ResponseEntity<*> {
        return when (val res = gameService.getMatchmakingStatus(mid, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    GameMatchmakingStatusOutputModel(
                        res.value.id,
                        res.value.userId,
                        res.value.gameId,
                        res.value.status.toString(),
                        res.value.variant,
                        res.value.created.toString(),
                        pollingTimeout
                    )
                ) {
                    clazz("matchmaking-status")
                    link(Uris.Games.getMatchmakingStatus(mid), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                MatchmakingStatusError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.getMatchmakingStatus(mid),
                    userId = authenticatedUser.user.id.value
                )

                MatchmakingStatusError.MatchDoesNotExist -> Problem.matchNotFound(
                    instance = Uris.Games.getMatchmakingStatus(mid),
                    matchEntryId = mid
                )
            }
        }
    }

    @DeleteMapping(Uris.Games.EXIT_MATCHMAKING_QUEUE)
    fun exitMatchmakingQueue(authenticatedUser: AuthenticatedUser, @PathVariable mid: Int): ResponseEntity<*> {
        return when (val res = gameService.exitMatchmakingQueue(mid, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    ExitMatchmakingOrLeaveGameOutputModel("User ${authenticatedUser.user.username} left matchmaking queue")
                ) {
                    clazz("leave-matchmaking")
                    link(Uris.Games.exitMatchmakingQueue(mid), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                LeaveMatchmakingError.MatchDoesNotExist -> Problem.matchNotFound(
                    instance = Uris.Games.exitMatchmakingQueue(mid),
                    matchEntryId = mid
                )

                LeaveMatchmakingError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.exitMatchmakingQueue(mid),
                    userId = authenticatedUser.user.id.value
                )
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_GAMES)
    fun getAllGames(@RequestParam(name = "page", defaultValue = "1") page: Int): ResponseEntity<*> =
        when (val games = gameService.getAll(PageValue(page))) {
            is Success ->
                ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                    siren(
                        GameGetAllOutputModel(
                            page,
                            games.value.pageSize
                        )
                    ) {
                        clazz("game-list")
                        link(URI(Uris.Games.getAllGames().toASCIIString() + "?page=" + page), Rels.SELF)
                        games.value.content.forEach { game ->
                            entity(
                                GameOutputModel(
                                    game.id.value,
                                    game.board,
                                    game.playerBLACK,
                                    game.playerWHITE,
                                    game.state.toString(),
                                    VariantOutputModel(
                                        game.variant.name,
                                        game.variant.boardDim.toInt(),
                                        game.variant.playingRule.toString(),
                                        game.variant.openingRule.toString(),
                                        game.variant.points
                                    ),
                                    game.created.toString()
                                ),
                                Rels.GAME
                            ) {
                                clazz("game")
                                link(Uris.Games.byId(game.id.value), Rels.GAME)
                                requireAuth(true)
                            }
                        }
                        if (games.value.firstPage != null)
                            link(URI(Uris.Games.GET_ALL_GAMES + "?page=" + games.value.firstPage), Rels.FIRST)

                        if (games.value.previousPage != null)
                            link(URI(Uris.Games.GET_ALL_GAMES + "?page=" + games.value.previousPage), Rels.PREVIOUS)

                        if (games.value.nextPage != null)
                            link(URI(Uris.Games.GET_ALL_GAMES + "?page=" + games.value.nextPage), Rels.NEXT)

                        link(URI(Uris.Games.GET_ALL_GAMES + "?page=" + games.value.lastPage), Rels.LAST)
                        requireAuth(true)
                    }
                )

            is Failure -> Problem.invalidPageNumber(instance = Uris.Games.getAllGames(), pageNumber = page)
        }


    @PutMapping(Uris.Games.LEAVE)
    fun leave(@PathVariable gid: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        return when (val res = gameService.leaveGame(gid, authenticatedUser.user.id.value)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    ExitMatchmakingOrLeaveGameOutputModel("User ${authenticatedUser.user.username} left the game")
                ) {
                    clazz("leave-game")
                    link(Uris.Games.leave(gid), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                LeaveGameError.GameAlreadyEnded -> Problem.gameAlreadyEnded(instance = Uris.Games.leave(gid), gid = gid)
                LeaveGameError.GameDoesNotExist -> Problem.gameDoesNotExists(
                    instance = Uris.Games.leave(gid),
                    gid = gid
                )

                LeaveGameError.InvalidUser -> Problem.invalidUser(
                    instance = Uris.Games.leave(gid),
                    userId = authenticatedUser.user.id.value
                )
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_GAMES_BY_USER)
    fun getAllGamesByUser(
        authenticatedUser: AuthenticatedUser,
        @PathVariable uid: String?,
        @RequestParam(name = "page", defaultValue = "1") page: Int
    ): ResponseEntity<*> {
        val id = uid?.toInt() ?: authenticatedUser.user.id.value
        return when (val games = gameService.getGamesOfUser(id, PageValue(page))) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    GameGetAllByUserOutputModel(
                        id,
                        page,
                        games.value.pageSize
                    )
                ) {
                    clazz("game-list-of-user")
                    link(URI(Uris.Games.getAllGamesByUser(id).path + "?page=" + page), Rels.SELF)
                    games.value.content.forEach { game ->
                        entity(
                            GameOutputModel(
                                game.id.value,
                                game.board,
                                game.playerBLACK,
                                game.playerWHITE,
                                game.state.toString(),
                                VariantOutputModel(
                                    game.variant.name,
                                    game.variant.boardDim.toInt(),
                                    game.variant.playingRule.toString(),
                                    game.variant.openingRule.toString(),
                                    game.variant.points
                                ),
                                game.created.toString()
                            ),
                            Rels.GAME
                        ) {
                            clazz("game")
                            link(Uris.Games.byId(game.id.value), Rels.GAME)
                            requireAuth(true)
                        }
                    }
                    if (games.value.firstPage != null)
                        link(URI(Uris.Games.getAllGamesByUser(id).path + "?page=" + games.value.firstPage), Rels.FIRST)

                    if (games.value.previousPage != null)
                        link(
                            URI(Uris.Games.getAllGamesByUser(id).path + "?page=" + games.value.previousPage),
                            Rels.PREVIOUS
                        )

                    if (games.value.nextPage != null)
                        link(URI(Uris.Games.getAllGamesByUser(id).path + "?page=" + games.value.nextPage), Rels.NEXT)

                    link(URI(Uris.Games.getAllGamesByUser(id).path + "?page=" + games.value.lastPage), Rels.LAST)
                    requireAuth(true)
                }
            )

            is Failure -> when (games.value) {
                GameListError.UserDoesNotExist -> Problem.userDoesNotExists(
                    instance = Uris.Games.getAllGamesByUser(id),
                    uid = id
                )

                GameListError.InvalidPageNumber -> Problem.invalidPageNumber(
                    instance = Uris.Games.getAllGamesByUser(id),
                    pageNumber = page
                )
            }
        }
    }

    @GetMapping(Uris.Games.GET_ALL_VARIANTS)
    fun getAllVariants(): ResponseEntity<*> {
        return when (val res = gameService.getAllVariants()) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    GameGetAllVariantsOutputModel(
                        res.value.map { variant ->
                            VariantOutputModel(
                                variant.name,
                                variant.boardDim.toInt(),
                                variant.playingRule.toString(),
                                variant.openingRule.toString(),
                                variant.points
                            )
                        }
                    )
                ) {
                    clazz("variant-list")
                    link(Uris.Games.getAllVariants(), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                VariantListError.VariantsNotFound -> Problem.variantsNotFound(instance = Uris.Games.getAllVariants())
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GameController::class.java)
    }
}