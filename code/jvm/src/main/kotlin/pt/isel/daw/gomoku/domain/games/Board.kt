package pt.isel.daw.gomoku.domain.games


typealias Moves = Map<Cell, Piece>

/**
 * Represents a board of the game.
 * @property moves the map of the moves of the game.
 * @constructor Creates a board with the given [moves] that is map from [Cell] to [Piece] ([Moves]).
 * There are four possible states of board: [BoardRun], [BoardWin] and [BoardDraw]
 * These hierarchies are to be used by pattern matching.
 */
sealed class Board(val moves: Moves, val variant: Variants = Variants.STANDARD) {
    private val boardSize = variant.boardDim.toInt()
    val maxMoves = boardSize * boardSize

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        if (moves.size != (other as Board).moves.size) return false
        return when (this) {
            is BoardRun -> turn == (other as BoardRun).turn
            is BoardWin -> winner == (other as BoardWin).winner
            else -> true
        }
    }

    override fun toString(): String {
        return when (this) {
            is BoardOpen -> "Open:$turn"
            is BoardRun -> "Run:$turn"
            is BoardWin -> "Win:$winner"
            is BoardDraw -> "Draw:-"
        } + moves.entries.joinToString(" ") {
            "${it.key}:${it.value.name}"
        }
    }

    override fun hashCode(): Int = moves.hashCode()

    companion object {
        fun createBoard(piece: Piece, variant: Variants) = BoardOpen(emptyMap(), piece, variant)
    }
}

class BoardOpen(moves: Moves, val turn: Piece, variant: Variants) : Board(moves, variant)
class BoardRun(moves: Moves, val turn: Piece, variant: Variants) : Board(moves, variant )
class BoardWin(moves: Moves, val winner: Piece) : Board(moves)
class BoardDraw(moves: Moves) : Board(moves)

/**
 * Makes a move in [cell] cell by the current turn.
 * @throws IllegalArgumentException if the [cell] is already used.
 * @throws IllegalStateException if the game is over (Draw or Win).
 */
fun Board.playRound(cell: Cell, nextPiece: Piece): Board = variant.play(this, cell, nextPiece)

/**
 * Checks if the move in [cell] position is a winning move.
 */
fun BoardRun.isWin(cell: Cell) = variant.isWin(this, cell)

/**
 * Checks if the move in [cell] position is a valid move according to the play rule.
 */
fun Board.canPlayOn(cell: Cell) = variant.validPlay(this, cell)

/**
 * Checks if the state of the board will end the game as a Draw.
 */
fun BoardRun.isDraw() = moves.size == maxMoves