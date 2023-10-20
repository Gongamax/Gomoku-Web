package pt.isel.daw.gomoku.http.model

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    typeUri: URI
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val userAlreadyExists = Problem(
            URI(
                "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/code/tic-tac-tow-service/" +
                        "docs/problems/user-already-exists"
            )
        )
        val insecurePassword = Problem(
            URI(
                "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/" +
                        "docs/problems/insecure-password"
            )
        )

        val userOrPasswordAreInvalid = Problem(
            URI(
                "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/" +
                        "docs/problems/user-or-password-are-invalid"
            )
        )

        val invalidRequestContent = Problem(
            URI(
                "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main" +
                        "docs/problems/invalid-request-content"
            )
        )

        val gameAlreadyExists = Problem(
            URI(
                "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/problems/game-already-exists"
            )
        )

        val userDoesNotExists = Problem(
            URI(
                "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/problems/user-does-not-exists"
            )
        )
    }
}