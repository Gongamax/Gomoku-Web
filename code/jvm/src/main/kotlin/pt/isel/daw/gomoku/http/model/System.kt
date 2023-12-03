package pt.isel.daw.gomoku.http.model

data class SystemInfoOutputModel(
    val systemInfo: String = "Gomoku Royale",
    val systemAuthors: String = "Gonçalo Frutuoso and Daniel Carvalho",
    val systemVersion: String = "0.1.3"
)

data class HomeOutputModel(val message: String = "Welcome to Gomoku Royale! Please log in to play.")
