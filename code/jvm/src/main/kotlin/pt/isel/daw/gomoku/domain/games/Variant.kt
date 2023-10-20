package pt.isel.daw.gomoku.domain.games


enum class Variant(val boardDim:BoardDim) {
    STANDARD(boardDim = BoardDim.STANDARD),
    SWAP(boardDim = BoardDim.STANDARD),
    RENJU(boardDim = BoardDim.STANDARD),
    CARO(boardDim = BoardDim.STANDARD),
    PENTE(boardDim = BoardDim.MODIFIED),
    OMOK (boardDim = BoardDim.MODIFIED),
    NINUKI_RENJU(boardDim = BoardDim.STANDARD)
}
