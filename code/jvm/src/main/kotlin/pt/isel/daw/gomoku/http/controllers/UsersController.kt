package pt.isel.daw.gomoku.http.controllers

import jakarta.validation.Valid
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.media.Problem
import pt.isel.daw.gomoku.http.media.siren.siren
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Rels
import pt.isel.daw.gomoku.http.util.Uris
import pt.isel.daw.gomoku.services.users.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.PositiveValue
import pt.isel.daw.gomoku.utils.Success
import java.net.URI


@RestController
class UsersController(
    private val userService: UsersService
) {
    @PostMapping(Uris.Users.REGISTER)
    fun create(@RequestBody @Valid input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.email, input.password)
        println(res)
        return when (res) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.Users.getUsersById(res.value).toASCIIString()
                ).body(
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
    fun login(
        @Valid @RequestBody input: UserCreateTokenInputModel
    ): ResponseEntity<*> {
        return when (val res = userService.createToken(input.username, input.password)) {
            is Success ->
                ResponseEntity.status(200)
                    .body(
                        siren(UserTokenCreateOutputModel(res.value.tokenValue)) {
                            clazz("login")
                            link(Uris.Users.login(), Rels.SELF)
                            requireAuth(false)
                        }
                    )

            is Failure -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.userOrPasswordAreInvalid(Uris.Users.login())
            }
        }
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        when (userService.revokeToken(authenticatedUser.token)) {
            is Success -> ResponseEntity.ok(
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
            is Success -> ResponseEntity.ok(
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
        ResponseEntity.ok(
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
                    hiddenField("userId", authenticatedUser.user.id.value.toString())
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
    fun getRankingInfo(@RequestParam page: String): ResponseEntity<*> =
        when (val res = userService.getRanking(PositiveValue(page.toInt()))) {
            is Success -> ResponseEntity.ok(
                siren(
                    RankingInfoOutputModel(
                        page.toInt(),
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

                    link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.lastPage), Rels.LAST)

                    requireAuth(false)
                }
            )

            is Failure -> Problem.rankingNotFound(Uris.Users.rankingInfo())
        }

    @GetMapping(Uris.Users.GET_STATS_BY_USERNAME)
    fun getStatsByUsername(@RequestParam username: String): ResponseEntity<*> =
        when (val stats = userService.getUserStatsByUsername(username)) {
            is Success -> ResponseEntity.ok(
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
                    link(URI(Uris.Users.GET_STATS_BY_USERNAME + "?username=" + username), Rels.SELF)
                    link(Uris.Games.getAllGamesByUser(stats.value.user.id.value), Rels.GET_ALL_GAMES_BY_USER)
                    requireAuth(false)
                }
            )

            is Failure ->
                when (stats.value) {
                    UserStatsError.UserDoesNotExist -> Problem.userDoesNotExists(
                        URI(Uris.Users.GET_STATS_BY_USERNAME + "?username=" + username),
                        username
                    )

                    UserStatsError.UserStatsDoesNotExist -> Problem.statsNotFound(
                        URI(Uris.Users.GET_STATS_BY_USERNAME + "?username=" + username),
                        username
                    )
                }
        }


    @GetMapping(Uris.Users.GET_STATS_BY_ID)
    fun getStatsById(@PathVariable uid: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        when (val stats = userService.getUserStatsById(uid)) {
            is Success -> ResponseEntity.ok(
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
            ).body(
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
}

