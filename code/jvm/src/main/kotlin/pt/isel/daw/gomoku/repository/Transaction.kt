package pt.isel.daw.gomoku.repository

import pt.isel.daw.gomoku.repository.UsersRepository

interface Transaction {

    val usersRepository: UsersRepository

    // other repository types
    fun rollback()
}
