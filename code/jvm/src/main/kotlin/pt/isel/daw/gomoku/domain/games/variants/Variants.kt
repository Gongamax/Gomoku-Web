package pt.isel.daw.gomoku.domain.games.variants

import pt.isel.daw.gomoku.domain.games.board.BoardDim


enum class Variants(
    val boardDim: BoardDim,
    val openingRule: OpeningRule,
    val playingRule: PlayingRule,
    val points: Int
) : Variant {
    STANDARD(
        boardDim = BoardDim.STANDARD,
        openingRule = OpeningRule.STANDARD,
        playingRule = PlayingRule.STANDARD,
        points = 110
    ),
    SWAP(
        boardDim = BoardDim.STANDARD,
        openingRule = OpeningRule.SWAP,
        playingRule = PlayingRule.STANDARD,
        points = 140
    ),
    RENJU(
        boardDim = BoardDim.STANDARD,
        openingRule = OpeningRule.STANDARD,
        playingRule = PlayingRule.THREE_AND_THREE,
        points = 150
    ),
    CARO(
        boardDim = BoardDim.STANDARD,
        openingRule = OpeningRule.STANDARD,
        playingRule = PlayingRule.STANDARD,
        points = 120
    ),
    PENTE(
        boardDim = BoardDim.MODIFIED,
        openingRule = OpeningRule.STANDARD,
        playingRule = PlayingRule.STANDARD,
        points = 130

    ),
    OMOK(
        boardDim = BoardDim.MODIFIED,
        openingRule = OpeningRule.STANDARD,
        playingRule = PlayingRule.THREE_AND_THREE,
        points = 170
    ),
    NINUKI_RENJU(
        boardDim = BoardDim.STANDARD,
        openingRule = OpeningRule.STANDARD,
        playingRule = PlayingRule.THREE_AND_THREE,
        points = 160
    );
}
fun String.toVariant(): Variants = Variants.valueOf(this)
