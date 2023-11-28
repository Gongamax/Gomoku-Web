package pt.isel.daw.gomoku.http

import org.junit.jupiter.api.RepeatedTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import pt.isel.daw.gomoku.http.model.TokenResponse
import pt.isel.daw.gomoku.http.model.UserGetByIdOutputModel
import kotlin.math.abs
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {

    @LocalServerPort
    var port: Int = 0

    @RepeatedTest(10)
    fun `can play a game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user
        val username1 = newTestUserName()
        val email1 = newTestEmail()
        val password1 = newTestPassword()
        var id1: Int? = null

        client.post().uri("/users")
            .bodyValue(
                mapOf(
                    "email" to email1,
                    "username" to username1,
                    "password" to password1
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
                id1 = it.substringAfter("/api/users/").toInt()
            }

        // and: a random user
        val username2 = newTestUserName()
        val email2 = newTestEmail()
        val password2 = newTestPassword()
        var id2: Int? = null

        client.post().uri("/users")
            .bodyValue(
                mapOf(
                    "email" to email2,
                    "username" to username2,
                    "password" to password2
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
                id2 = it.substringAfter("/api/users/").toInt()
            }
        // and: a login token
        val token1 = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username1,
                    "password" to password1
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        // and: a login token
        val token2 = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username2,
                    "password" to password2
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        //and : get players
        val player1 = client.get().uri("/users/$id1")
            .header("Authorization", "Bearer ${token1.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(UserGetByIdOutputModel::class.java)
            .returnResult()
            .responseBody!!

        val player2 = client.get().uri("/users/$id2")
            .header("Authorization", "Bearer ${token2.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(UserGetByIdOutputModel::class.java)
            .returnResult()
            .responseBody!!

        // when: matching a game
        val firstTryOnMatch = client.post().uri("/games/matchmaking")
            .header("Authorization", "Bearer ${token1.token}")
            .bodyValue(
                mapOf(
                    "userId" to player1.id,
                    "variant" to "STANDARD"
                )
            )
            .exchange()

        // and: other users try to match
        val secondTryOnMatch = client.post().uri("/games/matchmaking")
            .header("Authorization", "Bearer ${token2.token}")
            .bodyValue(
                mapOf(
                    "userId" to player2.id,
                    "variant" to "STANDARD"
                )
            )
            .exchange()

        println(player2.id)

        // and: get game location
        var playerInGame by Delegates.notNull<Int>()

        val gameLocation = if (firstTryOnMatch.returnResult<String>().responseHeaders.location != null) {
            firstTryOnMatch.returnResult<String>().responseHeaders.location!!.toASCIIString()
                .also { playerInGame = player1.id }
        } else {
            secondTryOnMatch.returnResult<String>().responseHeaders.location!!.toASCIIString()
                .also { playerInGame = player2.id }
        }

        val playUri = gameLocation.split("/").drop(2).joinToString("/") + "/play"
        // when: playing a game
        // then: the response is a 200
        client.post().uri("/$playUri")
            .header("Authorization", "Bearer ${token2.token}")
            .bodyValue(
                mapOf(
                    "row" to 1,
                    "column" to 1
                )
            )
            .exchange()
            .expectStatus().isOk
    }

    companion object {
        private fun newTestEmail() = "email-${abs(Random.nextLong())}@test.com"

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newTestPassword() = "TestPassword${abs(Random.nextLong())}"
    }
}