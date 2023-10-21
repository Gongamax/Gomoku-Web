package pt.isel.daw.gomoku.repository.jdbi

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import pt.isel.daw.gomoku.domain.games.*
import com.fasterxml.jackson.databind.module.SimpleModule

object CellKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(parser: String, context: DeserializationContext): Any {
        return parser.toCell()
    }
}

object PlayerDeserializer : JsonDeserializer<Player>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Player {
        val objectMapper = parser.codec as ObjectMapper
        val node: JsonNode = objectMapper.readTree(parser)
        val userId = node.get("userId").asInt()
        val piece = Piece.valueOf(node.get("piece").asText())
        return Player(userId, piece)
    }
}

object BoardSerializer {

    private val objectMapper: ObjectMapper = ObjectMapper()

    init {
        val module = SimpleModule()
        module.addKeyDeserializer(Cell::class.java, CellKeyDeserializer)
        module.addDeserializer(Player::class.java, PlayerDeserializer)
        objectMapper.registerModule(module)
    }

    private data class BoardData(val kind: String = "", val piece: String = "", val moves: Moves = mapOf())

    fun serialize(data: Board): String {
        var boardData = BoardData()
        boardData = when (data) {
            is BoardOpen -> boardData.copy(kind = "Open:${data.variant.name}", piece = data.turn.name)
            is BoardRun -> boardData.copy(kind = "Run:${data.variant.name}", piece = data.turn.name)
            is BoardWin -> boardData.copy(kind = "Win", piece = data.winner.name)
            is BoardDraw -> boardData.copy(kind = "Draw")
        }
        boardData = boardData.copy( moves = data.moves.entries.associate { (k, v) -> k to v })
        return objectMapper.writeValueAsString(boardData)
    }

    fun deserialize(stream: String): Board {
        val boardData = objectMapper.readValue(stream, BoardData::class.java)
        val info = boardData.kind.split(":")
        return when (info[0]) {
            "Open" -> {
                val variantPart = info[1]
                val variant = if (variantPart.isNotEmpty()) Variants.valueOf(variantPart) else Variants.STANDARD
                BoardOpen(boardData.moves, Piece.valueOf(boardData.piece), variant)
            }
            "Run" -> {
                val variantPart = info[1]
                val variant = if (variantPart.isNotEmpty()) Variants.valueOf(variantPart) else Variants.STANDARD
                BoardRun(boardData.moves, Piece.valueOf(boardData.piece), variant)
            }
            "Win" -> BoardWin(boardData.moves, Piece.valueOf(boardData.piece))
            "Draw" -> BoardDraw(boardData.moves)
            else -> error("Invalid board kind: ${boardData.kind}")
        }
    }
}
