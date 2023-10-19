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

    private data class BoardData(var kind: String = "", var piece: String = "", var moves: Moves = mapOf())

    fun serialize(data: Board): String {
        val boardData = BoardData()
         when (data) {
            is BoardOpen -> {
                boardData.kind ="Open:${data.turn.userId}:${data.variant}"
                boardData.piece = data.turn.piece.name
            }
            is BoardRun -> {
                boardData.kind ="Run:${data.turn.userId}:${data.variant}"
                boardData.piece = data.turn.piece.name
            }
            is BoardWin -> {
                boardData.kind = "Win:${data.winner.userId}"
                boardData.piece = data.winner.piece.name
            }
            is BoardDraw -> boardData.kind = "Draw"
        }
        boardData.moves = data.moves.entries.associate { (k, v) -> k to v }
        return objectMapper.writeValueAsString(boardData)
    }

    fun deserialize(stream: String): Board {
        val boardData = objectMapper.readValue(stream, BoardData::class.java)
        val info = boardData.kind.split(":")
        return when (info[0]) {
            "Open" -> {
                val playerPart = info[1]
                val variantPart = info[2]
                val player = if (playerPart.isNotEmpty()) playerPart.toInt() else -1
                val variant = if (variantPart.isNotEmpty()) Variant.valueOf(variantPart) else Variant.STANDARD
                BoardOpen(boardData.moves, Player(player, Piece.valueOf(boardData.piece)), variant)
            }
            "Run" -> {
                val playerPart = info[1]
                val variantPart = info[2]
                val player = if (playerPart.isNotEmpty()) playerPart.toInt() else -1
                val variant = if (variantPart.isNotEmpty()) Variant.valueOf(variantPart) else Variant.STANDARD
                BoardRun(boardData.moves, Player(player, Piece.valueOf(boardData.piece)), variant)
            }
            "Win" -> {
                val playerPart = boardData.kind.substringAfter(":")
                val player = if (playerPart.isNotEmpty()) playerPart.toInt() else -1
                BoardWin(boardData.moves, Player(player, Piece.valueOf(boardData.piece)))
            }
            "Draw" -> BoardDraw(boardData.moves)
            else -> error("Invalid board kind: ${boardData.kind}")
        }
    }

//    private data class BoardData(private val board: Board) {
//        val kind: String = when (board) {
//            is BoardRun -> "Run:${board.turn.userId}"
//            is BoardWin -> "Win:${board.winner.userId}"
//            is BoardDraw -> "Draw"
//        }
//        val moves: Moves = board.moves.entries.associate { (k, v) -> k to v }
//    }
}
