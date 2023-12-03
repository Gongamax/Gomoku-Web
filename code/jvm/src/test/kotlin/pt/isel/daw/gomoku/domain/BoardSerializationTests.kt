package pt.isel.daw.gomoku.domain

import kotlin.test.Test
import pt.isel.daw.gomoku.domain.games.board.BoardRun
import pt.isel.daw.gomoku.domain.games.Piece
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.utils.Id
import pt.isel.daw.gomoku.repository.jdbi.BoardSerializer
import kotlin.test.assertEquals

class BoardTests {

    private val mockUser = User(
        Id(1),
        "mockUser",
        Email("mock@mock.pt"),
        PasswordValidationInfo("mockPassword")
    )

    private val boardSerializer = BoardSerializer

    @Test
    fun `serialize and deserialize a board`() {

        val board = BoardRun(emptyMap(), Piece.BLACK)
        val boardString = board.toString()
        assertEquals(boardString, "Run:BLACK")

        val serialized = boardSerializer.serialize(board)

        val newBoard = boardSerializer.deserialize(serialized)

        assertEquals(board, newBoard)
    }
}