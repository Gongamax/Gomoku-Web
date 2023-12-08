package pt.isel.daw.gomoku.domain.games.variants

import pt.isel.daw.gomoku.domain.games.board.BoardDim

data class GameVariant(
    val name: String,
    val boardDim: BoardDim,
    val playingRule: PlayingRule,
    val openingRule: OpeningRule,
    val points: Int
)
