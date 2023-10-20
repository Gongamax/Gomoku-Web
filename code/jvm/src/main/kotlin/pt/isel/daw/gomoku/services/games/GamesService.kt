package pt.isel.daw.gomoku.services.games

import org.springframework.stereotype.Service
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.repository.TransactionManager
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingEntry
import pt.isel.daw.gomoku.services.exceptions.NotFoundException
import pt.isel.daw.gomoku.services.users.UserCreationError
import pt.isel.daw.gomoku.utils.failure
import pt.isel.daw.gomoku.utils.success
import java.util.UUID

@Service
class GamesService(
    private val transactionManager: TransactionManager,
    private val gamesDomain: GameDomain,
) {
    fun createGame(userBlack: User, userWhite: User, variant: Variant): GameCreationResult {
        return transactionManager.run {
            if (it.usersRepository.getUserById(userBlack.id) == null ||
                it.usersRepository.getUserById(userWhite.id) == null
            ) {
                failure(GameCreationError.UserDoesNotExist)
            }
            //Needs to do an if condition to see if variant is valid
            //using something like it.gamesRepository.getVariant(variant) != null
            val gamesRepository = it.gamesRepository
            val game = gamesDomain.createGame(userBlack, userWhite, variant)
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
            if (game.state.isEnded)
                throw IllegalStateException("Game already ended")
            val newGame = if (game.playerBLACK.id == userId) game.copy(state = Game.State.PLAYER_WHITE_WON)
            else game.copy(state = Game.State.PLAYER_BLACK_WON)
            gamesRepository.updateGame(newGame)
        }
    }

    fun getAll(): List<Game> {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getAll()
        }
    }

    fun tryMatchmaking(user: User, variant: Variant): MatchmakingResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val match = gamesRepository.tryMatchmaking(user.id)
            val opponent = match?.let { it1 -> it.usersRepository.getUserById(it1.playerId) }
            if (opponent != null) {
                success(createGame(user, opponent, variant))
            } else {
                failure(MatchmakingError.NoMatchFound)
            }
        }
    }
}