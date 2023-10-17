package pt.isel.daw.gomoku.domain

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import pt.isel.daw.gomoku.domain.games.Match
import pt.isel.daw.gomoku.domain.games.Matchmaking
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class MatchmakingTests {

    @Test
    //test the synchronizer matchmake with 2 users
    fun `matchmake 2 users`() {
        //given: 2 users
        val user1 = User(1, "user1", PasswordValidationInfo("password1"))
        val user2 = User(2, "user2", PasswordValidationInfo("password2"))
        var result : Match? = null

        //and: a matchmake
        val matchmaking = Matchmaking<User>()

        //when: user makes intent to match
        thread {
            logger.info("On the first waiting user")
            matchmaking.waitForMatch(user1, 1.minutes)
        }

        //and: user2 makes intent to match
        thread {
            logger.info("On the matching user")
            result = matchmaking.waitForMatch(user2, 1.minutes)
        }

        Thread.sleep(2000)

        //then: the matchmake should return a match
        assertEquals(Pair(user1, user2), result)
    }

    companion object {
        private const val TIMEOUT = 1000L

        private val logger = LoggerFactory.getLogger(MatchmakingTests::class.java)

    }
}
