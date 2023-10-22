package pt.isel.daw.gomoku.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.utils.Id
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun `can create a game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user
        val email = newTestEmail()
        val username = newTestUserName()
        val password = newTestPassword()
        val alice = User(Id(1), username, Email(email), PasswordValidationInfo(password))
        val bob = User(Id(2), newTestUserName(), Email(newTestEmail()), PasswordValidationInfo(newTestPassword()))

        // when: creating an game
        // then: the response is a 201 with a proper Location header
        client.post().uri("/games")
            .bodyValue(
                mapOf(
                    "userBlack" to alice.id.value,
                    "userWhite" to bob.id.value,
                    "variant" to "STANDARD"
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/games/"))
            }
    }

    @Test
    fun `can play a game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user
        val email = newTestEmail()
        val username = newTestUserName()
        val password = newTestPassword()
        val alice = User(Id(1), username, Email(email), PasswordValidationInfo(password))
        val bob = User(Id(2), newTestUserName(), Email(newTestEmail()), PasswordValidationInfo(newTestPassword()))

        // and: a game
        val gameLocation = client.post().uri("/games")
            .bodyValue(
                mapOf(
                    "userBlack" to alice.id.value,
                    "userWhite" to bob.id.value,
                    "variant" to "STANDARD"
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/games/"))
                it
            }
            .returnResult<String>()
            .responseHeaders
            .location!!
            .toASCIIString()

        val playUri = gameLocation.split("/").drop(2).joinToString("/") + "/play"
        // when: playing a game
        // then: the response is a 200
        client.put().uri("/$playUri")
            .bodyValue(
                mapOf(
                    "userId" to alice.id.value,
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