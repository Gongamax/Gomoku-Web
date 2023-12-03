package pt.isel.daw.gomoku.domain.games.board

/**
 * Class Cell represents a cell in the board.
 * Each cell is identified by a row and a column.
 * @property row the row of the cell
 * @property col the column of the cell
 */
data class Cell(val row: Row, val col: Column) {
    override fun toString(): String = "${row.number}${col.symbol}"
    operator fun rangeTo(cell: Cell): List<Cell> {
        val cells = mutableListOf<Cell>()
        for (r in row.number..cell.row.number) {
            for (c in col.symbol..cell.col.symbol) {
                cells.add(Cell(Row(r), Column(c)))
            }
        }
        return cells
    }

    companion object {
        operator fun invoke(rowIndex: Int, colIndex: Int) =
            Cell(Row(rowIndex), Column(colIndex))
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
operator fun Cell.plus(dir: Direction) = Cell(row.index + dir.difRow, col.index + dir.difCol)

/**
 * Returns the cells of the board in a line starting at [from] (excluding) in the direction [dir].
 * @param from the cell where the line starts (exclusive)
 * @param dir the direction of the line starting at [from]
 * @return The list of cells in the line.
 */
fun cellsInDirection(from: Cell, dir: Direction, boardDim: Int) = buildList {
    var cell = from
    val range = Cell(0, 0)..Cell(boardDim, boardDim)
    while ((cell + dir).also { cell = it } in range) add(cell)
}