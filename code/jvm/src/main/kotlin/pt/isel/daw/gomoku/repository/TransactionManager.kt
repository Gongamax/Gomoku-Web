package pt.isel.daw.gomoku.repository

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
