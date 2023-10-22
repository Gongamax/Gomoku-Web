package pt.isel.daw.gomoku.services.games

import kotlinx.datetime.Clock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.isel.daw.gomoku.domain.games.*
import pt.isel.daw.gomoku.repository.TransactionManager
import pt.isel.daw.gomoku.repository.jdbi.MatchmakingStatus
import pt.isel.daw.gomoku.utils.failure
import pt.isel.daw.gomoku.utils.success

@Service
class GamesService(
    private val transactionManager: TransactionManager,
    private val gamesDomain: GameDomain,
) {
    fun createGame(userBlackId: Int, userWhiteId: Int, variant: String): GameCreationResult {
        return transactionManager.run {
            val userBlack = it.usersRepository.getUserById(userBlackId)
            val userWhite = it.usersRepository.getUserById(userWhiteId)
            if (userBlack == null || userWhite == null)
                return@run failure(GameCreationError.UserDoesNotExist)

            if (variant !in Variants.values().map { variant -> variant.name })
                return@run failure(GameCreationError.VariantDoesNotExist)

            val gamesRepository = it.gamesRepository
            val gameModel = gamesDomain.createGameModel(userBlack, userWhite, Variants.valueOf(variant))
            val id = gamesRepository.createGame(gameModel)
            success(id)
        }
    }

    fun getGameById(id: Int): GameGetResult {
        return transactionManager.run {
            val game = it.gamesRepository.getGame(id)
            if (game == null)
                failure(GameGetError.GameDoesNotExist)
            else
                success(game)
        }
    }

    fun play(id: Int, userId: Int, row: Int, column: Int): GamePlayResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val usersRepository = it.usersRepository
            val game = gamesRepository.getGame(id) ?: return@run failure(GamePlayError.GameDoesNotExist)

            val user = usersRepository.getUserById(userId) ?: return@run failure(GamePlayError.InvalidUser)
            val piece = if (game.playerBLACK.id == user.id) Piece.BLACK else Piece.WHITE
            val round = Round(Cell(row, column), Player(user.id, piece))
            val result = gamesDomain.playRound(game, round)

            val newGame = when (result) {
                is RoundResult.YouWon -> result.game
                is RoundResult.Draw -> result.game
                is RoundResult.TooLate -> result.game
                is RoundResult.OthersTurn -> result.game
                else -> game // For other cases, keep the game unchanged
            }
            gamesRepository.updateGame(newGame)

            return@run when (result) {
                is RoundResult.YouWon -> success(result.game)
                is RoundResult.Draw -> success(result.game)
                is RoundResult.OthersTurn -> success(result.game)
                is RoundResult.TooLate -> failure(GamePlayError.InvalidTime)
                is RoundResult.PositionNotAvailable -> failure(GamePlayError.InvalidPosition)
                is RoundResult.GameAlreadyEnded -> failure(GamePlayError.InvalidState)
                is RoundResult.NotAPlayer -> failure(GamePlayError.InvalidUser)
                is RoundResult.NotYourTurn -> failure(GamePlayError.InvalidTurn)
            }
        }
    }

    fun leaveGame(id: Int, userId: Int) : LeaveGameResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val game = gamesRepository.getGame(id) ?: return@run failure(LeaveGameError.GameDoesNotExist)

            if (game.playerBLACK.id.value != userId && game.playerWHITE.id.value != userId)
                return@run failure(LeaveGameError.InvalidUser)

            if (game.state.isEnded)
                failure(LeaveGameError.GameAlreadyEnded)
            else {
                val newGame = if (game.playerBLACK.id.value == userId) game.copy(state = Game.State.PLAYER_WHITE_WON)
                else game.copy(state = Game.State.PLAYER_BLACK_WON)
                success(gamesRepository.updateGame(newGame))
            }
        }
    }

    fun getGamesOfUser(userId: Int): GameListResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            val usersRepository = it.usersRepository
            usersRepository.getUserById(userId) ?: return@run failure(GameListError.UserDoesNotExist)
            val games = gamesRepository.getGamesByUser(userId)
            success(games)
        }
    }

    fun getAll(): List<Game> {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            gamesRepository.getAll()
        }
    }

    @Transactional
    fun tryMatchmaking(userId: Int, variant: String): MatchmakingResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            if (variant !in Variants.values().map { variant -> variant.name })
                return@run failure(MatchmakingError.VariantDoesNotExist)
            val match = gamesRepository.getMatchmakingEntry(userId)

            if (match != null && match.status == MatchmakingStatus.PENDING) {
                // Match found
                gamesRepository.updateMatchmakingEntry(match.id, MatchmakingStatus.MATCHED)

                val opponent = it.usersRepository.getUserById(match.userId)
                if (opponent != null) {
                    val user = it.usersRepository.getUserById(userId)!!
                    val gameModel = gamesDomain.createGameModel(user, opponent, Variants.valueOf(variant))
                    val id = gamesRepository.createGame(gameModel)
                    success(id)
                }
                else
                    failure(MatchmakingError.InvalidUser)
            } else {
                gamesRepository.storeMatchmakingEntry(userId, MatchmakingStatus.PENDING, Clock.System.now())
                failure(MatchmakingError.NoMatchFound)
            }
        }
    }

    @Transactional
    fun exitMatchmakingQueue(userId: Int): LeaveMatchmakingResult {
        return transactionManager.run {
            val gamesRepository = it.gamesRepository
            if (it.usersRepository.getUserById(userId) == null)
                return@run failure(LeaveMatchmakingError.InvalidUser)
            val match = gamesRepository.getMatchmakingEntry(userId)
            if (match != null) {
                gamesRepository.exitMatchmakingQueue(match.id)
                success(Unit)
            } else
                failure(LeaveMatchmakingError.MatchDoesNotExist)
        }
    }
}