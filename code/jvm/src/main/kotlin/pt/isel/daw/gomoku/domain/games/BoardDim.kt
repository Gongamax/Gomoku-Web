package pt.isel.daw.gomoku.domain.games

enum class BoardDim {
    STANDARD,
    MODIFIED;
    fun toInt(): Int = when (this) {
        STANDARD -> 15
        MODIFIED -> 19
    }
}