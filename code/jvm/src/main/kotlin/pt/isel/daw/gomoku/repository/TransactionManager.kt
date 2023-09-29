package pt.isel.daw.tictactoe.repository

import pt.isel.daw.gomoku.repository.Transaction

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
