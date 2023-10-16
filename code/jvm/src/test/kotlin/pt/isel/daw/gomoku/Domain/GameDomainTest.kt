package pt.isel.daw.gomoku.Domain

import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class GameDomainTest {

        @Test
        fun `simple game`() {
            // given:a game
            var game = gameDomain.createGame(alice, bob)

            // when: alice plays
            var result = gameDomain.playRound(game, Round(Cell(1, 1), Player(alice.id, Piece.BLACK)))

            var col = 1

            for (i in 2..9) {
                game = when (result) {
                    is RoundResult.OthersTurn -> result.game
                    else -> fail("Unexpected round result $result")
                }

                if(i%2 == 0){
                    assertEquals(Game.State.NEXT_PLAYER_WHITE, game.state)

                    // when: bob plays
                    result = gameDomain.playRound(game, Round(Cell(2, col), Player(bob.id, Piece.WHITE)))
                    col ++
                }
                else{
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
            var game = gameDomain.createGame(alice, bob)

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
            val game = gameDomain.createGame(alice, bob)

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
            when (val result = play(gameDomain, game, rounds)) {
                is RoundResult.YouWon -> assertEquals(Game.State.PLAYER_BLACK_WON, result.game.state)
                else -> fail("Unexpected round result $result")
            }
        }

//        @Test
//        fun `test draw game`() {
//            // given: a game and a list of rounds
//            val game = gameDomain.createNewGame(alice, bob)
//
//            // when: the rounds are applied
//            val result = play(gameDomain, game, rounds)
//
//            // then: it's a draw
//        }

    @Test
    fun `timeout test`() {
        // given: a game logic, a game and a list of rounds
        val testClock = TestClock()
        val timeout = 5.minutes
        val gameLogic = GameDomain(testClock, timeout)
        var game = gameLogic.createGame(alice, bob)

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

        private fun play(domain: GameDomain, initialGame: Game, rounds: List<Round>): RoundResult? {
            var previousResult: RoundResult? = null
            for (round in rounds) {
                val game = when (previousResult) {
                    null -> initialGame
                    is RoundResult.OthersTurn -> previousResult.game
                    else -> fail("Unexpected round result $previousResult")
                }
                previousResult = domain.playRound(game, round)
            }
            return previousResult
        }

        companion object {
            private val gameDomain = GameDomain(
                Clock.System,
                5.minutes,
            )

            // our test players
            private val alice = User(1, "alice", PasswordValidationInfo(""))
            private val bob = User(2, "alice", PasswordValidationInfo(""))
        }
}