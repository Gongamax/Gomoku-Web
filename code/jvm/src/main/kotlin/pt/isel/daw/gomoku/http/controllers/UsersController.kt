package pt.isel.daw.gomoku.http.controllers

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.http.assemblers.*
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Uris
import pt.isel.daw.gomoku.services.users.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success


@RestController
class UsersController(
    private val userService: UsersService,
    private val userModelAssembler: UserModelAssembler
) {
    @PostMapping(Uris.Users.CREATE_USER)
    fun create(@RequestBody @Valid input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.email, input.password)
        println(res)
        return when (res) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.Users.getUsersById(res.value).toASCIIString()
                ).build<Unit>()

            is Failure -> when (res.value) {
                UserCreationError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                UserCreationError.UserAlreadyExists -> Problem.response(400, Problem.userAlreadyExists)
                UserCreationError.InsecureEmail -> Problem.response(400, Problem.insecureEmail)
            }
        }
    }

    @PostMapping(Uris.Users.TOKEN)
    fun login(
       @Valid @RequestBody input: UserCreateTokenInputModel
    ): ResponseEntity<*> {
        return when (val res = userService.createToken(input.username, input.password)) {
            is Success ->
                ResponseEntity.status(200)
                    .body(UserTokenCreateOutputModel(res.value.tokenValue))

            is Failure -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid ->
                    Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(Uris.Users.LOGOUT)
    fun logout(user: AuthenticatedUser): ResponseEntity<*> =
        when (userService.revokeToken(user.token)) {
            is Success -> ResponseEntity.ok(UserTokenRemoveOutputModel("Token ${user.token} revoked. Logout succeeded"))
            is Failure -> Problem.response(400, Problem.tokenNotRevoked)
        }

    @GetMapping(Uris.Users.GET_USER_BY_ID)
    fun getById(@PathVariable id: String): ResponseEntity<*> {
        return when (val user = userService.getUserById(id.toInt())) {
            is Success -> {
                ResponseEntity.ok(userModelAssembler.getUserModelAssembler.toModel(
                    UserGetByIdOutputModel(
                        user.value.id.value,
                        user.value.username,
                        user.value.email.value
                    )
                ))
            }

            is Failure -> when (user.value) {
                UserGetError.UserDoesNotExist -> Problem.response(404, Problem.userDoesNotExists)
                UserGetError.InvalidToken -> Problem.response(400, Problem.invalidToken)
                UserGetError.TokenExpired -> Problem.response(400, Problem.tokenExpired)
                UserGetError.UserIsNotAuthenticated -> Problem.response(400, Problem.userIsNotAuthenticated)
            }
        }
    }

    @GetMapping(Uris.Users.AUTH_HOME)
    fun getAuthHome(userAuthenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        ResponseEntity.ok(UserHomeOutputModel(userAuthenticatedUser.user.id.value, userAuthenticatedUser.user.username))

    @GetMapping(Uris.Users.RANKING_INFO)
    fun getRankingInfo(): ResponseEntity<*> =
        when (val res = userService.getRanking()) {
            is Success -> ResponseEntity.ok(userModelAssembler.getRankingInfoModelAssembler.toModel(RankingInfoOutputModel(res.value)))
            is Failure ->  Problem.response(404, Problem.rankingNotFound)
        }


    @GetMapping(Uris.Users.GET_STATS_BY_ID)
    fun getStatsById(@PathVariable id: String, user: AuthenticatedUser): ResponseEntity<*> =
        when (val stats = userService.getUserStatsById(id.toInt())) {
            is Success -> ResponseEntity.ok(
                StatsGetByIdOutputModel(
                    stats.value.user.id.value,
                    stats.value.user.username,
                    stats.value.gamesPlayed,
                    stats.value.wins,
                    stats.value.losses,
                    stats.value.rank,
                    stats.value.points
                )
            )

            is Failure ->
                when (stats.value) {
                    UserStatsError.UserDoesNotExist -> Problem.response(400, Problem.userDoesNotExists)
                    UserStatsError.UserStatsDoesNotExist -> Problem.response(404, Problem.statsNotFound)
                }
        }

}

