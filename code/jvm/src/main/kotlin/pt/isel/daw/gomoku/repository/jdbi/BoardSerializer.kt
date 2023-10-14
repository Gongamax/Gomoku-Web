package pt.isel.daw.gomoku.repository.jdbi

import pt.isel.daw.gomoku.domain.games.*

object BoardSerializer {

    fun serialize(data: Board) = buildString {
        appendLine(
            when (data) {
                is BoardRun -> "Run:${data.turn}"
                is BoardWin -> "Win:${data.winner}"
                is BoardDraw -> "Draw:-"
            }
        )
        appendLine(data.moves.entries.joinToString(" ") {
            "${it.key}:${it.value.name}"
        })
    }

    fun deserialize(stream: String): Board {
        val (header, movesLine) = stream.split("\n")
        val (kind, player) = header.split(":")
        val moves =
            if (movesLine.isEmpty()) emptyMap()
            else movesLine.split(" ").associate {
                val (k, v) = it.split(":")
                k.toCell() to Piece.valueOf(v)
            }
        return when (kind) {
            "Run" -> BoardRun(moves, Piece.valueOf(player))
            "Win" -> BoardWin(moves, Piece.valueOf(player))
            "Draw" -> BoardDraw(moves)
            else -> error("Invalid board kind: $kind")
        }
    }
}