type Piece = 'BLACK' | 'WHITE' | ' ';

interface Moves {
  [key: string]: Piece;
}

class Board {
  readonly moves: Moves;
  readonly winner?: Piece;
  readonly turn?: Piece;

  constructor(moves: Moves, winnerOrTurn: Piece) {
    this.moves = moves;
    if (winnerOrTurn === 'BLACK' || winnerOrTurn === 'WHITE') {
      this.turn = winnerOrTurn;
    } else {
      this.winner = winnerOrTurn;
    }
  }
}