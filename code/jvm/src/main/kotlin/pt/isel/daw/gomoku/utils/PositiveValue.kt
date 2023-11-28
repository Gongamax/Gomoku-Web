package pt.isel.daw.gomoku.utils

data class PositiveValue (val value: Int) {
    init {
        if (value < 0) throw IllegalArgumentException("Value must be positive")
    }
}