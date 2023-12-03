package pt.isel.daw.gomoku.domain.games.board

/**
  * Class Column represents a column in the board.
  * Each column is identified by a symbol.
  * @property symbol the symbol of the column
  * @property index the index of the column
 */

@JvmInline
value class Column private constructor (val symbol: Char) {
    val index: Int get() = symbol - 'A'
    override fun toString() = "Column $symbol"
    operator fun plus(offset: Int): Column = Column((this.index + offset + 'a'.code).toChar())

    companion object {
        operator fun invoke(symbol: Char): Column = Column(symbol)

        operator fun invoke(index: Int): Column = Column((index + 'A'.code).toChar())
    }
}

//Column Extension functions
fun Char.toColumnOrNull(): Column? = if (this in 'A'..'Z') Column(this) else null
fun Char.toColumn(): Column = toColumnOrNull() ?: throw IllegalArgumentException("Invalid column $this")