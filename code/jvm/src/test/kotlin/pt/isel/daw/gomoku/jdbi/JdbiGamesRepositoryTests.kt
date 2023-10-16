package pt.isel.daw.gomoku.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.gomoku.Environment
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.gomoku.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import java.util.UUID
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration.Companion.minutes

class JdbiGamesRepositoryTests {

    @Test
    fun `can create and retrieve a game`() = runWithHandle { handle ->
        // given: repositories and domain
        val userRepo = JdbiUsersRepository(handle)
        val gameRepo = JdbiGamesRepository(handle)
        val testClock = TestClock()
        val gameDomain = GameDomain(testClock, 5.minutes)

        // and: two existing users
        val aliceName = newTestUserName()
        val bobName = newTestUserName()
        val passwordValidationInfo = PasswordValidationInfo("not-valid")
        userRepo.storeUser(aliceName, passwordValidationInfo)
        userRepo.storeUser(bobName, passwordValidationInfo)

        // when:
        val alice = userRepo.getUserByUsername(aliceName) ?: fail("user must exist")
        val bob = userRepo.getUserByUsername(bobName) ?: fail("user must exist")
        val game = gameDomain.createGame(alice, bob)
        println(game.toString())
        gameRepo.createGame(game)

        // and: retrieving the game
        val retrievedGame = gameRepo.getGame(game.id) ?: fail("game must exist")

        println(retrievedGame.toString())

        // then: the retrieved game must be the same as the created one
        assertEquals(game, retrievedGame)

        // when: updating the game
        val newGame = game.copy(board = game.board.playRound(Cell(1,1), Player(bob.id, Piece.WHITE)))

        // and: storing the game
        gameRepo.updateGame(newGame)

        // and: retrieving the game again
        val newRetrievedGame = gameRepo.getGame(newGame.id) ?: fail("game must exist")

        // then: the two games are equal
        assertEquals(newGame, newRetrievedGame)
    }

    @Test
    fun `retrieve a game`() = runWithHandle { handle ->
        val id = UUID.fromString("4c7159cc-a146-46c4-8158-920b4e6d7ddb")
        val repo = JdbiGamesRepository(handle)
        val game = repo.getGame(id)

        println(game.toString())
        assertEquals(id, game?.id)
    }


    companion object {

        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private fun newTestUserName() = "user-${abs(Random.nextLong())}"

        private fun newGameId() = "game-${abs(Random.nextLong())}"

        private fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

        private val dbUrl = Environment.getDbUrl()

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(dbUrl)
            }
        ).configureWithAppRequirements()
    }

}