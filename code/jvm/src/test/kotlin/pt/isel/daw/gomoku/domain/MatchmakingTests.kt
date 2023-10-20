package pt.isel.daw.gomoku.domain

import org.slf4j.LoggerFactory

class MatchmakingTests {

//    @Test
//    //test the synchronizer matchmake with 2 users
//    fun `matchmake 2 users`() {
//        //given: 2 users
//        val user1 = User(1, "user1", Email("user1@gmail.com"),PasswordValidationInfo("password1"))
//        val user2 = User(2, "user2", Email("user2@gmail.com"), PasswordValidationInfo("password2"))
//        var result : Match? = null
//
//        //and: a matchmake
//        val matchmaking = Matchmaking()
//
//        //when: user makes intent to match
//        thread {
//            logger.info("On the first waiting user")
//            matchmaking.waitForMatch(user1, 1.minutes)
//        }
//
//        //and: user2 makes intent to match
//        thread {
//            logger.info("On the matching user")
//            result = matchmaking.waitForMatch(user2, 1.minutes)
//        }
//
//        Thread.sleep(2000)
//
//        //then: the matchmake should return a match
//        assertEquals(Pair(user1, user2), result)
//    }

    companion object {
        private const val TIMEOUT = 1000L

        private val logger = LoggerFactory.getLogger(MatchmakingTests::class.java)

    }
}
