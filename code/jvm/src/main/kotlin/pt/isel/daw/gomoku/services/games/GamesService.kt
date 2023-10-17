package pt.isel.daw.gomoku.services.games

import org.springframework.stereotype.Service
import pt.isel.daw.gomoku.domain.games.Game
import pt.isel.daw.gomoku.domain.games.GameDomain
import pt.isel.daw.gomoku.domain.games.Round
import pt.isel.daw.gomoku.domain.games.RoundResult
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.repository.TransactionManager
import pt.isel.daw.gomoku.services.exceptions.NotFoundException
import pt.isel.daw.gomoku.utils.failure
import pt.isel.daw.gomoku.utils.success
import java.util.UUID

@Service
class GamesService(
    private val transactionManager: TransactionManager,
    private val gamesDomain: GameDomain,
) {
    fun createGame(userBlack: User, userWhite: User): GameCreationResult {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userBlack.id) == null ||
                it.usersRepository.getUserById(userWhite.id) == null
            ) {
                failure(GameCreationError.UserDoesNotExist)
            }
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

    fun getGameById(id: UUID): Game {
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

    fun leaveGame(id: UUID, userId: Int) {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game = getGameById(id)
            val newGame = if (game.playerBLACK.id == userId) game.copy(state = Game.State.PLAYER_WHITE_WON)
            else game.copy(state = Game.State.PLAYER_BLACK_WON)
            if (newGame.state.isEnded)
                throw IllegalStateException("Game already ended")
            else
                gamesRepository.updateGame(newGame)
        }
    }

    fun getAll(): List<Game> {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getAll()
        }
    }

    fun getGameStateById(id: UUID): Game.State {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getGameState(id) ?: throw NotFoundException("Game with id $id not found")
        }
    }
}