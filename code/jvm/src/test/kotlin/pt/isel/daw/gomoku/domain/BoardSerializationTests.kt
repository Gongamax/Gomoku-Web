package pt.isel.daw.gomoku.domain

import org.junit.jupiter.api.Test
import pt.isel.daw.gomoku.domain.games.BoardRun
import pt.isel.daw.gomoku.domain.games.Piece
import pt.isel.daw.gomoku.domain.games.Player
import pt.isel.daw.gomoku.domain.games.Variant
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.repository.jdbi.BoardSerializer
import kotlin.test.assertEquals

class BoardTests {

    private val mockUser = User(
        1,
        "mockUser",
        Email("mock@mock.pt"),
        PasswordValidationInfo("mockPassword")
    )

    private val boardSerializer = BoardSerializer

    @Test
    fun `serialize and deserialize a board`() {

        val board = BoardRun(emptyMap(), Player(mockUser.id, Piece.BLACK), Variant.STANDARD)
        val boardString = board.toString()
        assertEquals(boardString, "Run:Player(userId=1, piece=BLACK)")

        val serialized = boardSerializer.serialize(board)

        val newBoard = boardSerializer.deserialize(serialized)

        assertEquals(board, newBoard)
    }
}