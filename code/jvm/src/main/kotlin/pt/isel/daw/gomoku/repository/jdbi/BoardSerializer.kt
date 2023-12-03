package pt.isel.daw.gomoku.repository.jdbi

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import pt.isel.daw.gomoku.domain.games.*
import com.fasterxml.jackson.databind.module.SimpleModule
import pt.isel.daw.gomoku.domain.games.board.*
import pt.isel.daw.gomoku.domain.games.variants.Variants

object CellKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(parser: String, context: DeserializationContext): Any {
        return parser.toCell()
    }
}

object VariantDeserializer : JsonDeserializer<Variants>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Variants {
        val objectMapper = parser.codec as ObjectMapper
        val node: JsonNode = objectMapper.readTree(parser)
        return Variants.valueOf(node.asText())
    }
}

object PieceDeserializer : JsonDeserializer<Piece>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Piece {
        val objectMapper = parser.codec as ObjectMapper
        val node: JsonNode = objectMapper.readTree(parser)
        return Piece.valueOf(node.asText())
    }
}

object BoardSerializer {

    private val objectMapper: ObjectMapper = ObjectMapper()

    init {
        val module = SimpleModule()
        module.addKeyDeserializer(Cell::class.java, CellKeyDeserializer)
        module.addDeserializer(Piece::class.java, PieceDeserializer)
        module.addDeserializer(Variants::class.java, VariantDeserializer)
        objectMapper.registerModule(module)
    }

    private data class BoardData(val kind: String = "", val piece: String = "", val moves: Moves = mapOf()){}

    fun serialize(data: Board): String {
        var boardData = BoardData()
        boardData = when (data) {
            is BoardOpen -> boardData.copy(kind = "Open", piece = data.turn.name)
            is BoardRun -> boardData.copy(kind = "Run", piece = data.turn.name)
            is BoardWin -> boardData.copy(kind = "Win", piece = data.winner.name)
            is BoardDraw -> boardData.copy(kind = "Draw")
        }
        boardData = boardData.copy(moves = data.moves.entries.associate { (k, v) -> k to v })
        return objectMapper.writeValueAsString(boardData)
    }

    fun deserialize(stream: String): Board {
        val boardData = objectMapper.readValue(stream, BoardData::class.java)
        val info = boardData.kind.split(":")
        return when (info[0]) {
            "Open" -> BoardOpen(boardData.moves, Piece.valueOf(boardData.piece))
            "Run" -> BoardRun(boardData.moves, Piece.valueOf(boardData.piece))
            "Win" -> BoardWin(boardData.moves, Piece.valueOf(boardData.piece))
            "Draw" -> BoardDraw(boardData.moves)
            else -> error("Invalid board kind: ${boardData.kind}")
        }
    }
}
