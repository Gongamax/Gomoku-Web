package pt.isel.daw.gomoku.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.utils.Sha256TokenEncoder
import pt.isel.daw.gomoku.domain.users.UsersDomain
import pt.isel.daw.gomoku.domain.users.UsersDomainConfig
import pt.isel.daw.gomoku.repository.jdbi.JdbiTransactionManager
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import pt.isel.daw.gomoku.services.users.UsersService
import pt.isel.daw.gomoku.utils.*
import java.util.*
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class UserServiceTests {

    @Test
    fun `can create user, token, and retrieve by token`() {
        // given: a user service
        val testClock = TestClock()
        val userService = createUsersService(testClock)

        // when: creating a user
        val username = newTestUserName()
        val password = newTestPassword()
        // then: the creation is successful
        when (val createUserResult = userService.createUser(username, newTestEmail(), password)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        // when: creating a token
        // then: the creation is successful
        val token = when (val createTokenResult = userService.createToken(username, password)) {
            is Either.Left -> fail(createTokenResult.toString())
            is Either.Right -> createTokenResult.value.tokenValue
        }

        // and: the token bytes have the expected length
        val tokenBytes = Base64.getUrlDecoder().decode(token)
        assertEquals(256 / 8, tokenBytes.size)

        // when: retrieving the user by token
        val user = when (val res = userService.getUserByToken(token)) {
            is Either.Left -> fail("Unexpected $res")
            is Either.Right -> res.value
        }

        // then: a user is found
        assertNotNull(user)

        // and: has the expected name
        assertEquals(username, user.username)
    }

    @Test
    fun `can use token during rolling period but not after absolute TTL`() {
        // given: a user service
        val testClock = TestClock()
        val tokenTtl = 90.minutes
        val tokenRollingTtl = 30.minutes
        val userService = createUsersService(testClock, tokenTtl, tokenRollingTtl)

        // when: creating a user
        val username = newTestUserName()
        val password = newTestPassword()
        // then: the creation is successful
        when (val createUserResult = userService.createUser(username, newTestEmail(), password)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        // when: creating a token
        // then: the creation is successful
        val token = when (val createTokenResult = userService.createToken(username, password)) {
            is Either.Left -> fail(createTokenResult.toString())
            is Either.Right -> createTokenResult.value.tokenValue
        }

        // when: retrieving the user after (rolling TTL - 1s) intervals
        val startInstant = testClock.now()
        while (true) {
            testClock.advance(tokenRollingTtl.minus(1.seconds))
            if (userService.getUserByToken(token) is Failure)
                break
        }

        // then: user is not found only after the absolute TTL has elapsed
        assertTrue((testClock.now() - startInstant) > tokenTtl)
    }

    @Test
    fun `can limit the number of tokens`() {
        // given: a user service
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        // when: creating a user
        val username = newTestUserName()
        val password = newTestPassword()
        // then: the creation is successful
        when (val createUserResult = userService.createUser(username, newTestEmail(), password)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        // when: creating MAX tokens
        val tokens = (0 until maxTokensPerUser).map {
            val createTokenResult = userService.createToken(username, password)
            testClock.advance(1.minutes)

            // then: the creation is successful
            val token = when (createTokenResult) {
                is Either.Left -> fail(createTokenResult.toString())
                is Either.Right -> createTokenResult.value
            }
            token
        }.toTypedArray().reversedArray()

        // and: using the tokens at different times
        (tokens.indices).forEach {
            assertNotNull(userService.getUserByToken(tokens[it].tokenValue), "token $it must be valid")
            testClock.advance(1.seconds)
        }

        // and: creating a new token
        val createTokenResult = userService.createToken(username, password)
        testClock.advance(1.seconds)
        val newToken = when (createTokenResult) {
            is Either.Left -> fail(createTokenResult.toString())
            is Either.Right -> createTokenResult.value
        }

        // then: newToken is valid
        assertNotNull(userService.getUserByToken(newToken.tokenValue))

        // and: the first token (the least recently used) is not valid
        assertTrue { userService.getUserByToken(tokens[0].tokenValue) is Failure }

        // and: the remaining tokens are still valid
        (1 until tokens.size).forEach {
            assertNotNull(userService.getUserByToken(tokens[it].tokenValue))
        }
    }

    @Test
    fun `can limit the number of tokens even if multiple tokens are used at the same time`() {
        // given: a user service
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        // when: creating a user
        val username = newTestUserName()
        val password = newTestPassword()
        // then: the creation is successful
        when (val createUserResult = userService.createUser(username, newTestEmail(), password)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        // when: creating MAX tokens
        val tokens = (0 until maxTokensPerUser).map {
            val createTokenResult = userService.createToken(username, password)
            testClock.advance(1.minutes)

            // then: the creation is successful
            val token = when (createTokenResult) {
                is Either.Left -> fail(createTokenResult.toString())
                is Either.Right -> createTokenResult.value
            }
            token
        }.toTypedArray().reversedArray()

        // and: using the tokens at the same time
        testClock.advance(1.minutes)
        (tokens.indices).forEach {
            assertNotNull(userService.getUserByToken(tokens[it].tokenValue), "token $it must be valid")
        }

        // and: creating a new token
        val createTokenResult = userService.createToken(username, password)
        testClock.advance(1.minutes)
        val newToken = when (createTokenResult) {
            is Either.Left -> fail(createTokenResult.toString())
            is Either.Right -> createTokenResult.value
        }

        // then: newToken is valid
        assertNotNull(userService.getUserByToken(newToken.tokenValue))

        // and: exactly one of the previous tokens is now not valid
        assertEquals(
            maxTokensPerUser - 1,
            tokens.count {
                userService.getUserByToken(it.tokenValue) is Success
            }
        )
    }

    @Test
    fun `can logout`() {
        // given: a user service
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        // when: creating a user
        val username = newTestUserName()
        val password = newTestPassword()
        // then: the creation is successful
        when (val createUserResult = userService.createUser(username, newTestEmail(), password)) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value > 0)
        }

        // when: creating a token
        // then: token creation is successful
        val token = when (val tokenCreationResult = userService.createToken(username, password)) {
            is Either.Left -> fail("Token creation should be successful: '${tokenCreationResult.value}'")
            is Either.Right -> tokenCreationResult.value
        }

        // when: using the token
        var maybeUser = userService.getUserByToken(token.tokenValue)

        // then: token usage is successful
        assertNotNull(maybeUser)

        // when: revoking and using the token
        userService.revokeToken(token.tokenValue)

        maybeUser = userService.getUserByToken(token.tokenValue)

        // then: token usage is successful
        assertTrue { maybeUser is Failure }
    }

    @Test
    fun `search by username on leaderboard returns for valid username and page`(){
        // given: a user service
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        // when: searching for alice
        val username = "alice"
        val page = 1
        val users = userService.getStatsByUsernameForRanking(username, PageValue(page))

        // then: the search is successful
        assertTrue(users is Success)
        assertTrue { users.value.content.isNotEmpty() }
        assertEquals(username, users.value.content.first { it.user.username == "alice" }.user.username)
    }

    @Test
    fun `search by username on leaderboard returns empty for invalid username`(){
        // given: a user service
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        // when: searching for invalid username
        val username = "invalid"
        val page = 1
        val users = userService.getStatsByUsernameForRanking(username, PageValue(page))

        // then: the search is successful
        assertTrue(users is Success)
        assertTrue { users.value.content.isEmpty() }
    }

    @Test
    fun `search by username on leaderboard returns empty for invalid page`(){
        // given: a user service
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        // when: searching for invalid page
        val username = "alice"
        val page = -1
        val users = userService.getStatsByUsernameForRanking(username, PageValue(page))

        // then: the search is successful
        assertTrue(users is Failure)
    }

    @Test
    fun `search by username on leaderboard returns a list of users`(){
        // given: a user service
        val testClock = TestClock()
        val maxTokensPerUser = 5
        val userService = createUsersService(testClock, maxTokensPerUser = maxTokensPerUser)

        // when: searching for alice
        val username = "user"
        val page = 1
        val users = userService.getStatsByUsernameForRanking(username, PageValue(page))

        // then: the search is successful
        assertTrue(users is Success)
        assertTrue { users.value.content.isNotEmpty() }
        assertTrue { users.value.content.size > 5 }
    }

    companion object {

        private fun createUsersService(
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3
        ) = UsersService(
            JdbiTransactionManager(jdbi),
            UsersDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UsersDomainConfig(
                    tokenSizeInBytes = 256 / 8,
                    tokenTtl = tokenTtl,
                    tokenRollingTtl,
                    maxTokensPerUser = maxTokensPerUser
                )
            ),
            testClock
        )

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newTestPassword() = "TestPassword${abs(Random.nextLong())}"

        private fun newTestEmail() = "email-${abs(Random.nextLong())}@test.com"

        private val dbUrl = Environment.getDbUrl()

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(dbUrl)
            }
        ).configureWithAppRequirements()
    }
}
