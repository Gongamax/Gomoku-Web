package pt.isel.daw.gomoku.http.model

data class SystemInfoOutputModel(
    val systemInfo: String = "Gomoku Royal",
    val systemAuthors: String = "Gonçalo Frutuoso, Daniel Carvalho, Diogo Guerra",
    val systemVersion: String = "0.1.2"
)

data class HomeOutputModel(val message: String = "Welcome to Gomoku Royal! Please log in to play.")
