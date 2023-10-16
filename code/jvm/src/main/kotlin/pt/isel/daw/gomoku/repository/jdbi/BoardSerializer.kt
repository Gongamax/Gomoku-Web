package pt.isel.daw.gomoku.repository.jdbi

import pt.isel.daw.gomoku.domain.games.*

object BoardSerializer {

    fun serialize(data: Board) = buildString {
        appendLine(
            when (data) {
                is BoardRun -> "Run:${data.turn.userId}"
                is BoardWin -> "Win:${data.winner.userId}"
                is BoardDraw -> "Draw:-"
            }
        )
        appendLine(data.moves.entries.joinToString(" ") {
            "${it.key}:${it.value.piece.name}"
        })
    }

    fun deserialize(stream: String): Board {
        val (header, movesLine) = stream.split("\n")
        val (kind, player) = header.split(":")
        var piece = Piece.BLACK
        val moves =
            if (movesLine.isEmpty()) emptyMap()
            else movesLine.split(" ").associate {
                val (k, v) = it.split(":")
                piece = Piece.valueOf(v)
                k.toCell() to Player(player.toInt(), piece)
            }

        return when (kind) {
            "Run" -> BoardRun(moves, Player(player.toInt(), piece))
            "Win" -> BoardWin(moves, Player(player.toInt(), piece))
            "Draw" -> BoardDraw(moves)
            else -> error("Invalid board kind: $kind")
        }
    }
}