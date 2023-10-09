package pt.isel.daw.gomoku.domain.games

// Just for testing, not definitive yet
data class Cell(
    val x: Int,
    val y: Int
)
// Just for testing, not definitive yet
fun String.toCell(): Cell {
    val (x, y) = this.split(",")
    return Cell(x.toInt(), y.toInt())
}