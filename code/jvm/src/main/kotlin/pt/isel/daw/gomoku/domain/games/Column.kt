package pt.isel.daw.gomoku.domain.games

/**
  * Class Column represents a column in the board.
  * Each column is identified by a symbol.
  * @property symbol the symbol of the column
  * @property index the index of the column
  * @property values the list of all columns
 */

@JvmInline
value class Column private constructor (val symbol: Char) {
    val index: Int get() = symbol - 'A'
    override fun toString() = "Column $symbol"
    operator fun plus(offset: Int): Column = Column((this.index + offset + 'a'.code).toChar())

    companion object {
        private var boardDim = 0
        val values = List(boardDim) { Column('A' + it) }
        operator fun invoke(symbol: Char, boardDim: BoardDim): Column = run {
            this.boardDim = boardDim.toInt()
            values.first { it.symbol == symbol }
        }
    }
}

//Column Extension functions
fun Char.toColumnOrNull(): Column? = Column.values.firstOrNull { it.symbol == this }
fun Char.toColumn(): Column = toColumnOrNull() ?: throw IllegalArgumentException("Invalid column $this")