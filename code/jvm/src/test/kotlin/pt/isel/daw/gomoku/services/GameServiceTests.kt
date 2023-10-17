package pt.isel.daw.gomoku.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.gomoku.Environment
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.UsersDomain
import pt.isel.daw.gomoku.domain.users.UsersDomainConfig
import pt.isel.daw.gomoku.domain.utils.Sha256TokenEncoder
import pt.isel.daw.gomoku.repository.jdbi.JdbiTransactionManager
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import pt.isel.daw.gomoku.services.games.GamesService
import pt.isel.daw.gomoku.services.users.UsersService
import pt.isel.daw.gomoku.utils.Either
import kotlin.random.Random
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class GameServiceTests {

    @Test
    fun `can create game and retrieve it`() {
        //given: a game service and a user service to create users
        val testClock = TestClock()
        val gamesService = createGamesService(testClock)
        val usersService = createUsersService(testClock)

        //when: creating a game
        val createAliceResult = usersService.createUser(newTestUserName(), "password")
        val createBobResult = usersService.createUser(newTestUserName(), "password")

        //when: creating alice and bob
        val aliceId = when (createAliceResult) {
            is Either.Left -> fail("User creation failed for $createAliceResult")
            is Either.Right -> createAliceResult.value
        }
        val bobId = when (createBobResult) {
            is Either.Left -> fail("User creation failed for $createBobResult")
            is Either.Right -> createBobResult.value
        }

        //when: getting alice and bob
        val alice = usersService.getUserById(aliceId)
        val bob = usersService.getUserById(bobId)

        //then: alice and bob are not null
        assertNotNull(alice)
        assertNotNull(bob)

        //when: creating a game
        val createGameResult = gamesService.createGame(alice, bob)

        //then: the game is created
        val game = when (createGameResult) {
            is Either.Left -> fail("Game creation failed for $createGameResult")
            is Either.Right -> createGameResult.value
        }

        // when: getting the game by id
        val gameById = gamesService.getById(game.id)

        //then: the game is found
        assertNotNull(gameById)

        // then: the game is the same
        println(game)
        println(gameById)
        assert(gameById == game)
    }

    @Test
    fun `get all games on database`() {
        //given: a game service and a user service to create users
        val testClock = TestClock()
        val gamesService = createGamesService(testClock)
        val usersService = createUsersService(testClock)

        //when: creating a game
        val createAliceResult = usersService.createUser(newTestUserName(), "password")
        val createBobResult = usersService.createUser(newTestUserName(), "password")

        //when: creating alice and bob
        val aliceId = when (createAliceResult) {
            is Either.Left -> fail("User creation failed for $createAliceResult")
            is Either.Right -> createAliceResult.value
        }
        val bobId = when (createBobResult) {
            is Either.Left -> fail("User creation failed for $createBobResult")
            is Either.Right -> createBobResult.value
        }

        //when: getting alice and bob
        val alice = usersService.getUserById(aliceId)
        val bob = usersService.getUserById(bobId)

        //then: alice and bob are not null
        assertNotNull(alice)
        assertNotNull(bob)

        //when: creating a game
        val createGameResult = gamesService.createGame(alice, bob)

        //then: the game is created
        val game = when (createGameResult) {
            is Either.Left -> fail("Game creation failed for $createGameResult")
            is Either.Right -> createGameResult.value
        }

        // when: getting all games
        val allGames = gamesService.getAll()

        //then: the game is found
        assertNotNull(allGames)

        // then: the game is the same
        assertTrue { allGames.contains(game) }
    }

    @Test
    fun `create a game and play a round then leave a game`() {
        //given: a game service and a user service to create users
        val testClock = TestClock()
        val gamesService = createGamesService(testClock)
        val usersService = createUsersService(testClock)

        //when: creating a game
        val createAliceResult = usersService.createUser(newTestUserName(), "password")
        val createBobResult = usersService.createUser(newTestUserName(), "password")

        //when: creating alice and bob
        val aliceId = when (createAliceResult) {
            is Either.Left -> fail("User creation failed for $createAliceResult")
            is Either.Right -> createAliceResult.value
        }
        val bobId = when (createBobResult) {
            is Either.Left -> fail("User creation failed for $createBobResult")
            is Either.Right -> createBobResult.value
        }

        //when: getting alice and bob
        val alice = usersService.getUserById(aliceId)
        val bob = usersService.getUserById(bobId)

        //then: alice and bob are not null
        assertNotNull(alice)
        assertNotNull(bob)

        //when: creating a game
        val createGameResult = gamesService.createGame(alice, bob)

        //then: the game is created
        val game = when (createGameResult) {
            is Either.Left -> fail("Game creation failed for $createGameResult")
            is Either.Right -> createGameResult.value
        }

        // when: playing a round
        val round = Round(Cell(1, 1), Player(alice.id, Piece.BLACK))
        val playRoundResult = gamesService.play(game.id, round)

        //then: the round is played
        assertTrue { playRoundResult is RoundResult.OthersTurn }

        //then: the game is updated
        val gameById = gamesService.getById(game.id)
        assertTrue { gameById.board.moves[Cell(1,1)] == round.player }

        // then: leaving the game
        gamesService.leaveGame(game.id, alice.id)

        // then: the game is updated
        val gameByIdAfterLeave = gamesService.getById(game.id)

        // then: check the winner
        assertTrue { gameByIdAfterLeave.state == Game.State.PLAYER_WHITE_WON }
    }

    companion object {

        private fun createGamesService(
            testClock: TestClock,
        ) = GamesService(
            JdbiTransactionManager(jdbi),
            GameDomain(
                clock = testClock,
                config = GamesDomainConfig(
                    boardSize = 15,
                    variant = Variant.STANDARD,
                    openingRule = OpeningRule.STANDARD,
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

        private val dbUrl = Environment.getDbUrl()

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(dbUrl)
            }
        ).configureWithAppRequirements()
    }
}