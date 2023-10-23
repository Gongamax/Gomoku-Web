package pt.isel.daw.gomoku.domain.games

interface Variant {
    private val WIN_LENGTH: Int
        get() = 5

    private val directions: List<Pair<Direction, Direction>>
        get() = listOf(
            Pair(Direction.DOWN_LEFT, Direction.UP_RIGHT),
            Pair(Direction.DOWN_RIGHT, Direction.UP_LEFT),
            Pair(Direction.UP, Direction.DOWN),
            Pair(Direction.LEFT, Direction.RIGHT)
        )

    fun play(board: Board, cell: Cell, nextPiece: Piece): Board {
        return when (board) {
            is BoardOpen -> playOpening(board, cell, nextPiece)
            is BoardRun -> {
                require(board.moves[cell] == null) { "Position $cell used" }
                val moves = board.moves + (cell to board.turn)
                when {
                    board.isWin(cell) -> return BoardWin(moves, winner = board.turn)
                    board.isDraw() ->  return BoardDraw(moves)
                    else -> return BoardRun(moves, nextPiece, board.variant)
                }
            }
            is BoardWin, is BoardDraw -> error("Game over")
        }
    }
    fun playOpening(board: BoardOpen, cell: Cell, nextPiece: Piece): Board =
        when (board.variant.openingRule) {
            OpeningRule.STANDARD -> BoardRun(board.moves + (cell to board.turn), nextPiece, board.variant)
            OpeningRule.SWAP -> BoardOpen(board.moves + (cell to board.turn), nextPiece, board.variant)
        }
    fun validPlay(board: Board, cell: Cell): Boolean =
        when (board.variant.playingRule) {
            PlayingRule.STANDARD -> cell !in board.moves
            PlayingRule.THREE_AND_THREE -> isValidOnThreeAndThreeRule(board, cell)
        }



    fun isWin(board: BoardRun, cell: Cell): Boolean =
        board.moves.size >= WIN_LENGTH * 2 - 2 &&
                (board.moves.filter { it.value == board.turn }.keys + cell).run {
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

    /*
    * in this function is not possible to play if
    * theres a move that simultaneously forms two open rows of three stones
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
}