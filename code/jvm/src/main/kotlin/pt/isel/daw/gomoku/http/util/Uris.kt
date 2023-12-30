package pt.isel.daw.gomoku.http.util

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    private const val PREFIX = "/api"
    const val HOME = "$PREFIX/"
    const val SYSTEM_INFO = "$PREFIX/system"
    fun home(): URI = URI(HOME)
    fun systemInfo(): URI = URI(SYSTEM_INFO)
    object Users {
        const val REGISTER = "$PREFIX/users"
        const val LOGIN = "$PREFIX/users/token"
        const val LOGOUT = "$PREFIX/logout"
        const val GET_USER_BY_ID = "$PREFIX/users/{uid}"
        const val AUTH_HOME = "$PREFIX/me"
        const val RANKING_INFO = "$PREFIX/ranking"
        const val GET_STATS_BY_USERNAME_FOR_RANKING = "$PREFIX/ranking/{name}"
        const val GET_STATS_BY_ID = "$PREFIX/stats/{uid}"
        const val GET_STATS_BY_USERNAME = "$PREFIX/stats/username/{name}"
        const val UPDATE_USER = "$PREFIX/users/update"

        fun getUsersById(id: Int) = UriTemplate(GET_USER_BY_ID).expand(id)
        fun getStatsById(id: Int) = UriTemplate(GET_STATS_BY_ID).expand(id)
        fun getStatsByUsername(name: String) = UriTemplate(GET_STATS_BY_USERNAME).expand(name)
        fun authHome(): URI = URI(AUTH_HOME)
        fun login(): URI = URI(LOGIN)
        fun logout(): URI = URI(LOGOUT)
        fun register(): URI = URI(REGISTER)
        fun rankingInfo(): URI = URI(RANKING_INFO)
        fun getStatsByUsernameForRanking(name: String) = UriTemplate(GET_STATS_BY_USERNAME_FOR_RANKING).expand(name)
        fun updateUser(): URI = URI(UPDATE_USER)
    }

    object Games {
        const val GET_GAME_BY_ID = "$PREFIX/games/{gid}"
        const val PLAY = "$PREFIX/games/{gid}/play"
        const val MATCHMAKING = "$PREFIX/games/matchmaking"
        const val GET_MATCHMAKING_STATUS = "$PREFIX/games/matchmaking/{mid}/status"
        const val LEAVE = "$PREFIX/games/{gid}/leave"
        const val GET_ALL_GAMES = "$PREFIX/games"
        const val GET_ALL_GAMES_BY_USER = "$PREFIX/games/user/{uid}"
        const val EXIT_MATCHMAKING_QUEUE = "$PREFIX/games/matchmaking/{mid}/exit"
        const val GET_ALL_VARIANTS = "$PREFIX/games/variants"

        fun byId(id: Int) = UriTemplate(GET_GAME_BY_ID).expand(id)
        fun play(id : Int): URI = UriTemplate(PLAY).expand(id)

        fun matchmaking(): URI = URI(MATCHMAKING)

        fun getMatchmakingStatus(id: Int): URI = UriTemplate(GET_MATCHMAKING_STATUS).expand(id)

        fun exitMatchmakingQueue(id: Int): URI = UriTemplate(EXIT_MATCHMAKING_QUEUE).expand(id)

        fun leave(id: Int): URI = UriTemplate(LEAVE).expand(id)

        fun getAllGames(): URI = URI(GET_ALL_GAMES)

        fun getAllGamesByUser(userId: Int): URI = UriTemplate(GET_ALL_GAMES_BY_USER).expand(userId)

        fun getAllVariants(): URI = URI(GET_ALL_VARIANTS)

    }
}
