package pt.isel.daw.gomoku.http.model

/** WARNING: ALL VALUES ARE HARD CODED FOR TEST PURPOSES **/
/** ALSO THIS CLASS COULD BE PUT ON THE DOMAIN LOGIC **/
data class SystemInfoOutputModel(
    val systemInfo: String = "Gomoku Game",
    val systemAuthors: String = "Diogo Guerra, Gonçalo Frutuoso, Daniel Carvalho",
    val systemVersion: String = "0.1.0"
)