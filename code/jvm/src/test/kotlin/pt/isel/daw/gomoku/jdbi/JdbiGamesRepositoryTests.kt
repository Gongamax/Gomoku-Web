package pt.isel.daw.gomoku.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.gomoku.utils.Environment
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.games.board.Cell
import pt.isel.daw.gomoku.domain.games.board.playRound
import pt.isel.daw.gomoku.domain.games.variants.Variants
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.gomoku.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.minutes

class JdbiGamesRepositoryTests {

    @Test
    fun `can create and retrieve a game`() = runWithHandle { handle ->
        // given: repositories and domain
        val userRepo = JdbiUsersRepository(handle)
        val gameRepo = JdbiGamesRepository(handle)
        val testClock = TestClock()
        val gameDomain = GameDomain(testClock, gameConfig)

        // and: two existing users
        val aliceName = newTestUserName()
        val bobName = newTestUserName()
        val aliceEmail = newTestEmail()
        val bobEmail = newTestEmail()
        val passwordValidationInfo = PasswordValidationInfo("not-valid")
        userRepo.storeUser(aliceName, aliceEmail, passwordValidationInfo)
        userRepo.storeUser(bobName, bobEmail, passwordValidationInfo)

        // when:
        val alice = userRepo.getUserByUsername(aliceName) ?: fail("user must exist")
        val bob = userRepo.getUserByUsername(bobName) ?: fail("user must exist")
        val g = gameRepo.createGame(gameDomain.createGameModel(alice, bob, Variants.STANDARD))


        // and: retrieving the game
        val game = gameRepo.getGame(g) ?: fail("game must exist")

        // when: updating the game
        val newGame = game.copy(board = game.board.playRound(Cell(1,1), Piece.BLACK, game.variant))

        // and: storing the game
        gameRepo.updateGame(newGame)

        // and: retrieving the game again
        val newRetrievedGame = gameRepo.getGame(newGame.id.value) ?: fail("game must exist")

        // then: the two games are equal
        assertEquals(newGame, newRetrievedGame)
    }

    @Test
    fun `get variants returns all variants`() = runWithHandle { handle ->
        val gameRepo = JdbiGamesRepository(handle)

        // and: gettings all variants
        val variants = gameRepo.getAllVariants()
        assertTrue { variants.isNotEmpty()  }
    }


    companion object {

        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newTestEmail() = Email("email-${abs(Random.nextLong())}@test.com")

        private val gameConfig = GamesDomainConfig(
            timeout = 10.minutes
        )

        private val dbUrl = Environment.getDbUrl()

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(dbUrl)
            }
        ).configureWithAppRequirements()
    }

}