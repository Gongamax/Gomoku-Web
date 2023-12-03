package pt.isel.daw.gomoku.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.gomoku.utils.Environment
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.GameDomain
import pt.isel.daw.gomoku.domain.games.GamesDomainConfig
import pt.isel.daw.gomoku.domain.games.variants.Variants
import pt.isel.daw.gomoku.domain.users.UsersDomain
import pt.isel.daw.gomoku.domain.users.UsersDomainConfig
import pt.isel.daw.gomoku.domain.utils.Sha256TokenEncoder
import pt.isel.daw.gomoku.repository.jdbi.JdbiTransactionManager
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingStatus
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import pt.isel.daw.gomoku.services.games.GamesService
import pt.isel.daw.gomoku.services.games.MatchmakingSuccess
import pt.isel.daw.gomoku.services.users.UsersService
import pt.isel.daw.gomoku.utils.Either
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class MatchmakingTests {

    @RepeatedTest(10)
    fun `can create a matchmaking entry`() {
        //given: a game service and a user service to create users
        val testClock = TestClock()
        val gamesService = createGamesService(testClock)
        val usersService = createUsersService(testClock)
        val variant: Variants = Variants.STANDARD

        //given: a variable to store the game id
        //var res: Int? = null

        //when: creating a user
        val createAliceResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())

        //when: creating alice and bob
        val aliceId = when (createAliceResult) {
            is Either.Left -> fail("User creation failed for $createAliceResult")
            is Either.Right -> createAliceResult.value
        }

        //when: creating a matchmaking entry
        when (val matchmakingEntry = gamesService.tryMatchmaking(aliceId, variant.name)) {
            is Failure -> {
                logger.info("Failed to create matchmaking entry for $matchmakingEntry")
            }

            is Success -> {
                //then: check if was added to the queue or if a game was created
                when (matchmakingEntry.value) {
                    is MatchmakingSuccess.OnWaitingQueue -> {
                        logger.info("Alice is on the waiting queue in position {} ", matchmakingEntry.value.id)
                        //then: check if the matchmaking entry is valid
                        when (val entry = gamesService.getMatchmakingStatus(matchmakingEntry.value.id, aliceId)) {
                            is Failure -> fail("Failed to get matchmaking entry for $entry")
                            is Success -> {
                                assertNotNull(entry.value)
                                assertTrue { entry.value.status == MatchmakingStatus.PENDING }
                            }
                        }
                    }

                    is MatchmakingSuccess.MatchFound -> {
                        logger.info("Game created for alice")
                        //then: check if the game is valid
                        when (val game = gamesService.getGameById(matchmakingEntry.value.id)) {
                            is Failure -> fail("Failed to get game for $game")
                            is Success -> {
                                assertNotNull(game.value)
                                assertTrue { game.value.playerBLACK.id.value == aliceId || game.value.playerWHITE.id.value == aliceId }
                            }
                        }

                    }
                }
            }
        }
    }

    companion object {

        private val logger = LoggerFactory.getLogger(MatchmakingTests::class.java)

        private fun createGamesService(
            testClock: TestClock,
        ) = GamesService(
            JdbiTransactionManager(jdbi),
            testClock,
            GameDomain(
                clock = testClock,
                config = GamesDomainConfig(
                    timeout = 10.minutes
                )
            ),
        )

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

        private fun newTestUserName() = "user-${Math.abs(Random.nextLong())}"

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