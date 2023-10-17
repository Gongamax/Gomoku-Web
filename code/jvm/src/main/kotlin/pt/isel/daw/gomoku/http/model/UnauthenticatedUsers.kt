package pt.isel.daw.gomoku.http.model

/** WARNING: ALL VALUES ARE HARD CODED FOR TEST PURPOSES **/
/** PROBABLY RANKING INFO MAY OR MAY NOT BE LIST THAT CONTAIN USERNAME, NUMBER OF PLAYS AND USER RANK FOR EACH USER **/
/** ALSO THIS CLASS COULD BE PUT ON THE DOMAIN LOGIC **/
data class RankingInfoOutputModel(
    val username: String = "Mario Matos",
    val userRank: String = "Bronze",
    val nOfPlayedGames: Int = 8,
    val wins: Int = 3,
    val losses: Int = 8
)

/** WARNING: ALL VALUES ARE HARD CODED FOR TEST PURPOSES **/
/** ALSO THIS CLASS COULD BE PUT ON THE DOMAIN LOGIC **/
data class SystemInfoOutputModel(
    val systemInfo: String = "Gomoku Game",
    val systemAuthors: String = "Diogo Guerra, Gonçalo Frutuoso, Daniel Carvalho",
    val systemVersion: String = "0.1.0"
)
