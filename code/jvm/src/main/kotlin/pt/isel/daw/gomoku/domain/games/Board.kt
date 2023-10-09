package pt.isel.daw.gomoku.domain.games

typealias Moves = Map<Cell, Player>

// Just for testing, not definitive yet
sealed class Board(val moves: Moves)
open class BoardRun(moves: Moves, val turn: Player) : Board(moves)
class BoardWin(moves: Moves, val winner: Player) : Board(moves)
class BoardDraw(moves: Moves) : Board(moves)