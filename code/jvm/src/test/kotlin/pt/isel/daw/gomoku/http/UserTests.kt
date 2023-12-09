package pt.isel.daw.gomoku.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.gomoku.http.media.siren.SirenModel
import pt.isel.daw.gomoku.http.model.TokenResponse
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.getClientToken
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.newTestEmail
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.newTestPassword
import pt.isel.daw.gomoku.util.AuxiliaryFunctions.newTestUserName
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @Test
    fun `can create an user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user
        val email = newTestEmail()
        val username = newTestUserName()
        val password = newTestPassword()

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        client.post().uri("/users")
            .bodyValue(
                mapOf(
                    "email" to email,
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }
    }

    @Test
    fun `can create an user, obtain a token, and access user home, and logout`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user
        val email = newTestEmail()
        val username = newTestUserName()
        val password = newTestPassword()

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        client.post().uri("/users")
            .bodyValue(
                mapOf(
                    "email" to email,
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }

        // when: creating a token
        // then: the response is a 200
        val token = getClientToken(client, username, password)

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        assertEquals(getClientUserName(client, token), username)

        // when: getting the user home with an invalid token
        // then: the response is a 4001 with the proper problem
        client.get().uri("/me")
            .header("Authorization", "Bearer ${token}-invalid")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().valueEquals("WWW-Authenticate", "bearer")

        // when: revoking the token
        // then: response is a 200
        client.post().uri("/logout")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }

        // when: getting the user home with the revoked token
        // then: response is a 401
        client.get().uri("/me")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().valueEquals("WWW-Authenticate", "bearer")
    }

    @Test
    fun `can create an user, obtain a token, and get a user by id`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user
        val email = newTestEmail()
        val username = newTestUserName()
        val password = newTestPassword()

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        client.post().uri("/users")
            .bodyValue(
                mapOf(
                    "email" to email,
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }

        // when: creating a token
        val token = getClientToken(client, username, password)

        // when: getting the user by id
        client.get().uri("/users/1")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }
            .expectBody()
    }

    companion object {
        private fun getClientUserName(client: WebTestClient, token: String): String{
            val properties = client.get().uri("/me")
                .header("Authorization", "Bearer $token")
                .exchange()
                .expectStatus().isOk
                .expectHeader().value("Content-Type") {
                    assertEquals("application/vnd.siren+json", it)
                }
                .expectBody(SirenModel::class.java)
                .returnResult()
                .responseBody!!
                .properties
            return (properties as LinkedHashMap<*,*>)["username"].toString()
        }
    }
}
