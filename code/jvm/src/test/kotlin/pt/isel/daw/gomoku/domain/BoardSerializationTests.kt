package pt.isel.daw.gomoku.domain

import pt.isel.daw.gomoku.domain.games.Piece
import pt.isel.daw.gomoku.domain.games.board.BoardDraw
import pt.isel.daw.gomoku.domain.games.board.BoardRun
import pt.isel.daw.gomoku.domain.games.board.Cell
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.utils.Id
import pt.isel.daw.gomoku.repository.jdbi.BoardSerializer
import kotlin.test.Test
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
        val boardString = board.toString()
        assertEquals(boardString, "Run:BLACK2C:BLACK 4E:WHITE")

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
            "Draw:-2B:BLACK 2C:WHITE 2D:BLACK 2E:WHITE 2F:BLACK 3B:WHITE 3C:BLACK 3D:WHITE 3E:BLACK 3F:WHITE 4B:BLACK 4C:WHITE 4D:BLACK 4E:WHITE 4F:BLACK 5B:WHITE 5C:BLACK 5D:WHITE 5E:BLACK 5F:WHITE 6B:BLACK 6C:WHITE 6D:BLACK 6E:WHITE 6F:BLACK"
        )

        val serialized = boardSerializer.serialize(board)

        val newBoard = boardSerializer.deserialize(serialized)

        assertEquals(board, newBoard)
    }

    @Test
    fun `serialize a play then deserialize`() {
        //given: a board
        val board = BoardRun(mapOf(Cell(13, 12) to Piece.BLACK, Cell(10, 4) to Piece.WHITE), Piece.BLACK)
        val boardString = board.toString()
        assertEquals(boardString, "Run:BLACK14M:BLACK 11E:WHITE")

        //then: serialize the board
        val serialized = boardSerializer.serialize(board)

        //assert: the serialized board is the same as the original
        assertEquals(
            serialized,
            "{\"kind\":\"Run\",\"piece\":\"BLACK\",\"moves\":{\"14M\":\"BLACK\",\"11E\":\"WHITE\"}}"
        )

        //then: deserialize the board
        val newBoard = boardSerializer.deserialize(serialized)

        //assert: the deserialized board is the same as the original
        assertEquals(board, newBoard)
    }
}