package pt.isel.daw.gomoku.domain.games.board

/**
    * Class Row represents a row in the board.
    * Each row is identified by a number.
    * @property number the number of the row
    * @property index the index of the row
 */
@JvmInline
value class Row private constructor (val number: Int) {
    val index: Int get() = number - 1
    override fun toString() = "Row $number"
    operator fun plus(offset: Int): Row = Row(this.index + offset)

    companion object {
        val INVALID = Row(-1)
        operator fun invoke(number : Int): Row = Row(number)
    }
}

//Row Extension functions
fun Int.toRowOrNull(): Row? = if (this > 0) Row(this) else null
fun Int.toRow(): Row = toRowOrNull() ?: throw IllegalArgumentException("Invalid row $this")