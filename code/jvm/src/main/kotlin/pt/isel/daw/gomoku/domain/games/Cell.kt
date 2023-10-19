package pt.isel.daw.gomoku.domain.games

/**
  * Class Cell represents a cell in the board.
  * Each cell is identified by a row and a column.
  * @property row the row of the cell
  * @property col the column of the cell
  * @property rowIndex the index of the row of the cell
  * @property colIndex the index of the column of the cell
  * @property values the list of all cells
 */
class Cell private constructor(private val index: Int, val boardSize: BoardDim = BoardDim.STANDARD) {
    private val rowIndex get() = index / boardSize.toInt() // same value as row.index
    private val colIndex get() = index % boardSize.toInt() // same value as col.index

    val row get() = Row.values[rowIndex]
    val col get() = Column.values[colIndex]

    override fun toString(): String =
        if (this == INVALID) "INVALID Cell" else "${row.number}${col.symbol}"

    companion object {
        private var boardDim = 0

        val values = List(boardDim) { Cell(it) }
        val INVALID = Cell(-1)
        private fun isValidCell(row: Int, col: Int): Cell =
            values.firstOrNull { it.row.index == row && it.col.index == col } ?: INVALID

        operator fun invoke(row: Int, col: Int, boardSize: BoardDim): Cell = run {
            this.boardDim = boardSize.toInt()
            isValidCell(row, col)
        }
        operator fun invoke(row: Row, col: Column): Cell = isValidCell(row.index, col.index)
    }
}

/**
 * Converts a string in format <Row Number><Column Symbol> to a cell.
 * @return The cell corresponding to the string.
 * @throws IllegalArgumentException if the string is not valid.
 */
fun String.toCell(): Cell {
    require(length == 2) { "Cell must have row and column" }
    return Cell(this[0].digitToInt().toRow(), this[1].toColumn())
}

/**
 * Converts a string in format <Row Number><Column Symbol> to a cell.
 * @return The cell corresponding to the string, or null if the string is not valid.
 */
fun String.toCellOrNull(): Cell? =
    if (length != 2) null
    else this[0].digitToInt().toRowOrNull()?.let { row ->
        this[1].toColumnOrNull()?.let { col -> Cell(row, col) }
    }

/**
 * Direction of possible lines formed by the cells.
 * @property difRow the difference in row index for the direction
 * @property difCol the difference in column index for the direction
 */
enum class Direction(val difRow: Int, val difCol: Int) {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1),
    UP_LEFT(-1, -1), UP_RIGHT(-1, 1), DOWN_LEFT(1, -1), DOWN_RIGHT(1, 1)
}

/**
 * Adds a direction to a cell resulting in a new cell.
 * @return The cell resulting or [Cell.INVALID] if the cell is out of the board.
 */
operator fun Cell.plus(dir: Direction) = Cell(row.index + dir.difRow, col.index + dir.difCol, boardSize)

/**
 * Returns the cells of the board in a line starting at [from] (excluding) in the direction [dir].
 * @param from the cell where the line starts (exclusive)
 * @param dir the direction of the line starting at [from]
 * @return The list of cells in the line.
 */
fun cellsInDirection(from: Cell, dir: Direction) = buildList {
    var cell = from
    while ((cell + dir).also { cell = it } != Cell.INVALID) add(cell)
}