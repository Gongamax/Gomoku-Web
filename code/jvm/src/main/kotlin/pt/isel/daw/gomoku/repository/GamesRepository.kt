package pt.isel.daw.gomoku.repository

import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingEntry
import java.util.UUID

interface GamesRepository {

    fun getGame(id: UUID): Game?

    fun createGame(game: Game) : Unit

    fun updateGame(game: Game): Unit

    fun deleteGame(id: UUID): Unit

    fun getGamesByUser(userId : Int): List<Game>

    fun getAll() : List<Game>

    fun tryMatchmaking(userId: Int) : MatchmakingEntry?

    fun storeMatchmakingEntry(matchmakingEntry: MatchmakingEntry) : Int

    fun exitMatchmakingQueue(id : Int) : Int
}