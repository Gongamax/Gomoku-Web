package pt.isel.daw.gomoku.util

import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.gomoku.http.media.siren.SirenModel
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertEquals

object AuxiliaryFunctions {
    fun newTestEmail() = "email-${abs(Random.nextLong())}@test.com"

    fun newTestUserName() = "user-${abs(Random.nextLong())}"

    fun newTestPassword() = "TestPassword${abs(Random.nextLong())}"

    fun getClientToken(client: WebTestClient, username: String, password: String): String {
        val properties = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectHeader().value("Content-Type") {
                assertEquals("application/vnd.siren+json", it)
            }
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody!!
            .properties
        return (properties as LinkedHashMap<*,*>)["token"].toString()
    }

    fun getClientUser(client: WebTestClient, id: Int?, token: String): Int {
        val properties = client.get().uri("/users/$id")
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
        return (properties as LinkedHashMap<*,*>)["uid"].toString().toInt()
    }

}