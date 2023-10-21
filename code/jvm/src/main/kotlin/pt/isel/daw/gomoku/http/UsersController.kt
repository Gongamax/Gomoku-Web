package pt.isel.daw.gomoku.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.services.users.TokenCreationError
import pt.isel.daw.gomoku.services.users.UserCreationError
import pt.isel.daw.gomoku.services.users.UserGetError
import pt.isel.daw.gomoku.services.users.UsersService
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success

@RestController
class UsersController(
    private val userService: UsersService
) {
    @PostMapping(Uris.Users.CREATE_USER)
    fun create(@RequestBody input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.email, input.password)
        println(res)
        return when (res) {
            is Success -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.Users.byId(res.value).toASCIIString()
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
        @RequestBody input: UserCreateTokenInputModel
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
    fun logout(user: AuthenticatedUser) {
        userService.revokeToken(user.token)
    }

    @GetMapping(Uris.Users.GET_USER_BY_ID)
    fun getById(@PathVariable id: String): ResponseEntity<*> {
        return when (val user = userService.getUserById(id.toInt())) {
            is Success -> ResponseEntity.ok(UserGetByIdOutputModel(user.value.id.value, user.value.username, user.value.email.value))
            is Failure -> when (user.value) {
                UserGetError.UserDoesNotExist -> Problem.response(404, Problem.userDoesNotExists)
                UserGetError.InvalidToken -> Problem.response(400, Problem.invalidToken)
                UserGetError.TokenExpired -> Problem.response(400, Problem.tokenExpired)
                UserGetError.UserIsNotAuthenticated -> Problem.response(400, Problem.userIsNotAuthenticated)
            }
        }
    }

    @GetMapping(Uris.Users.HOME)
    fun getUserHome(userAuthenticatedUser: AuthenticatedUser): UserHomeOutputModel {
        return UserHomeOutputModel(
            id = userAuthenticatedUser.user.id.value,
            username = userAuthenticatedUser.user.username
        )
    }
}
