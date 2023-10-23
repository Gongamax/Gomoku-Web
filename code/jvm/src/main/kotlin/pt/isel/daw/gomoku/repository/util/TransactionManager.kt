package pt.isel.daw.gomoku.repository.util

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
