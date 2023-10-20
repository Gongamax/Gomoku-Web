package pt.isel.daw.gomoku.domain.games

private const val WIN_LENGTH = 5
typealias Moves = Map<Cell, Player>

/**
 * Represents a board of the game.
 * @property moves the map of the moves of the game.
 * @constructor Creates a board with the given [moves] that is map from [Cell] to [Piece] ([Moves]).
 * There are four possible states of board: [BoardRun], [BoardWin] and [BoardDraw]
 * These hierarchies are to be used by pattern matching.
 */
sealed class Board(val moves: Moves, val variant: Variant = Variant.STANDARD) {
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
            "${it.key}:${it.value.piece.name}"
        }
    }

    override fun hashCode(): Int = moves.hashCode()

    companion object {
        fun createBoard(player: Player, variant: Variant) = BoardRun(emptyMap(), player, variant)
    }
}

open class BoardOpen(moves: Moves, val turn: Player, variant: Variant) : Board(moves, variant)
open class BoardRun(moves: Moves, val turn: Player, variant: Variant) : Board(moves, variant )
class BoardWin(moves: Moves, val winner: Player) : Board(moves)
class BoardDraw(moves: Moves) : Board(moves)

/**
 * Makes a move in [cell] cell by the current turn.
 * @throws IllegalArgumentException if the [cell] is already used.
 * @throws IllegalStateException if the game is over (Draw or Win).
 */
fun Board.playRound(cell: Cell, nextPlayer: Player): Board {
    return when (this) {
        is BoardOpen -> BoardRun(moves, nextPlayer, variant)
        is BoardRun -> {
            require(moves[cell] == null) { "Position $cell used" }
            val moves = moves + (cell to turn)
            when {
                isWin(cell) -> BoardWin(moves, winner = turn)
                isDraw() -> BoardDraw(moves)
                else -> BoardRun(moves, nextPlayer, variant)
            }
        }

        is BoardWin, is BoardDraw -> error("Game over")
    }
}

/**
 * Checks if the move in [cell] position is a winning move.
 */
private fun BoardRun.isWin(cell: Cell) =
    moves.size >= WIN_LENGTH * 2 - 2 &&
            (moves.filter { it.value == turn }.keys + cell).run {
                any { winningCell ->
                    directions.any { (forwardDir, backwardDir) ->
                        val forwardCells = cellsInDirection(winningCell, forwardDir, size)
                            .takeWhile { it in this }
                        val backwardCells = cellsInDirection(winningCell, backwardDir, size)
                            .takeWhile { it in this }

                        val consecutiveCells = (backwardCells + listOf(winningCell) + forwardCells)

                        consecutiveCells.size >= WIN_LENGTH
                    }
                }
            }

fun Board.canPlayOn(cell: Cell) = (cell !in moves)

private val directions = listOf(
    Pair(Direction.DOWN_LEFT, Direction.UP_RIGHT),
    Pair(Direction.DOWN_RIGHT, Direction.UP_LEFT),
    Pair(Direction.UP, Direction.DOWN),
    Pair(Direction.LEFT, Direction.RIGHT)
)

/**
 * Checks if the state of the board will end the game as a Draw.
 */
private fun BoardRun.isDraw() = moves.size == maxMoves