package pt.isel.daw.gomoku

object Environment {

    fun getDbUrl() = "jdbc:postgresql://localhost/DAW?user=postgres&password=Revolver-38"
        //System.getenv(KEY_DB_URL) ?: throw Exception("Missing env var $KEY_DB_URL")

    private const val KEY_DB_URL = "DB_URL"
}