package pt.isel.daw.gomoku.repository

interface Transaction {

    val usersRepository: UsersRepository

    // other repository types
    fun rollback()
}
