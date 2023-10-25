package pt.isel.daw.gomoku.http

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.http.model.TokenResponse
import kotlin.math.abs
import kotlin.random.Random
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

        // when: creating a token
        // then: the response is a 200
        val result = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        client.get().uri("/me")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("username").isEqualTo(username)

        // when: getting the user home with an invalid token
        // then: the response is a 4001 with the proper problem
        client.get().uri("/me")
            .header("Authorization", "Bearer ${result.token}-invalid")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().valueEquals("WWW-Authenticate", "bearer")

        // when: revoking the token
        // then: response is a 200
        client.post().uri("/logout")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus().isOk

        // when: getting the user home with the revoked token
        // then: response is a 401
        client.get().uri("/me")
            .header("Authorization", "Bearer ${result.token}")
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

        // when: creating a token
        val token = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        // when: getting the user by id
        client.get().uri("/users/1")
            .header("Authorization", "Bearer ${token.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
    }

    companion object {
        private fun newTestEmail() = "email-${abs(Random.nextLong())}@test.com"

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newTestPassword() = "TestPassword${abs(Random.nextLong())}"
    }
}
