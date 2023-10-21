package pt.isel.daw.gomoku.domain.games

interface Variant {
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

    fun isValidOnThreeAndThreeRule(board: Board, cell: Cell): Boolean {
        return true
    }
}