package pt.isel.daw.gomoku.http.model

/** WARNING: ALL VALUES ARE HARD CODED FOR TEST PURPOSES **/
/** PROBABLY RANKING INFO MAY OR MAY NOT BE LIST THAT CONTAIN USERNAME, NUMBER OF PLAYS AND USER RANK FOR EACH USER **/
/** ALSO THIS CLASS COULD BE PUT ON THE DOMAIN LOGIC **/
data class RankingInfoOutputModel(
    val username: String = "Mario Matos",
    val userRank: String = "Bronze",
    val nOfPlayedGames: String = "8"
)