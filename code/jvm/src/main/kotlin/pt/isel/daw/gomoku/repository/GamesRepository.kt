package pt.isel.daw.gomoku.repository

import kotlinx.datetime.Instant
import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.GameCreationModel
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingEntry
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingStatus

interface GamesRepository {

    fun getGame(id: Int): Game?

    fun createGame(game: GameCreationModel) : Int

    fun updateGame(game: Game): Unit

    fun deleteGame(id: Int): Unit

    fun getGamesByUser(userId : Int): List<Game>

    fun getAll() : List<Game>

    fun isGameStoredById(id: Int): Boolean

    fun getMatchmakingEntry(userId: Int) : MatchmakingEntry?

    fun updateMatchmakingEntry(id: Int, status: MatchmakingStatus) : Int

    fun storeMatchmakingEntry(userId: Int, status: MatchmakingStatus, created: Instant) : Int

    fun exitMatchmakingQueue(id : Int) : Int
}