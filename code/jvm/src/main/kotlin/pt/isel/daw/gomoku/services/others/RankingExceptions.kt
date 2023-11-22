package pt.isel.daw.gomoku.services.others

import org.springframework.data.domain.Page
import pt.isel.daw.gomoku.domain.users.UserStatistics
import pt.isel.daw.gomoku.domain.utils.RankingEntry
import pt.isel.daw.gomoku.utils.Either

sealed class RankingError {
    object RankingDoesNotExist : RankingError()
}

typealias RankingResult = Either<RankingError, Page<UserStatistics>>