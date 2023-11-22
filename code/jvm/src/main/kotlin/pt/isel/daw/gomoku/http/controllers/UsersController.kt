package pt.isel.daw.gomoku.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Uris
import pt.isel.daw.gomoku.services.users.*
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success

@RestController
class UsersController(
    private val userService: UsersService
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
    fun logout(user: AuthenticatedUser): ResponseEntity<*> =
        when (userService.revokeToken(user.token)) {
            is Success -> ResponseEntity.ok(UserTokenRemoveOutputModel("Token ${user.token} revoked. Logout succeeded"))
            is Failure -> Problem(
                typeUri = Problem.tokenNotRevoked,
                title = "Problem.tokenNotRevoked",
                status = 400,
                detail = "Token ${user.token} not revoked",
                instance = Uris.Users.logout()
            ).toResponse()
        }

    @GetMapping(Uris.Users.GET_USER_BY_ID)
    fun getById(@PathVariable id: String): ResponseEntity<*> {
        return when (val user = userService.getUserById(id.toInt())) {
            is Success -> ResponseEntity.ok(
                UserGetByIdOutputModel(
                    user.value.id.value,
                    user.value.username,
                    user.value.email.value
                )
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
    }

    @GetMapping(Uris.Users.AUTH_HOME)
    fun getAuthHome(userAuthenticatedUser: AuthenticatedUser): ResponseEntity<*> =
        ResponseEntity.ok(UserHomeOutputModel(userAuthenticatedUser.user.id.value, userAuthenticatedUser.user.username))

    @GetMapping(Uris.Users.RANKING_INFO)
    fun getRankingInfo(): ResponseEntity<*> =
        when (val res = userService.getRanking()) {
            is Success -> ResponseEntity.ok(RankingInfoOutputModel(res.value))
            is Failure -> Problem(
                typeUri = Problem.rankingNotFound,
                title = "Problem.rankingNotFound",
                status = 404,
                detail = "Ranking not found",
                instance = Uris.Users.rankingInfo()
            ).toResponse()
        }


    @GetMapping(Uris.Users.GET_STATS_BY_ID)
    fun getStatsById(@PathVariable id: String, user: AuthenticatedUser): ResponseEntity<*> =
        when (val stats = userService.getUserStatsById(id.toInt())) {
            is Success -> ResponseEntity.ok(
                UserStatsOutputModel(
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
                    UserStatsError.UserDoesNotExist -> Problem(
                        typeUri = Problem.userDoesNotExists,
                        title = "Problem.userDoesNotExists",
                        status = 404,
                        detail = "User with id $id does not exist",
                        instance = Uris.Users.getUsersById(id.toInt())
                    ).toResponse()

                    UserStatsError.UserStatsDoesNotExist -> Problem(
                        typeUri = Problem.statsNotFound,
                        title = "Problem.statsNotFound",
                        status = 404,
                        detail = "Stats for user with id $id does not exist",
                        instance = Uris.Users.getStatsById(id.toInt())
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
                UserUpdateOutputModel("User with id ${authenticatedUser.user.id.value} updated successfully")
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
}

