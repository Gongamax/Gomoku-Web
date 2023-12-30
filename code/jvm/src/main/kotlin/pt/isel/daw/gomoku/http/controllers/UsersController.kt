package pt.isel.daw.gomoku.http.controllers

import jakarta.validation.Valid
import kotlinx.datetime.Clock
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
import pt.isel.daw.gomoku.services.users.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.PageValue
import pt.isel.daw.gomoku.utils.Success
import java.net.URI


@RestController
class UsersController(
    private val userService: UsersService
) {

    @PostMapping(Uris.Users.REGISTER)
    fun create(@RequestBody @Valid input: UserCreateInputModel): ResponseEntity<*> {
        return when (val res = userService.createUser(input.username, input.email, input.password)) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.Users.getUsersById(res.value).toASCIIString()
                )
                .header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
                .body(
                    siren(UserCreateOutputModel(res.value)) {
                        clazz("register")
                        link(Uris.Users.register(), Rels.SELF)
                        requireAuth(false)
                    }
                )

            is Failure -> when (res.value) {
                UserCreationError.InsecurePassword -> Problem.insecurePassword(Uris.Users.register())
                UserCreationError.UserAlreadyExists -> Problem.userAlreadyExists(Uris.Users.register(), input.username)
                UserCreationError.InsecureEmail -> Problem.insecureEmail(Uris.Users.register())
            }
        }
    }

    @PostMapping(Uris.Users.LOGIN)
    fun login(@Valid @RequestBody input: UserCreateTokenInputModel): ResponseEntity<*> {
        return when (val res = userService.createToken(input.username, input.password)) {
            is Success -> {
                val cookieMaxAge: Long = res.value.tokenExpiration.epochSeconds - Clock.System.now().epochSeconds
                ResponseEntity.status(200)
                    .header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
                    .header(
                        "Set-Cookie",
                        "token=${res.value.tokenValue}; Max-Age=$cookieMaxAge; HttpOnly; SameSite=Strict; Path=/"
                    )
                    .header(
                        "Set-Cookie",
                        "login=${input.username}; Max-Age=$cookieMaxAge; SameSite=Strict; Path=/"
                    )
                    .body(
                        siren(UserTokenCreateOutputModel(res.value.tokenValue)) {
                            clazz("login")
                            link(Uris.Users.login(), Rels.SELF)
                            requireAuth(false)
                        }
                    )
            }

            is Failure -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.userOrPasswordAreInvalid(Uris.Users.login())
            }
        }
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        when (userService.revokeToken(authenticatedUser.token)) {
            is Success -> ResponseEntity.status(200)
                .header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
                .header(
                    "Set-Cookie",
                    "token=${authenticatedUser.token}; Max-Age=0; HttpOnly; SameSite=Strict; Path=/"
                )
                .header(
                    "Set-Cookie",
                    "login=${authenticatedUser.user.username}; Max-Age=0; SameSite=Strict; Path=/"
                )
                .body(
                    siren(
                        UserTokenRemoveOutputModel("Token ${authenticatedUser.token} revoked. Logout succeeded")
                    ) {
                        clazz("logout")
                        link(Uris.Users.logout(), Rels.SELF)
                        requireAuth(true)
                    }
                )

            is Failure -> Problem.tokenNotRevoked(Uris.Users.logout(), authenticatedUser.token)
        }

    @GetMapping(Uris.Users.GET_USER_BY_ID)
    fun getById(@PathVariable uid: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        when (val user = userService.getUserById(uid)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    UserGetByIdOutputModel(
                        user.value.id.value,
                        user.value.username,
                        user.value.email.value
                    )
                ) {
                    clazz("user")
                    link(Uris.Users.getUsersById(uid), Rels.SELF)
                    action(
                        "update-user",
                        Uris.Users.updateUser(),
                        HttpMethod.PUT,
                        "application/x-www-form-urlencoded"
                    ) {
                        textField("username")
                        textField("email")
                        textField("password")
                        requireAuth(true)
                    }
                    requireAuth(true)
                }
            )

            is Failure -> when (user.value) {
                UserGetByIdError.UserDoesNotExist -> Problem.userDoesNotExists(Uris.Users.getUsersById(uid), uid)
            }
        }

    @GetMapping(Uris.Users.AUTH_HOME)
    fun getAuthHome(authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
            siren(UserHomeOutputModel(authenticatedUser.user.id.value, authenticatedUser.user.username)) {
                clazz("user-home")
                link(Uris.Users.authHome(), Rels.SELF)
                link(Uris.systemInfo(), Rels.SYSTEM_INFO)
                link(URI(Uris.Users.RANKING_INFO + "?page=0"), Rels.RANKING_INFO)
                action(
                    "matchmaking",
                    Uris.Games.matchmaking(),
                    HttpMethod.POST,
                    "application/x-www-form-urlencoded"
                ) {
                    hiddenField("uid", authenticatedUser.user.id.value.toString())
                    textField("variant")
                    requireAuth(true)
                }
                action(
                    "logout",
                    Uris.Users.logout(),
                    HttpMethod.POST,
                    "application/json"
                ) {
                    requireAuth(true)
                }
                requireAuth(true)
            }
        )

    @GetMapping(Uris.Users.RANKING_INFO)
    fun getRankingInfo(@RequestParam(name = "page", defaultValue = "1") page: Int): ResponseEntity<*> =
        when (val res = userService.getRanking(PageValue(page))) {
            is Success -> ResponseEntity.ok()
                .header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
                .body(
                    siren(
                        RankingInfoOutputModel(
                            page,
                            res.value.pageSize
                        )
                    ) {
                        clazz("ranking-info")
                        link(URI(Uris.Users.RANKING_INFO + "?page=" + page), Rels.SELF)
                        res.value.content.forEach {
                            entity(
                                UserStatsOutputModel(
                                    it.user.id.value,
                                    it.user.username,
                                    it.gamesPlayed,
                                    it.wins,
                                    it.losses,
                                    it.rank,
                                    it.points
                                ),
                                Rels.USER_STATS
                            ) {
                                clazz("user-statistics")
                                link(Uris.Users.getStatsById(it.user.id.value), Rels.SELF)
                                requireAuth(false)
                            }
                        }
                        if (res.value.firstPage != null)
                            link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.firstPage), Rels.FIRST)

                        if (res.value.previousPage != null)
                            link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.previousPage), Rels.PREVIOUS)

                        if (res.value.nextPage != null)
                            link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.nextPage), Rels.NEXT)

                        if (res.value.lastPage != null)
                            link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.lastPage), Rels.LAST)

                        requireAuth(false)
                    }
                )

            is Failure -> Problem.invalidPageNumber(Uris.Users.rankingInfo(), page)
        }

    @GetMapping(Uris.Users.GET_STATS_BY_USERNAME_FOR_RANKING)
    fun getRankingByUsername(
        @PathVariable name: String,
        @RequestParam(name = "page", defaultValue = "1") page: Int
    ): ResponseEntity<*> {
        logger.info("getStatsByUsernameForRanking: $name, $page")
        return when (val res = userService.getStatsByUsernameForRanking(name, PageValue(page))) {
            is Success -> {
                logger.info("res {}", res.value.content)
                ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                    siren(
                        RankingInfoOutputModel(
                            page,
                            res.value.pageSize
                        )
                    ) {
                        clazz("ranking-info-by-username")
                        link(URI(Uris.Users.getStatsByUsernameForRanking(name).path + "?page=" + page), Rels.SELF)
                        res.value.content.forEach {
                            entity(
                                UserStatsOutputModel(
                                    it.user.id.value,
                                    it.user.username,
                                    it.gamesPlayed,
                                    it.wins,
                                    it.losses,
                                    it.rank,
                                    it.points
                                ),
                                Rels.USER_STATS
                            ) {
                                clazz("user-statistics")
                                link(Uris.Users.getStatsById(it.user.id.value), Rels.SELF)
                                requireAuth(false)
                            }
                        }
                        if (res.value.firstPage != null)
                            link(
                                URI(Uris.Users.getStatsByUsernameForRanking(name).path + "?page=" + res.value.firstPage),
                                Rels.FIRST
                            )

                        if (res.value.previousPage != null)
                            link(
                                URI(Uris.Users.getStatsByUsernameForRanking(name).path + "?page=" + res.value.previousPage),
                                Rels.PREVIOUS
                            )

                        if (res.value.nextPage != null)
                            link(
                                URI(Uris.Users.getStatsByUsernameForRanking(name).path + "?page=" + res.value.nextPage),
                                Rels.NEXT
                            )

                        if (res.value.lastPage != null)
                            link(
                                URI(Uris.Users.getStatsByUsernameForRanking(name).path + "?page=" + res.value.lastPage),
                                Rels.LAST
                            )

                        requireAuth(false)
                    }
                )

            }

            is Failure -> Problem.invalidPageNumber(Uris.Users.getStatsByUsernameForRanking(name), page)
        }
    }

    @GetMapping(Uris.Users.GET_STATS_BY_USERNAME)
    fun getStatsByUsername(@PathVariable name: String): ResponseEntity<*> =
        when (val stats = userService.getUserStatsByUsername(name)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    UserStatsOutputModel(
                        stats.value.user.id.value,
                        stats.value.user.username,
                        stats.value.gamesPlayed,
                        stats.value.wins,
                        stats.value.losses,
                        stats.value.rank,
                        stats.value.points
                    )
                ) {
                    clazz("user-statistics")
                    link(Uris.Users.getStatsByUsername(name), Rels.SELF)
                    link(Uris.Games.getAllGamesByUser(stats.value.user.id.value), Rels.GET_ALL_GAMES_BY_USER)
                    requireAuth(false)
                }
            )

            is Failure ->
                when (stats.value) {
                    UserStatsError.UserDoesNotExist -> Problem.userDoesNotExists(
                        Uris.Users.getStatsByUsername(name),
                        name
                    )

                    UserStatsError.UserStatsDoesNotExist -> Problem.statsNotFound(
                        Uris.Users.getStatsByUsername(name),
                        name
                    )
                }
        }

    @GetMapping(Uris.Users.GET_STATS_BY_ID)
    fun getStatsById(@PathVariable uid: Int): ResponseEntity<*> =
        when (val stats = userService.getUserStatsById(uid)) {
            is Success -> ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE).body(
                siren(
                    UserStatsOutputModel(
                        stats.value.user.id.value,
                        stats.value.user.username,
                        stats.value.gamesPlayed,
                        stats.value.wins,
                        stats.value.losses,
                        stats.value.rank,
                        stats.value.points
                    )
                ) {
                    clazz("user-statistics")
                    link(Uris.Users.getStatsById(uid), Rels.SELF)
                    link(Uris.Games.getAllGamesByUser(stats.value.user.id.value), Rels.GET_ALL_GAMES_BY_USER)
                    requireAuth(false)
                }
            )

            is Failure ->
                when (stats.value) {
                    UserStatsError.UserDoesNotExist -> Problem.userDoesNotExists(Uris.Users.getUsersById(uid), uid)
                    UserStatsError.UserStatsDoesNotExist -> Problem.statsNotFound(Uris.Users.getStatsById(uid), uid)
                }
        }

    @PutMapping(Uris.Users.UPDATE_USER)
    fun updateUser(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid input: UserUpdateInputModel
    ): ResponseEntity<*> {
        return when (val res =
            userService.updateUser(authenticatedUser.user.id.value, input.username, input.email, input.password)) {
            is Success -> ResponseEntity.ok().header(
                "Location",
                Uris.Users.getUsersById(authenticatedUser.user.id.value).toASCIIString()
            ).header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
                .body(
                    siren(UserUpdateOutputModel("User with id ${authenticatedUser.user.id.value} updated successfully")) {
                        clazz("update-user")
                        link(Uris.Users.updateUser(), Rels.SELF)
                        requireAuth(true)
                    }
                )

            is Failure -> when (res.value) {
                UserUpdateError.UserDoesNotExist -> Problem.userDoesNotExists(
                    Uris.Users.updateUser(),
                    authenticatedUser.user.id.value
                )

                UserUpdateError.InsecurePassword -> Problem.insecurePassword(
                    Uris.Users.updateUser()
                )

                UserUpdateError.InsecureEmail -> Problem.insecureEmail(
                    Uris.Users.updateUser()
                )
            }
        }
    }


    companion object {
        private val logger = LoggerFactory.getLogger(UsersController::class.java)
    }
}

