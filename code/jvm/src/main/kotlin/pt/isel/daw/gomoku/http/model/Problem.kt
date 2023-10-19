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
        val userOrPasswordAreInvalid = Problem(URI(BASE_URL + "user-or-password-are-invalid"))
        val invalidRequestContent = Problem(URI(BASE_URL + "invalid-request-content"))
        val gameAlreadyExists = Problem(URI(BASE_URL + "game-already-exists"))
        val userDoesNotExists = Problem(URI(BASE_URL + "user-does-not-exists"))
    }
}