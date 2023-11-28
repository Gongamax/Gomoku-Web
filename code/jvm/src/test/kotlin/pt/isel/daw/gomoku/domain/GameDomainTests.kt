package pt.isel.daw.gomoku.domain

import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.utils.Id
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class GameDomainTests {

    @Test
    fun `simple game`() {
        // given:a game
        var game = createTestGame()

        // when: alice plays
        var result = gameDomain.playRound(game, Round(Cell(1, 1), Player(alice.id, Piece.BLACK)))

        var col = 1

        for (i in 2..9) {
            game = when (result) {
                is RoundResult.OthersTurn -> result.game
                else -> fail("Unexpected round result $result")
            }

            if (i % 2 == 0) {
                assertEquals(Game.State.NEXT_PLAYER_WHITE, game.state)

                // when: bob plays
                result = gameDomain.playRound(game, Round(Cell(2, col), Player(bob.id, Piece.WHITE)))
                col++
            } else {
                assertEquals(Game.State.NEXT_PLAYER_BLACK, game.state)

                // when: alice plays
                result = gameDomain.playRound(game, Round(Cell(1, col), Player(alice.id, Piece.BLACK)))
            }
        }

        // then: alice wins
        game = when (result) {
            is RoundResult.YouWon -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.PLAYER_BLACK_WON, game.state)
    }

    @Test
    fun `cannot play twice`() {
        // given: a game
        var game = createTestGame()

        // when: alice plays
        var result = gameDomain.playRound(game, Round(Cell(1, 1), Player(alice.id, Piece.BLACK)))

        // then: next player is bob
        game = when (result) {
            is RoundResult.OthersTurn -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_WHITE, game.state)

        // when: bob plays
        result = gameDomain.playRound(game, Round(Cell(2, 1), Player(bob.id, Piece.WHITE)))

        // then: next player is alice
        game = when (result) {
            is RoundResult.OthersTurn -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_BLACK, game.state)

        // when: bob plays
        result = gameDomain.playRound(game, Round(Cell(2, 1), Player(bob.id, Piece.WHITE)))

        // then: result is a failure and next player is still alice
        when (result) {
            is RoundResult.NotYourTurn -> {}
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_BLACK, game.state)
    }

    @Test
    fun `alice wins`() {
        // given: a game and a list of rounds
        val game = createTestGame()

        val rounds = listOf(
            Round(Cell(1, 1), Player(alice.id, Piece.BLACK)),
            Round(Cell(2, 1), Player(bob.id, Piece.WHITE)),
            Round(Cell(1, 2), Player(alice.id, Piece.BLACK)),
            Round(Cell(2, 2), Player(bob.id, Piece.WHITE)),
            Round(Cell(1, 3), Player(alice.id, Piece.BLACK)),
            Round(Cell(2, 3), Player(bob.id, Piece.WHITE)),
            Round(Cell(1, 4), Player(alice.id, Piece.BLACK)),
            Round(Cell(2, 4), Player(bob.id, Piece.WHITE)),
            Round(Cell(1, 5), Player(alice.id, Piece.BLACK)),
        )

        // when: the rounds are applied
        // then: alice wins
        when (val result = play(game, rounds)) {
            is RoundResult.YouWon -> assertEquals(Game.State.PLAYER_BLACK_WON, result.game.state)
            else -> fail("Unexpected round result $result")
        }
    }

    @Test
    fun `timeout test`() {
        // given: a game logic, a game and a list of rounds
        val testClock = TestClock()
        val timeout = 5.minutes
        val gameLogic = GameDomain(
            testClock,
            gameConfig
        )
        var game = createTestGame()

        // when: alice plays
        testClock.advance(timeout - 1.minutes)
        var result = gameLogic.playRound(game, Round(Cell(1, 1), Player(alice.id, Piece.BLACK)))

        // then: round is accepted
        game = when (result) {
            is RoundResult.OthersTurn -> result.game
            else -> fail("Unexpected result $result")
        }

        // when: bob plays
        testClock.advance(timeout.plus(1.seconds))
        result = gameLogic.playRound(game, Round(Cell(1, 1), Player(bob.id, Piece.WHITE)))

        // then: round is not accepted and alice won
        game = when (result) {
            is RoundResult.TooLate -> result.game
            else -> fail("Unexpected result $result")
        }
        assertEquals(Game.State.PLAYER_BLACK_WON, game.state)
    }

    private fun play(initialGame: Game, rounds: List<Round>): RoundResult? {
        var previousResult: RoundResult? = null
        for (round in rounds) {
            val game = when (previousResult) {
                null -> initialGame
                is RoundResult.OthersTurn -> previousResult.game
                else -> fail("Unexpected round result $previousResult")
            }
            previousResult = gameDomain.playRound(game, round)
        }
        return previousResult
    }

    companion object {
        private val gameConfig = GamesDomainConfig(
            timeout = 5.minutes
        )

        private val gameDomain = GameDomain(
            Clock.System,
            gameConfig
        )

        // our test players
        private val alice = User(Id(1), "alice", Email("alice@gmail.com"), PasswordValidationInfo(""))
        private val bob = User(Id(2), "alice", Email("bob@gmail.com"), PasswordValidationInfo(""))

        private fun createTestGame(): Game =
            Game(
                id = Id(1),
                state = Game.State.NEXT_PLAYER_BLACK,
                board = Board.createBoard(Piece.BLACK),
                created = Clock.System.now(),
                updated = Clock.System.now(),
                deadline = null,
                playerBLACK = alice,
                playerWHITE = bob,
                variant = Variants.STANDARD
            )
    }
}