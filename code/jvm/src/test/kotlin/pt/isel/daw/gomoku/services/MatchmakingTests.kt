package pt.isel.daw.gomoku.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.gomoku.utils.Environment
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.GameDomain
import pt.isel.daw.gomoku.domain.games.GamesDomainConfig
import pt.isel.daw.gomoku.domain.games.Variants
import pt.isel.daw.gomoku.domain.users.UsersDomain
import pt.isel.daw.gomoku.domain.users.UsersDomainConfig
import pt.isel.daw.gomoku.domain.utils.Sha256TokenEncoder
import pt.isel.daw.gomoku.repository.jdbi.JdbiTransactionManager
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingStatus
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import pt.isel.daw.gomoku.services.games.GamesService
import pt.isel.daw.gomoku.services.users.UsersService
import pt.isel.daw.gomoku.utils.Either
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success
import kotlin.math.abs
import kotlin.random.Random
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
        var res: Int? = null

        //when: creating a user
        val createAliceResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())

        //when: creating alice and bob
        val aliceId = when (createAliceResult) {
            is Either.Left -> fail("User creation failed for $createAliceResult")
            is Either.Right -> createAliceResult.value
        }

        //when: creating a matchmaking entry
        val matchmakingEntry = gamesService.tryMatchmaking(aliceId, variant.name)

        //then: if there was no match, alice is in the matchmaking queue
        when (matchmakingEntry) {
            is Failure -> {
                println("Failed to create matchmaking entry for $matchmakingEntry")
                val status = when (val s = gamesService.getMatchmakingStatus(aliceId)) {
                    is Either.Left -> fail("Failed to get matchmaking status for $s")
                    is Either.Right -> s.value
                }

                //then: while the matchmaking entry is valid, the game is not found
                while (status != MatchmakingStatus.MATCHED) {
                    val createBobResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())

                    val bobId = when (createBobResult) {
                        is Either.Left -> fail("User creation failed for $createBobResult")
                        is Either.Right -> createBobResult.value
                    }

                    //when: creating a matchmaking entry
                    when (val newMatchmakingEntry = gamesService.tryMatchmaking(bobId, variant.name)) {
                        is Either.Left -> Unit
                        is Either.Right -> {
                            when (val game = gamesService.getGameById(newMatchmakingEntry.value)) {
                                is Either.Left -> fail("Failed to get game by id for $game")
                                is Either.Right -> {
                                    res = game.value.id.value
                                    break
                                }
                            }
                        }
                    }
                }

                val gameByIdValidated = when (val gameById = gamesService.getGameById(res!!)) {
                    is Either.Left -> null
                    is Either.Right -> gameById.value
                }

                //then: the game is found
                assertNotNull(gameByIdValidated)
            }

            is Success -> {

                res = matchmakingEntry.value

                //when: getting the game by id
                val gameByIdValidated = when (val gameById = gamesService.getGameById(res)) {
                    is Either.Left -> null
                    is Either.Right -> gameById.value
                }

                //then: the game is found
                assertNotNull(gameByIdValidated)
            }
        }
    }

    companion object {

        private fun createGamesService(
            testClock: TestClock,
        ) = GamesService(
            JdbiTransactionManager(jdbi),
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