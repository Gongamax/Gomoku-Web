package pt.isel.daw.gomoku.repository

import pt.isel.daw.gomoku.domain.games.Game
import java.util.UUID

interface GamesRepository {

    fun getGame(gameId: UUID): Game?

    fun createGame(game: Game) : Unit

    fun updateGame(game: Game): Unit

    //TODO: ADD MORE METHODS
}