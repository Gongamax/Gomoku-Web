package pt.isel.daw.gomoku.domain.games.board

enum class BoardDim {
    STANDARD,
    MODIFIED;
    fun toInt(): Int = when (this) {
        STANDARD -> 15
        MODIFIED -> 19
    }

    companion object {
        fun fromInt(value: Int): BoardDim = when (value) {
            15 -> STANDARD
            19 -> MODIFIED
            else -> throw IllegalArgumentException("Invalid board dimension $value")
        }
    }
}