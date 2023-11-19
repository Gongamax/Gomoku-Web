package pt.isel.daw.gomoku.services.others

import pt.isel.daw.gomoku.domain.users.UserStatistics
import pt.isel.daw.gomoku.domain.utils.RankingEntry
import pt.isel.daw.gomoku.utils.Either

sealed class RankingError {
    object RankingDoesNotExist : RankingError()
}

typealias RankingResult = Either<RankingError, List<UserStatistics>>