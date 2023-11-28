package pt.isel.daw.gomoku.http.media

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    val typeUri: URI,
    val title: String = "",
    val status: Int = 500,
    val detail: String = "",
    val instance: URI? = null
) {

    fun toResponse() = ResponseEntity
        .status(status)
        .header("Content-Type", MEDIA_TYPE)
        .body<Any>(this)

    @Suppress("unused")
    val problemMediaType = MediaType(MEDIA_TYPE)

    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        private const val BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/problems/"

        // Bad request, insecure
        val insecurePassword = URI(BASE_URL + "insecure-password")
        val insecureEmail = URI(BASE_URL + "insecure-email")

        // Bad request
        val userOrPasswordAreInvalid = URI(BASE_URL + "user-or-password-are-invalid")
        val invalidRequestContent = URI(BASE_URL + "invalid-request-content")
        val invalidUser = URI(BASE_URL + "invalid-user")
        val invalidState = URI(BASE_URL + "invalid-state")
        val invalidTime = URI(BASE_URL + "invalid-time")
        val invalidTurn = URI(BASE_URL + "invalid-turn")
        val invalidPosition = URI(BASE_URL + "invalid-position")
        val invalidToken = URI(BASE_URL + "invalid-token")

        // Conflict, already exists
        val userAlreadyExists = URI(BASE_URL + "user-already-exists")
        val gameAlreadyExists = URI(BASE_URL + "game-already-exists")
        val userDoesNotExists = URI(BASE_URL + "user-does-not-exists")
        val variantDoesNotExists = URI(BASE_URL + "variant-does-not-exists")
        val internalServerError = URI(BASE_URL + "internal-server-error")
        val gameDoesNotExists = URI(BASE_URL + "game-does-not-exists")
        val gameAlreadyEnded = URI(BASE_URL + "game-already-ended")

        // Unauthorized
        val tokenExpired = URI(BASE_URL + "token-expired")
        val userIsNotAuthenticated = URI(BASE_URL + "user-is-not-authenticated")
        val tokenNotRevoked = URI(BASE_URL + "token-not-revoked")

        // Not found
        val matchNotFound = URI(BASE_URL + "match-not-found")
        val rankingNotFound = URI(BASE_URL + "ranking-not-found")
        val statsNotFound = URI(BASE_URL + "stats-not-found")
        val gamesNotFound: URI = URI(BASE_URL + "games-not-found")
    }

    fun insecurePassword() = Problem(
        typeUri = insecurePassword,
        title = "Problem.insecurePassword",
        status = 400,
        detail = "Password is insecure",
        instance = null
    ).toResponse()

    fun insecureEmail() = Problem(
        typeUri = insecureEmail,
        title = "Problem.insecureEmail",
        status = 400,
        detail = "Email is insecure",
        instance = null
    ).toResponse()

    fun userOrPasswordAreInvalid() = Problem(
        typeUri = userOrPasswordAreInvalid,
        title = "Problem.userOrPasswordAreInvalid",
        status = 401,
        detail = "User or password are invalid",
        instance = null
    ).toResponse()

    fun invalidRequestContent() = Problem(
        typeUri = invalidRequestContent,
        title = "Problem.invalidRequestContent",
        status = 400,
        detail = "Invalid request content",
        instance = null
    ).toResponse()

    fun invalidToken() = Problem(
        typeUri = invalidToken,
        title = "Problem.invalidToken",
        status = 400,
        detail = "Invalid token",
        instance = null
    ).toResponse()

    fun tokenExpired() = Problem(
        typeUri = tokenExpired,
        title = "Problem.tokenExpired",
        status = 401,
        detail = "Token expired",
        instance = null
    ).toResponse()

    fun tokenNotRevoked() = Problem(
        typeUri = tokenNotRevoked,
        title = "Problem.tokenNotRevoked",
        status = 400,
        detail = "Token not revoked",
        instance = null
    ).toResponse()

    fun matchNotFound() = Problem(
        typeUri = matchNotFound,
        title = "Problem.matchNotFound",
        status = 404,
        detail = "Match not found",
        instance = null
    ).toResponse()

    fun rankingNotFound() = Problem(
        typeUri = rankingNotFound,
        title = "Problem.rankingNotFound",
        status = 404,
        detail = "Ranking not found",
        instance = null
    ).toResponse()

    fun statsNotFound() = Problem(
        typeUri = statsNotFound,
        title = "Problem.statsNotFound",
        status = 404,
        detail = "Stats not found",
        instance = null
    ).toResponse()

    fun internalServerError() = Problem(
        typeUri = internalServerError,
        title = "Problem.internalServerError",
        status = 500,
        detail = "Internal server error",
        instance = null
    ).toResponse()

    fun gameAlreadyEnded() = Problem(
        typeUri = gameAlreadyEnded,
        title = "Problem.gameAlreadyEnded",
        status = 409,
        detail = "Game already ended",
        instance = null
    ).toResponse()

    fun gameDoesNotExists() = Problem(
        typeUri = gameDoesNotExists,
        title = "Problem.gameDoesNotExists",
        status = 404,
        detail = "Game does not exists",
        instance = null
    ).toResponse()

    fun variantDoesNotExists() = Problem(
        typeUri = variantDoesNotExists,
        title = "Problem.variantDoesNotExists",
        status = 404,
        detail = "Variant does not exists",
        instance = null
    ).toResponse()

    fun userDoesNotExists() = Problem(
        typeUri = userDoesNotExists,
        title = "Problem.userDoesNotExists",
        status = 404,
        detail = "User does not exists",
        instance = null
    ).toResponse()

    fun gameAlreadyExists() = Problem(
        typeUri = gameAlreadyExists,
        title = "Problem.gameAlreadyExists",
        status = 409,
        detail = "Game already exists",
        instance = null
    ).toResponse()

    fun userAlreadyExists() = Problem(
        typeUri = userAlreadyExists,
        title = "Problem.userAlreadyExists",
        status = 409,
        detail = "User already exists",
        instance = null
    ).toResponse()

    fun invalidPosition() = Problem(
        typeUri = invalidPosition,
        title = "Problem.invalidPosition",
        status = 400,
        detail = "Invalid position",
        instance = null
    ).toResponse()

    fun invalidTurn() = Problem(
        typeUri = invalidTurn,
        title = "Problem.invalidTurn",
        status = 400,
        detail = "Invalid turn",
        instance = null
    ).toResponse()

    fun invalidTime() = Problem(
        typeUri = invalidTime,
        title = "Problem.invalidTime",
        status = 400,
        detail = "Invalid time",
        instance = null
    ).toResponse()

    fun invalidState() = Problem(
        typeUri = invalidState,
        title = "Problem.invalidState",
        status = 400,
        detail = "Invalid state",
        instance = null
    ).toResponse()

    //TODO: continue for all
}