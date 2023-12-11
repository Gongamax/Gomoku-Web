package pt.isel.daw.gomoku.http

import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import pt.isel.daw.gomoku.http.media.siren.SirenModel
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.getClientToken
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.getClientUser
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.newTestEmail
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.newTestPassword
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.newTestUserName
import kotlin.math.abs
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.test.assertEquals
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
            }.expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
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
            }.expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }
        // and: a login token
        val token1 = getClientToken(client, username1, password1)

        // and: a login token
        val token2 = getClientToken(client, username2, password2)

        //and : get players
        val player1Id = getClientUser(client, id1, token1)

        val player2Id = getClientUser(client, id2, token2)

        // when: matching a game
        val firstTryOnMatch = client.post().uri("/games/matchmaking")
            .header("Authorization", "Bearer $token1")
            .bodyValue(
                mapOf(
                    "userId" to player1Id,
                    "variant" to "STANDARD"
                )
            )
            .exchange()
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }

        // and: other users try to match
        val secondTryOnMatch = client.post().uri("/games/matchmaking")
            .header("Authorization", "Bearer $token2")
            .bodyValue(
                mapOf(
                    "userId" to player2Id,
                    "variant" to "STANDARD"
                )
            )
            .exchange()
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }

        // and: get game location
        var playerInGame by Delegates.notNull<Int>()

        val gameLocation = if (firstTryOnMatch.returnResult<String>().responseHeaders.location != null) {
            firstTryOnMatch.returnResult<String>().responseHeaders.location!!.toASCIIString()
                .also { playerInGame = player1Id }
        } else {
            secondTryOnMatch.returnResult<String>().responseHeaders.location!!.toASCIIString()
                .also { playerInGame = player2Id }
        }

        val playUri = gameLocation.split("/").drop(2).joinToString("/") + "/play"
        // when: playing a game
        // then: the response is a 200
        client.post().uri("/$playUri")
            .header("Authorization", "Bearer $token2")
            .bodyValue(
                mapOf(
                    "row" to 3,
                    "column" to 11
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }
    }
}