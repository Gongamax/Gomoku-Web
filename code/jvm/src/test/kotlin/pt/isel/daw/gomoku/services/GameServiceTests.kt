package pt.isel.daw.gomoku.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.games.board.Cell
import pt.isel.daw.gomoku.domain.games.variants.Variants
import pt.isel.daw.gomoku.domain.users.UsersDomain
import pt.isel.daw.gomoku.domain.users.UsersDomainConfig
import pt.isel.daw.gomoku.domain.utils.Id
import pt.isel.daw.gomoku.domain.utils.Sha256TokenEncoder
import pt.isel.daw.gomoku.repository.jdbi.JdbiTransactionManager
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import pt.isel.daw.gomoku.services.games.GamesService
import pt.isel.daw.gomoku.services.users.UsersService
import pt.isel.daw.gomoku.utils.Either
import pt.isel.daw.gomoku.utils.Environment
import pt.isel.daw.gomoku.utils.PageValue
import kotlin.math.abs
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
        val variant : Variants = Variants.STANDARD

        //when: creating a game
        val createAliceResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())
        val createBobResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())

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
        val alice = when(val res =usersService.getUserById(aliceId)){
            is Either.Left -> fail("User creation failed for $res")
            is Either.Right -> res.value
        }
        val bob = when(val res = usersService.getUserById(bobId)) {
            is Either.Left -> fail("User creation failed for $res")
            is Either.Right -> res.value
        }

        //then: alice and bob are not null
        assertNotNull(alice)
        assertNotNull(bob)

        //when: creating a game
        val createGameResult = gamesService.createGame(alice.id.value, bob.id.value, variant.name)

        //then: the game is created
        val game = when (createGameResult) {
            is Either.Left -> fail("Game creation failed for ${createGameResult.value}")
            is Either.Right -> createGameResult.value
        }

        // when: getting the game by id
        val gameByIdValidated = when (val gameById = gamesService.getGameById(game)) {
            is Either.Left -> fail("Failed to get game by id for $gameById")
            is Either.Right -> gameById.value
        }

        //then: the game is found
        assertNotNull(gameByIdValidated)
    }

    @Test
    fun `get all games on database`() {
        //given: a game service and a user service to create users
        val testClock = TestClock()
        val gamesService = createGamesService(testClock)
        val usersService = createUsersService(testClock)
        val variant : Variants = Variants.STANDARD

        //when: creating a game
        val createAliceResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())
        val createBobResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())

        //when: creating alice and bob
        val aliceId = when (createAliceResult) {
            is Either.Left -> fail("User creation failed for $createAliceResult")
            is Either.Right -> createAliceResult.value
        }
        val bobId = when (createBobResult) {
            is Either.Left -> fail("User creation failed for $createBobResult")
            is Either.Right -> createBobResult.value
        }

        //when: creating a game
        val createGameResult = gamesService.createGame(aliceId, bobId, variant.name)

        //then: the game is created
        val game = when (createGameResult) {
            is Either.Left -> fail("Game creation failed for $createGameResult")
            is Either.Right -> createGameResult.value
        }

        // when: getting all games
        val allGames = gamesService.getAll(PageValue(1))

        //then: the game is found
        assertNotNull(allGames)

        //then: the game is found
        val gamesFound = when (allGames) {
            is Either.Left -> fail("Failed to get all games for $allGames")
            is Either.Right -> allGames.value
        }
        assertTrue { gamesFound.content.isNotEmpty() }
    }

    @Test
    fun `create a game and play a round then leave a game and check if user games was updated`() {
        //given: a game service and a user service to create users
        val testClock = TestClock()
        val gamesService = createGamesService(testClock)
        val usersService = createUsersService(testClock)
        val variant : Variants = Variants.STANDARD

        //when: creating a game
        val createAliceResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())
        val createBobResult = usersService.createUser(newTestUserName(), newTestEmail(), newTestPassword())

        //when: creating alice and bob
        val aliceId = when (createAliceResult) {
            is Either.Left -> fail("User creation failed for $createAliceResult")
            is Either.Right -> createAliceResult.value
        }
        val bobId = when (createBobResult) {
            is Either.Left -> fail("User creation failed for $createBobResult")
            is Either.Right -> createBobResult.value
        }

        //when: creating a game
        val gameId = when (val createGameResult = gamesService.createGame(aliceId, bobId, variant.name)) {
            is Either.Left -> fail("Game creation failed for $createGameResult")
            is Either.Right -> createGameResult.value
        }

        // when: getting the game by id
        val game = when (val gameById = gamesService.getGameById(gameId)) {
            is Either.Left -> fail("Failed to get game by id for $gameById")
            is Either.Right -> gameById.value
        }

        // when: playing a round
        val round = Round(Cell(1, 1), Player(Id(aliceId), Piece.BLACK))


        when(val playRoundResult = gamesService.play(gameId, aliceId, 1, 1)) {
            is Either.Left -> fail("Failed to play round for $playRoundResult")
            is Either.Right -> playRoundResult.value
        }

        //then: the game is updated
        val gameById = when (val gameById = gamesService.getGameById(gameId)) {
            is Either.Left -> fail("Failed to get game by id for $gameById")
            is Either.Right -> gameById.value
        }
        assertTrue { gameById.board.moves[Cell(1, 1)] == round.player.piece }

        // then: leaving the game
        gamesService.leaveGame(game.id.value, aliceId)

        // then: the game is updated
        val gameByIdAfterLeave = when(val gameByIdAfterLeave = gamesService.getGameById(game.id.value)) {
            is Either.Left -> fail("Failed to get game by id for $gameByIdAfterLeave")
            is Either.Right -> gameByIdAfterLeave.value
        }
        // then: check the winner
        assertTrue { gameByIdAfterLeave.state == Game.State.PLAYER_WHITE_WON }

        //then: check if points were added and games played were updated
        val whiteStats = when(val whiteStats = usersService.getUserStatsById(game.playerWHITE.id.value)) {
            is Either.Left -> fail("Failed to get user stats for $whiteStats")
            is Either.Right -> whiteStats.value
        }
        assertTrue { whiteStats.points > 0 }
        assertTrue { whiteStats.gamesPlayed > 0 }

        //then: check if games of user were updated
        val gamesOfUser = when(val gamesOfUser = gamesService.getGamesOfUser(game.playerWHITE.id.value, PageValue(1))) {
            is Either.Left -> fail("Failed to get games of user for $gamesOfUser")
            is Either.Right -> gamesOfUser.value
        }
        assertTrue { gamesOfUser.content.isNotEmpty() }
    }

    @Test
    fun `get all variants`(){
        //given: a game service
        val testClock = TestClock()
        val gamesService = createGamesService(testClock)

        //when: getting all variants
        val variants = gamesService.getAllVariants()

        //then: the variants are not empty
        when (variants) {
            is Either.Left -> fail("Failed to get all variants for $variants")
            is Either.Right -> assertTrue { variants.value.isNotEmpty() }
        }
    }

    companion object {

        private fun createGamesService(
            testClock: TestClock,
        ) = GamesService(
            JdbiTransactionManager(jdbi),
            clock = testClock,
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