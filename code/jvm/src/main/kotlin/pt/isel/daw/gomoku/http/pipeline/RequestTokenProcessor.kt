package pt.isel.daw.gomoku.http.pipeline

import jakarta.servlet.http.Cookie
import org.springframework.stereotype.Component
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.services.users.UsersService
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success

@Component
class RequestTokenProcessor(
    val usersService: UsersService
) {

    fun processAuthorizationHeaderValue(authorizationValue: String?): AuthenticatedUser? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }
        val user = when (val result = usersService.getUserByToken(parts[1])) {
            is Success -> result.value
            is Failure -> null
        }
        return user?.let {
            AuthenticatedUser(
                it,
                parts[1]
            )
        }
    }

    fun processAuthorizationCookieValue(cookie: Cookie): AuthenticatedUser? {
        val user = when (val result = usersService.getUserByToken(cookie.value)) {
            is Success -> result.value
            is Failure -> null
        }
        return user?.let {
            AuthenticatedUser(
                it,
                cookie.value
            )
        }
    }

    companion object {
        const val SCHEME = "bearer"
    }
}
