package pt.isel.daw.gomoku.domain

import kotlin.test.Test
import pt.isel.daw.gomoku.domain.games.board.BoardRun
import pt.isel.daw.gomoku.domain.games.Piece
import pt.isel.daw.gomoku.domain.games.board.BoardDraw
import pt.isel.daw.gomoku.domain.games.board.Cell
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

    @Test
    fun `serialize and deserialize a board with moves`() {

        val board = BoardRun(mapOf(Cell(1, 2) to Piece.BLACK, Cell(3, 4) to Piece.WHITE), Piece.BLACK)
        println(board)
        val boardString = board.toString()
        assertEquals(boardString, "Run:BLACK1C:BLACK 3E:WHITE")

        val serialized = boardSerializer.serialize(board)

        val newBoard = boardSerializer.deserialize(serialized)

        assertEquals(board, newBoard)
    }

    @Test
    fun `serialize and deserialize a board on draw`() {

        //Board
        val board = BoardDraw(
            moves =
            mapOf(
                Cell(1, 1) to Piece.BLACK,
                Cell(1, 2) to Piece.WHITE,
                Cell(1, 3) to Piece.BLACK,
                Cell(1, 4) to Piece.WHITE,
                Cell(1, 5) to Piece.BLACK,
                Cell(2, 1) to Piece.WHITE,
                Cell(2, 2) to Piece.BLACK,
                Cell(2, 3) to Piece.WHITE,
                Cell(2, 4) to Piece.BLACK,
                Cell(2, 5) to Piece.WHITE,
                Cell(3, 1) to Piece.BLACK,
                Cell(3, 2) to Piece.WHITE,
                Cell(3, 3) to Piece.BLACK,
                Cell(3, 4) to Piece.WHITE,
                Cell(3, 5) to Piece.BLACK,
                Cell(4, 1) to Piece.WHITE,
                Cell(4, 2) to Piece.BLACK,
                Cell(4, 3) to Piece.WHITE,
                Cell(4, 4) to Piece.BLACK,
                Cell(4, 5) to Piece.WHITE,
                Cell(5, 1) to Piece.BLACK,
                Cell(5, 2) to Piece.WHITE,
                Cell(5, 3) to Piece.BLACK,
                Cell(5, 4) to Piece.WHITE,
                Cell(5, 5) to Piece.BLACK
            )
        )
        val boardString = board.toString()
        assertEquals(
            boardString,
            "Draw:-1B:BLACK 1C:WHITE 1D:BLACK 1E:WHITE 1F:BLACK 2B:WHITE 2C:BLACK 2D:WHITE 2E:BLACK 2F:WHITE 3B:BLACK 3C:WHITE 3D:BLACK 3E:WHITE 3F:BLACK 4B:WHITE 4C:BLACK 4D:WHITE 4E:BLACK 4F:WHITE 5B:BLACK 5C:WHITE 5D:BLACK 5E:WHITE 5F:BLACK"
        )

        val serialized = boardSerializer.serialize(board)

        val newBoard = boardSerializer.deserialize(serialized)

        assertEquals(board, newBoard)
    }
}