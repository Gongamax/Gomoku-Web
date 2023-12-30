package pt.isel.daw.gomoku.repository.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.slf4j.LoggerFactory
import pt.isel.daw.gomoku.domain.games.EndGameResult
import pt.isel.daw.gomoku.domain.users.Email
import pt.isel.daw.gomoku.domain.users.PasswordValidationInfo
import pt.isel.daw.gomoku.domain.utils.Token
import pt.isel.daw.gomoku.domain.utils.TokenValidationInfo
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.users.UserStatistics
import pt.isel.daw.gomoku.domain.utils.Id
import pt.isel.daw.gomoku.repository.util.UsersRepository

class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from dbo.Users where username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserById(id: Int): User? =
        handle.createQuery("select * from dbo.Users where id = :id")
            .bind("id", id)
            .mapTo<User>()
            .singleOrNull()

    override fun updateUser(user: User): Int =
        handle.createUpdate(
            """
            update dbo.Users
            set username=:username, email=:email, password_validation=:password_validation
            where id=:id
        """
        )
            .bind("id", user.id.value)
            .bind("username", user.username)
            .bind("email", user.email.value)
            .bind("password_validation", user.passwordValidation.validationInfo)
            .execute()

    override fun updateUserStats(id: Int, opponent: Int, result: EndGameResult) : Int =
        when (result) {
            EndGameResult.WIN ->
                handle.createUpdate(
                    """
                            update dbo.Statistics
                            set games_played = games_played + 1,
                                wins = case when user_id = :id then wins + 1 else wins end,
                                losses = case when user_id = :opponent then losses + 1 else losses end
                            where user_id in (:id, :opponent)
                        """
                )
                    .bind("id", id)
                    .bind("opponent", opponent)
                    .execute()

            EndGameResult.DRAW ->
                handle.createUpdate(
                    """
                            update dbo.Statistics
                            set games_played = games_played + 1,
                                draws = draws + 1
                            where user_id in (:id, :opponent)
                        """
                )
                    .bind("id", id)
                    .bind("opponent", opponent)
                    .execute()
        }

    override fun getUserStatsById(id: Int): UserStatistics? =
        handle.createQuery(
            """
                select users.id, users.username, users.email, users.password_validation, games_played, wins, losses, draws, rank, points
                from dbo.Users as users
                inner join dbo.Statistics as statistics
                on users.id = statistics.user_id
                where users.id = :id
            """.trimIndent()
        )
            .bind("id", id)
            .mapTo<UserStatsDBModel>()
            .singleOrNull()?.run {
                toUserStatistics()
            }

    override fun getUserStatsByUsername(username: String): UserStatistics? =
        handle.createQuery(
            """
                select users.id, users.username, users.email, users.password_validation, games_played, wins, losses, draws, rank, points
                from dbo.Users as users
                inner join dbo.Statistics as statistics
                on users.id = statistics.user_id
                where users.username = :username
            """.trimIndent()
        )
            .bind("username", username)
            .mapTo<UserStatsDBModel>()
            .singleOrNull()?.run {
                toUserStatistics()
            }


    override fun storeUser(username: String, email: Email, passwordValidation: PasswordValidationInfo): Int =
        handle.createUpdate(
            """
            insert into dbo.Users (username, email, password_validation) values (:username, :email, :password_validation)
            """
        )
            .bind("username", username)
            .bind("email", email.value)
            .bind("password_validation", passwordValidation.validationInfo)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("select count(*) from dbo.Users where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun createToken(token: Token, maxTokens: Int) {
        val deletions = handle.createUpdate(
            """
            delete from dbo.Tokens 
            where user_id = :user_id 
                and token_validation in (
                    select token_validation from dbo.Tokens where user_id = :user_id 
                        order by last_used_at desc offset :offset
                )
            """.trimIndent()
        )
            .bind("user_id", token.userId.value)
            .bind("offset", maxTokens - 1)
            .execute()

        logger.info("{} tokens deleted when creating new token", deletions)

        handle.createUpdate(
            """
                insert into dbo.Tokens(user_id, token_validation, created_at, last_used_at) 
                values (:user_id, :token_validation, :created_at, :last_used_at)
            """.trimIndent()
        )
            .bind("user_id", token.userId.value)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("created_at", token.createdAt.epochSeconds)
            .bind("last_used_at", token.lastUsedAt.epochSeconds)
            .execute()
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle.createUpdate(
            """
                update dbo.Tokens
                set last_used_at = :last_used_at
                where token_validation = :validation_information
            """.trimIndent()
        )
            .bind("last_used_at", now.epochSeconds)
            .bind("validation_information", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle.createQuery(
            """
                select id, username, email, password_validation, token_validation, created_at, last_used_at
                from dbo.Users as users 
                inner join dbo.Tokens as tokens 
                on users.id = tokens.user_id
                where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        return handle.createUpdate(
            """
                delete from dbo.Tokens
                where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun getAllUsers(): List<User> =
        handle.createQuery("select * from dbo.Users")
            .mapTo<User>()
            .list()

    override fun getStatsByUsernameForRanking(username: String): List<UserStatistics> =
        handle.createQuery(
            """
            select users.id, users.username, users.email, users.password_validation, games_played, wins, losses, draws, rank, points
            from dbo.Users as users
            inner join dbo.Statistics as statistics
            on users.id = statistics.user_id and statistics.rank > 0
            where users.username LIKE :username
            order by rank 
        """.trimIndent()
        )
            .bind("username", "%$username%")
            .mapTo<UserStatsDBModel>()
            .list()
            .map { it.toUserStatistics() }

    override fun getAllStats(): List<UserStatistics> =
        handle.createQuery(
            """
                select users.id, users.username, users.email, users.password_validation, games_played, wins, losses, draws, rank, points
                from dbo.Users as users
                inner join dbo.Statistics as statistics
                on users.id = statistics.user_id and statistics.rank > 0
                order by rank asc
            """.trimIndent()
        )
            .mapTo<UserStatsDBModel>()
            .map { it.toUserStatistics() }
            .list()


    private data class UserAndTokenModel(
        val id: Int,
        val username: String,
        val email: Email,
        val passwordValidation: PasswordValidationInfo,
        val tokenValidation: TokenValidationInfo,
        val createdAt: Long,
        val lastUsedAt: Long
    ) {
        val userAndToken: Pair<User, Token>
            get() = Pair(
                User(Id(id), username, email, passwordValidation),
                Token(
                    tokenValidation,
                    Id(id),
                    Instant.fromEpochSeconds(createdAt),
                    Instant.fromEpochSeconds(lastUsedAt)
                )
            )
    }

    private data class UserStatsDBModel(
        val id: Int,
        val username: String,
        val email: String,
        @ColumnName("password_validation")
        val passwordValidation: String,
        @ColumnName("games_played")
        val gamesPlayed: Int,
        val wins: Int,
        val losses: Int,
        val draws: Int,
        val rank: Int,
        val points: Int
    ) {
        fun toUserStatistics(): UserStatistics =
            UserStatistics(
                User(Id(id), username, Email(email), PasswordValidationInfo(passwordValidation)),
                gamesPlayed,
                wins,
                losses,
                draws,
                rank,
                points
            )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JdbiUsersRepository::class.java)
    }
}
