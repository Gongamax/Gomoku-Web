package pt.isel.daw.gomoku.domain.users

data class Email(
    val mail : String
) {
    init {
        require(mail.isNotEmpty()) { "Email cannot be empty" }
        require(mail.length <= 255) { "Email cannot be longer than 255 characters" }
        require(mail.contains("@")) { "Email must contain @" }
        require(mail.contains(".")) { "Email must contain ." }
    }
}
