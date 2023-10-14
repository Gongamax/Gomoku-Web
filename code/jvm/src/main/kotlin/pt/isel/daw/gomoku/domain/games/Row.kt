package pt.isel.daw.gomoku.domain.games

/**
    * Class Row represents a row in the board.
    * Each row is identified by a number.
    * @property number the number of the row
    * @property index the index of the row
    * @property values the list of all rows
 */
@JvmInline
value class Row private constructor (val number: Int) {
    val index: Int get() = number - 1
    override fun toString() = "Row $number"
    operator fun plus(offset: Int): Row = Row(this.index + offset)
    companion object {
        val values = List(BOARD_DIM) { Row(it + 1) }
        operator fun invoke(number: Int): Row = values.first { it.number == number }
    }
}

//Row Extension functions
fun Int.toRowOrNull(): Row? = Row.values.firstOrNull { it.number == this}
fun Int.toRow(): Row = toRowOrNull() ?: throw IllegalArgumentException("Invalid row $this")