package pt.isel.daw.gomoku.domain.games.variants

import pt.isel.daw.gomoku.domain.games.Piece
import pt.isel.daw.gomoku.domain.games.board.*

interface Variant {
    private val winLength: Int
        get() = 5

    private val directions: List<Pair<Direction, Direction>>
        get() = listOf(
            Pair(Direction.DOWN_LEFT, Direction.UP_RIGHT),
            Pair(Direction.DOWN_RIGHT, Direction.UP_LEFT),
            Pair(Direction.UP, Direction.DOWN),
            Pair(Direction.LEFT, Direction.RIGHT)
        )

    fun play(board: Board, cell: Cell, nextPiece: Piece, variant: Variants): Board {
        return when (board) {
            is BoardOpen -> playOpening(board, cell, nextPiece, variant)
            is BoardRun -> {
                require(board.moves[cell] == null) { "Position $cell used" }
                val moves = board.moves + (cell to board.turn)
                when {
                    board.isWin(cell, variant) -> return BoardWin(moves, winner = board.turn)
                    board.isDraw(variant) -> return BoardDraw(moves)
                    else -> return BoardRun(moves, nextPiece)
                }
            }

            is BoardWin, is BoardDraw -> error("Game over")
        }
    }

    fun playOpening(board: BoardOpen, cell: Cell, nextPiece: Piece, variant: Variants): Board =
        when (variant.openingRule) {
            OpeningRule.STANDARD -> BoardRun(board.moves + (cell to board.turn), nextPiece)
            OpeningRule.SWAP -> BoardOpen(board.moves + (cell to board.turn), nextPiece)
        }

    fun validPlay(board: Board, cell: Cell, variant: Variants): Boolean =
        when (variant.playingRule) {
            PlayingRule.STANDARD -> cell !in board.moves
            PlayingRule.THREE_AND_THREE -> isValidOnThreeAndThreeRule(board, cell)
        }

    fun isWin(board: BoardRun, cell: Cell): Boolean =
        board.moves.size >= winLength * 2 - 2 &&
                (board.moves.filter { it.value == board.turn }.keys + cell).run {
                    any { winningCell ->
                        directions.any { (forwardDir, backwardDir) ->
                            val forwardCells = countCellsForIsWin(this.toList(), winningCell, forwardDir)
                            val backwardCells = countCellsForIsWin(this.toList() ,winningCell, backwardDir)
                            val consecutiveCells = backwardCells + listOf(winningCell).size + forwardCells
                            consecutiveCells >= winLength
                        }
                    }
                }

    /*
    * in this function is not possible to play if
    * there's a move that simultaneously forms two open rows of three stones
    * (rows not blocked by an opponent's stone at either end
    * */
    fun isValidOnThreeAndThreeRule(board: Board, cell: Cell): Boolean =
        board is BoardRun && (board.moves.filter { it.value == board.turn }.keys + cell).run {
            any {
                directions.any { (forwardDir, backwardDir) ->
                    val forwardCells = cellsInDirection(it, forwardDir, size)
                        .takeWhile { it in this }
                    val backwardCells = cellsInDirection(it, backwardDir, size)
                        .takeWhile { it in this }
                    val consecutiveCells = (backwardCells + listOf(it) + forwardCells)

                    (board.moves[forwardCells.last()] != board.turn || board.moves[backwardCells.last()] != board.turn)
                            && consecutiveCells.size <= 3
                }
            }
        }

    fun isDraw(board: BoardRun, variant: Variants): Boolean =
        board.moves.size == variant.boardDim.toInt() * variant.boardDim.toInt()
}