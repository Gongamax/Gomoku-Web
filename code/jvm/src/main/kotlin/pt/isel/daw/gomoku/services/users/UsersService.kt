package pt.isel.daw.gomoku.services.users

import kotlinx.datetime.Clock
import org.springframework.stereotype.Service
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.utils.Token
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.users.UsersDomain
import pt.isel.daw.gomoku.domain.utils.RankingEntry
import pt.isel.daw.gomoku.repository.util.TransactionManager
import pt.isel.daw.gomoku.services.others.RankingError
import pt.isel.daw.gomoku.services.others.RankingResult
import pt.isel.daw.gomoku.utils.Success
import pt.isel.daw.gomoku.utils.failure
import pt.isel.daw.gomoku.utils.success

@Service
class UsersService(
    private val transactionManager: TransactionManager,
    private val usersDomain: UsersDomain,
    private val clock: Clock
) {

    fun createUser(username: String, email: String, password: String): UserCreationResult {
        if (!usersDomain.isSafePassword(password)) {
            return failure(UserCreationError.InsecurePassword)
        }

        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)

        val emailValidation = Email(email)
        if (!usersDomain.isSafeEmail(emailValidation)) {
            return failure(UserCreationError.InsecureEmail)
        }

        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserStoredByUsername(username)) {
                failure(UserCreationError.UserAlreadyExists)
            } else {
                val id = usersRepository.storeUser(username, emailValidation, passwordValidationInfo)
                success(id)
            }
        }
    }

    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            failure(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user: User = usersRepository.getUserByUsername(username)
                ?: return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            if (!usersDomain.validatePassword(password, user.passwordValidation))
                return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            val tokenValue = usersDomain.generateTokenValue()
            val now = clock.now()
            val newToken = Token(
                usersDomain.createTokenValidationInformation(tokenValue),
                user.id,
                createdAt = now,
                lastUsedAt = now
            )
            usersRepository.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
            Success(
                TokenExternalInfo(
                    tokenValue,
                    usersDomain.getTokenExpiration(newToken)
                )
            )
        }
    }

    fun getUserById(id: Int): UserGetByIdResult {
        return transactionManager.run {
            val userAndId = it.usersRepository.getUserById(id)
            if (userAndId != null)
                success(userAndId)
            else
                failure(UserGetError.UserDoesNotExist)
        }
    }

    fun getUserByToken(token: String): UserGetByIdResult {
        if (!usersDomain.canBeToken(token)) {
            return failure(UserGetError.InvalidToken)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
            val userAndToken = usersRepository.getTokenByTokenValidationInfo(tokenValidationInfo)
            if (userAndToken != null ) {
                if (!usersDomain.isTokenTimeValid(clock, userAndToken.second))
                    return@run failure(UserGetError.TokenExpired)
                usersRepository.updateTokenLastUsed(userAndToken.second, clock.now())
                success(userAndToken.first)
            } else {
                failure(UserGetError.InvalidToken)
            }
        }
    }

    fun revokeToken(token: String): TokenRemovalResult {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        return transactionManager.run {
            val res = it.usersRepository.removeTokenByValidationInfo(tokenValidationInfo)
            if (res == 0) failure(TokenRemovalError.TokenDoesNotExist)
            else success(Unit)
        }
    }

    fun getRanking(): RankingResult =
        transactionManager.run {
            val usersRepository = it.usersRepository
            val ranking = usersRepository.getRanking().filterIndexed { index, _ ->
                index < 10
            }.map { userStats ->
                RankingEntry(
                    userStats.rank,
                    userStats.user.username,
                    userStats.points
                ) }
            if (ranking.isEmpty()) failure(RankingError.RankingDoesNotExist)
            else success(ranking)
        }

    fun getUserStatsById(id: Int): UserStatsResult =
        transactionManager.run {
            val usersRepository = it.usersRepository
            val user = usersRepository.getUserById(id)
            if (user == null) failure(UserStatsError.UserDoesNotExist)
            else {
                val userStats = usersRepository.getUserStatsById(id)
                if (userStats != null) success(userStats)
                else failure(UserStatsError.UserStatsDoesNotExist)
            }
        }
}
