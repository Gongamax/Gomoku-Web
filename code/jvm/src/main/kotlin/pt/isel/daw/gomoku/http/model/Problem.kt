package pt.isel.daw.gomoku.http.model

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    typeUri: URI
) {
    val type = typeUri.toASCIIString()

    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        private const val BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/problems/"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val userAlreadyExists = Problem(URI(BASE_URL + "user-already-exists"))
        val insecurePassword = Problem(URI(BASE_URL + "insecure-password"))
        val insecureEmail = Problem(URI(BASE_URL + "insecure-email"))
        val userOrPasswordAreInvalid = Problem(URI(BASE_URL + "user-or-password-are-invalid"))
        val invalidRequestContent = Problem(URI(BASE_URL + "invalid-request-content"))
        val gameAlreadyExists = Problem(URI(BASE_URL + "game-already-exists"))
        val userDoesNotExists = Problem(URI(BASE_URL + "user-does-not-exists"))
        val variantDoesNotExists = Problem(URI(BASE_URL + "variant-does-not-exists"))
        val internalServerError = Problem(URI(BASE_URL + "internal-server-error"))
        val gameDoesNotExists = Problem(URI(BASE_URL + "game-does-not-exists"))
        val invalidUser = Problem(URI(BASE_URL + "invalid-user"))
        val invalidState = Problem(URI(BASE_URL + "invalid-state"))
        val invalidTime = Problem(URI(BASE_URL + "invalid-time"))
        val invalidTurn = Problem(URI(BASE_URL + "invalid-turn"))
        val invalidPosition = Problem(URI(BASE_URL + "invalid-position"))
        val gameAlreadyEnded = Problem(URI(BASE_URL + "game-already-ended"))
        val invalidToken = Problem(URI(BASE_URL + "invalid-token"))
        val tokenExpired = Problem(URI(BASE_URL + "token-expired"))
        val userIsNotAuthenticated = Problem(URI(BASE_URL + "user-is-not-authenticated"))

    }
}