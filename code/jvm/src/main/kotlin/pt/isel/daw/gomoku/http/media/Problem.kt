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
        val invalidPageNumber = URI(BASE_URL + "invalid-page-number")

        // Conflict, already exists
        val userAlreadyExists = URI(BASE_URL + "user-already-exists")
        val gameAlreadyExists = URI(BASE_URL + "game-already-exists")
        val userDoesNotExists = URI(BASE_URL + "user-does-not-exists")
        val variantDoesNotExists = URI(BASE_URL + "variant-does-not-exists")
        val internalServerError = URI(BASE_URL + "internal-server-error")
        val gameDoesNotExists = URI(BASE_URL + "game-does-not-exists")
        val gameAlreadyEnded = URI(BASE_URL + "game-already-ended")
        val userAlreadyInQueue = URI(BASE_URL + "user-already-in-queue")

        // Unauthorized
        val tokenExpired = URI(BASE_URL + "token-expired")
        val userIsNotAuthenticated = URI(BASE_URL + "user-is-not-authenticated")
        val tokenNotRevoked = URI(BASE_URL + "token-not-revoked")

        // Not found
        val matchNotFound = URI(BASE_URL + "match-not-found")
        val rankingNotFound = URI(BASE_URL + "ranking-not-found")
        val statsNotFound = URI(BASE_URL + "stats-not-found")
        val gamesNotFound: URI = URI(BASE_URL + "games-not-found")
        val variantsNotFound = URI(BASE_URL + "variants-not-found")

        fun insecurePassword(instance: URI?) = Problem(
            typeUri = insecurePassword,
            title = "Problem.insecurePassword",
            status = 422,
            detail = "Password is insecure",
            instance = instance
        ).toResponse()

        fun insecureEmail(instance: URI?) = Problem(
            typeUri = insecureEmail,
            title = "Problem.insecureEmail",
            status = 422,
            detail = "Email is insecure",
            instance = instance
        ).toResponse()

        fun userOrPasswordAreInvalid(instance: URI?) = Problem(
            typeUri = userOrPasswordAreInvalid,
            title = "Problem.userOrPasswordAreInvalid",
            status = 401,
            detail = "User or password are invalid",
            instance = instance
        ).toResponse()

        fun invalidRequestContent(instance: URI?) = Problem(
            typeUri = invalidRequestContent,
            title = "Problem.invalidRequestContent",
            status = 400,
            detail = "Invalid request content",
            instance = instance
        ).toResponse()

        fun invalidToken(instance: URI?) = Problem(
            typeUri = invalidToken,
            title = "Problem.invalidToken",
            status = 400,
            detail = "Invalid token",
            instance = instance
        ).toResponse()

        fun tokenExpired(instance: URI?) = Problem(
            typeUri = tokenExpired,
            title = "Problem.tokenExpired",
            status = 401,
            detail = "Token expired",
            instance = instance
        ).toResponse()

        fun tokenNotRevoked(instance: URI?, token : String) = Problem(
            typeUri = tokenNotRevoked,
            title = "Token not revoked",
            status = 400,
            detail = "Token $token not revoked",
            instance = instance
        ).toResponse()

        fun matchNotFound(instance: URI?, matchEntryId: Int) = Problem(
            typeUri = matchNotFound,
            title = "Match not found",
            status = 404,
            detail = "Match entry with id $matchEntryId does not exist",
            instance = instance
        ).toResponse()

        fun rankingNotFound(instance: URI?) = Problem(
            typeUri = rankingNotFound,
            title = "Problem.rankingNotFound",
            status = 404,
            detail = "Ranking not found",
            instance = instance
        ).toResponse()

        fun statsNotFound(instance: URI?, uid : Int) = Problem(
            typeUri = statsNotFound,
            title = "Stats not found",
            status = 404,
            detail = "Stats for user with id $uid does not exist",
            instance = instance
        ).toResponse()

        fun statsNotFound(instance: URI?, username : String) = Problem(
            typeUri = statsNotFound,
            title = "Stats not found",
            status = 404,
            detail = "Stats for user with username $username does not exist",
            instance = instance
        ).toResponse()

        fun internalServerError(instance: URI?) = Problem(
            typeUri = internalServerError,
            title = "Problem.internalServerError",
            status = 500,
            detail = "Internal server error",
            instance = instance
        ).toResponse()

        fun gameAlreadyEnded(instance: URI?, gid : Int) = Problem(
            typeUri = gameAlreadyEnded,
            title = "Game Already Ended",
            status = 409,
            detail = "Game with id $gid already ended",
            instance = instance
        ).toResponse()

        fun gameDoesNotExists(instance: URI?, gid : Int) = Problem(
            typeUri = gameDoesNotExists,
            title = "Game does not exist",
            status = 404,
            detail = "Game with id $gid does not exist",
            instance = instance
        ).toResponse()

        fun variantDoesNotExists(instance: URI?, variantName: String) = Problem(
            typeUri = variantDoesNotExists,
            title = "Variant does not exist",
            status = 404,
            detail = "Variant $variantName does not exist",
            instance = instance
        ).toResponse()

        fun userDoesNotExists(instance: URI?, uid : Int) = Problem(
            typeUri = userDoesNotExists,
            title = "User does not exist",
            status = 404,
            detail = "User with id $uid does not exist",
            instance = instance
        ).toResponse()

        fun userDoesNotExists(instance: URI?, username : String) = Problem(
            typeUri = userDoesNotExists,
            title = "User does not exist",
            status = 404,
            detail = "User with username $username does not exist",
            instance = instance
        ).toResponse()

        fun userIsNotAuthenticated(instance: URI?) = Problem(
            typeUri = userIsNotAuthenticated,
            title = "Problem.userIsNotAuthenticated",
            status = 401,
            detail = "User is not authenticated",
            instance = instance
        ).toResponse()

        fun gameAlreadyExists(instance: URI?) = Problem(
            typeUri = gameAlreadyExists,
            title = "Problem.gameAlreadyExists",
            status = 409,
            detail = "Game already exists",
            instance = instance
        ).toResponse()

        fun userAlreadyExists(instance: URI?, name : String) = Problem(
            typeUri = userAlreadyExists,
            title = "Problem.userAlreadyExists",
            status = 409,
            detail = "User with username $name already exists",
            instance = instance
        ).toResponse()

        fun invalidPosition(instance: URI?, gid: Int) = Problem(
            typeUri = invalidPosition,
            title = "Problem.invalidPosition",
            status = 400,
            detail = "Unplayable position on game with id $gid",
            instance = instance
        ).toResponse()

        fun invalidTurn(instance: URI?, gid : Int) = Problem(
            typeUri = invalidTurn,
            title = "Invalid Turn",
            status = 400,
            detail = "Game with id $gid is not in the correct turn to play",
            instance = instance
        ).toResponse()

        fun invalidTime(instance: URI?, gid: Int) = Problem(
            typeUri = invalidTime,
            title = "Invalid Time",
            status = 400,
            detail = "Game with id $gid is not in the correct time to play",
            instance = instance
        ).toResponse()

        fun invalidState(instance: URI?, gid : Int) = Problem(
            typeUri = invalidState,
            title = "Invalid State",
            status = 409,
            detail = "Game with id $gid is not in the correct state to play",
            instance = instance
        ).toResponse()

        fun invalidUser(instance: URI?, userId : Int) = Problem(
            typeUri = invalidUser,
            title = "Invalid User",
            status = 401,
            detail = "User with id $userId does not exist",
            instance = instance
        ).toResponse()

        fun invalidPageNumber(instance: URI?, pageNumber : Int) = Problem(
            typeUri = invalidPageNumber,
            title = "Invalid Page Number",
            status = 400,
            detail = "Page number $pageNumber is invalid",
            instance = instance
        ).toResponse()

        fun gamesNotFound(instance: URI?) = Problem(
            typeUri = gamesNotFound,
            title = "Games not found",
            status = 404,
            detail = "Games not found",
            instance = instance
        ).toResponse()

        fun variantsNotFound(instance: URI?) = Problem(
            typeUri = variantsNotFound,
            title = "Variants not found",
            status = 404,
            detail = "Variants not found",
            instance = instance
        ).toResponse()

        fun userAlreadyInQueue(instance: URI?, uid : Int) = Problem(
            typeUri = userAlreadyInQueue,
            title = "User already in queue",
            status = 409,
            detail = "User with id $uid is already in queue",
            instance = instance
        ).toResponse()

    }
}