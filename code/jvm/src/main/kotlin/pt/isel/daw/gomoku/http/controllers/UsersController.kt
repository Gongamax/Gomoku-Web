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
                UserCreationError.InsecurePassword -> Problem(
                    typeUri = Problem.insecurePassword,
                    title = "Problem.insecurePassword",
                    status = 400,
                    detail = "Password is insecure",
                    instance = Uris.Users.register()
                ).toResponse()

                UserCreationError.UserAlreadyExists -> Problem(
                    typeUri = Problem.userAlreadyExists,
                    title = "Problem.userAlreadyExists",
                    status = 409,
                    detail = "User with username ${input.username} already exists",
                    instance = Uris.Users.register()
                ).toResponse()

                UserCreationError.InsecureEmail -> Problem(
                    typeUri = Problem.insecureEmail,
                    title = "Problem.insecureEmail",
                    status = 400,
                    detail = "Email is insecure",
                    instance = Uris.Users.register()
                ).toResponse()
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
                        siren(UserTokenCreateOutputModel(res.value.tokenValue)){
                            clazz("login")
                            link(Uris.Users.login(), Rels.SELF)
                            requireAuth(false)
                        }
                    )

            is Failure -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid ->
                    Problem(
                        typeUri = Problem.userOrPasswordAreInvalid,
                        title = "Problem.userOrPasswordAreInvalid",
                        status = 401,
                        detail = "User or password are invalid",
                        instance = Uris.Users.login()
                    ).toResponse()
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

            is Failure -> Problem(
                typeUri = Problem.tokenNotRevoked,
                title = "Problem.tokenNotRevoked",
                status = 400,
                detail = "Token ${authenticatedUser.token} not revoked",
                instance = Uris.Users.logout()
            ).toResponse()
        }

    @GetMapping(Uris.Users.GET_USER_BY_ID)
    fun getById(@PathVariable id: String, authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        when (val user = userService.getUserById(id.toInt())) {
            is Success -> ResponseEntity.ok(
                siren(UserGetByIdOutputModel(
                    user.value.id.value,
                    user.value.username,
                    user.value.email.value
                )){
                    clazz("user")
                    link(Uris.Users.getUsersById(id.toInt()), Rels.SELF)
                    action(
                        "update-user",
                        Uris.Users.updateUser(id.toInt()),
                        HttpMethod.PUT,
                        "application/x-www-form-urlencoded"
                    ){
                        textField("username")
                        textField("email")
                        textField("password")
                        requireAuth(true)
                    }
                    requireAuth(true)
                }
            )

            is Failure -> when (user.value) {
                UserGetByIdError.UserDoesNotExist -> Problem(
                    typeUri = Problem.userDoesNotExists,
                    title = "Problem.userDoesNotExists",
                    status = 404,
                    detail = "User with id $id does not exist",
                    instance = Uris.Users.getUsersById(id.toInt())
                ).toResponse()
            }
        }

    @GetMapping(Uris.Users.AUTH_HOME)
    fun getAuthHome(authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        ResponseEntity.ok(
            siren(UserHomeOutputModel(authenticatedUser.user.id.value, authenticatedUser.user.username)) {
                clazz("user-home")
                link(Uris.Users.authHome(), Rels.SELF)
                link(Uris.systemInfo(), Rels.SYSTEM_INFO)
                link(URI(Uris.Users.RANKING_INFO + "?page=0" ), Rels.RANKING_INFO)
                action(
                    "matchmaking",
                    Uris.Games.matchmaking(),
                    HttpMethod.POST,
                    "application/x-www-form-urlencoded"
                ){
                        hiddenField("userId", authenticatedUser.user.id.value.toString())
                        textField("variant")
                        requireAuth(true)
                }
                action("logout",
                    Uris.Users.logout(),
                    HttpMethod.POST,
                    "application/json") {
                        requireAuth(true)
                    }
                requireAuth(true)
            }
        )
    @GetMapping(Uris.Users.RANKING_INFO)
    fun getRankingInfo(@RequestParam page: String): ResponseEntity<*> =
        when (val res = userService.getRanking(PositiveValue(page.toInt()))) {
            is Success -> ResponseEntity.ok(
                siren(RankingInfoOutputModel(res.value.content)
                ){
                    clazz("ranking-info")
                    link(URI(Uris.Users.RANKING_INFO + "?page=" + page), Rels.SELF)
                    entity(
                        "{id, username, gamesPlayed, wins, losses, rank, points}",
                        Rels.USER_STATS
                    ) {
                        clazz("user-statistics")
                        requireAuth(false)
                    }
                    if(res.value.firstPage != null)
                        link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.firstPage), Rels.FIRST)

                    if(res.value.previousPage != null)
                        link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.previousPage), Rels.PREVIOUS)

                    if(res.value.nextPage != null)
                        link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.nextPage), Rels.NEXT)

                    link(URI(Uris.Users.RANKING_INFO + "?page=" + res.value.lastPage), Rels.LAST)

                    requireAuth(false)
                }
            )
            is Failure -> Problem(
                typeUri = Problem.rankingNotFound,
                title = "Problem.rankingNotFound",
                status = 404,
                detail = "Ranking not found",
                instance = URI(Uris.Users.RANKING_INFO + "?page=" + page)
            ).toResponse()
        }


    @GetMapping(Uris.Users.GET_STATS_BY_ID)
    fun getStatsById(@PathVariable id: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        when (val stats = userService.getUserStatsById(id)) {
            is Success -> ResponseEntity.ok(
                siren(UserStatsOutputModel(
                    stats.value.user.id.value,
                    stats.value.user.username,
                    stats.value.gamesPlayed,
                    stats.value.wins,
                    stats.value.losses,
                    stats.value.rank,
                    stats.value.points
                )){
                    clazz("user-statistics")
                    link(Uris.Users.getStatsById(id), Rels.SELF)
                    entity(
                        "[ {gid, board, playerBlack, playerWhite, state, variant, created}, ... ]",
                        Rels.GET_ALL_GAMES_BY_USER
                    ){
                        clazz("game-list-of-user")
                        link(Uris.Games.getAllGamesByUser(id), Rels.SELF)
                        requireAuth(true)
                    }
                    requireAuth(true)
                }
            )

            is Failure ->
                when (stats.value) {
                    UserStatsError.UserDoesNotExist -> Problem(
                        typeUri = Problem.userDoesNotExists,
                        title = "Problem.userDoesNotExists",
                        status = 404,
                        detail = "User with id $id does not exist",
                        instance = Uris.Users.getUsersById(id)
                    ).toResponse()

                    UserStatsError.UserStatsDoesNotExist -> Problem(
                        typeUri = Problem.statsNotFound,
                        title = "Problem.statsNotFound",
                        status = 404,
                        detail = "Stats for user with id $id does not exist",
                        instance = Uris.Users.getStatsById(id)
                    ).toResponse()
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
                siren( UserUpdateOutputModel("User with id ${authenticatedUser.user.id.value} updated successfully")){
                    clazz("update-user")
                    link(Uris.Users.updateUser(authenticatedUser.user.id.value), Rels.SELF)
                    requireAuth(true)
                }
            )

            is Failure -> when (res.value) {
                UserUpdateError.UserDoesNotExist -> Problem(
                    typeUri = Problem.userDoesNotExists,
                    title = "Problem.userDoesNotExists",
                    status = 404,
                    detail = "User with id ${authenticatedUser.user.id.value} does not exist",
                    instance = Uris.Users.getUsersById(authenticatedUser.user.id.value)
                ).toResponse()

                UserUpdateError.InsecurePassword -> Problem(
                    typeUri = Problem.insecurePassword,
                    title = "Problem.insecurePassword",
                    status = 400,
                    detail = "Password is insecure",
                    instance = Uris.Users.updateUser(authenticatedUser.user.id.value)
                ).toResponse()

                UserUpdateError.InsecureEmail -> Problem(
                    typeUri = Problem.insecureEmail,
                    title = "Problem.insecureEmail",
                    status = 400,
                    detail = "Email is insecure",
                    instance = Uris.Users.updateUser(authenticatedUser.user.id.value)
                ).toResponse()
            }
        }
    }

    //TODO: MAKE FUNCTION TO GET USER STATS BY NAME
}

