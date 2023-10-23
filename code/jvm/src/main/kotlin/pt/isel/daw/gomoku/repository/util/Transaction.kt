package pt.isel.daw.gomoku.repository.util

interface Transaction {

    val usersRepository: UsersRepository

    val gamesRepository: GamesRepository

    // other repository types
    fun rollback()
}
