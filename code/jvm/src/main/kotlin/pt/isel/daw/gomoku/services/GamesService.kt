package pt.isel.daw.gomoku.services

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.GameDomain
import pt.isel.daw.gomoku.domain.games.Round
import pt.isel.daw.gomoku.domain.games.RoundResult
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.repository.TransactionManager
import pt.isel.daw.gomoku.services.exceptions.NotFoundException
import pt.isel.daw.gomoku.utils.Either
import pt.isel.daw.gomoku.utils.failure
import pt.isel.daw.gomoku.utils.success
import java.util.UUID

sealed class GameCreationError {
    object GameAlreadyExists : GameCreationError()
}

typealias GameCreationResult = Either<GameCreationError, Game>

@Component
@Service
class GamesService(
    private val transactionManager: TransactionManager,
    private val gamesDomain: GameDomain,
    private val clock: Clock
) {
    fun createGame(userBlack: User, userWhite: User): GameCreationResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game = gamesDomain.createGame(userBlack, userWhite)
            if (gamesRepository.getGame(game.id) != null) {
                failure(GameCreationError.GameAlreadyExists)
            } else {
                gamesRepository.createGame(game)
                success(game)
            }
        }
    }

    fun getById(id: UUID): Game? {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getGame(id) ?: throw NotFoundException("Game with id $id not found")
        }
    }

    fun play(id: UUID, round: Round): RoundResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game = gamesRepository.getGame(id) ?: return@run RoundResult.GameAlreadyEnded
            val result = gamesDomain.playRound(game, round)
            val newGame = when (result) {
                is RoundResult.YouWon -> result.game
                is RoundResult.Draw -> result.game
                is RoundResult.TooLate -> result.game
                is RoundResult.OthersTurn -> result.game
                else -> game // For other cases, keep the game unchanged
            }
            gamesRepository.updateGame(newGame)
            result
        }
    }

    fun getAll(): List<Game> {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getAll()
        }
    }

}