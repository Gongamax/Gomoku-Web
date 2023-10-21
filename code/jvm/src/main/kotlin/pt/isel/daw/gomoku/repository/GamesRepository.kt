package pt.isel.daw.gomoku.repository

import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.GameCreationModel
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingEntry

interface GamesRepository {

    fun getGame(id: Int): Game?

    fun createGame(game: GameCreationModel) : Int

    fun updateGame(game: Game): Unit

    fun deleteGame(id: Int): Unit

    fun getGamesByUser(userId : Int): List<Game>

    fun getAll() : List<Game>

    fun isGameStoredById(id: Int): Boolean

    fun tryMatchmaking(userId: Int) : MatchmakingEntry?

    fun storeMatchmakingEntry(matchmakingEntry: MatchmakingEntry) : Int

    fun exitMatchmakingQueue(id : Int) : Int
}