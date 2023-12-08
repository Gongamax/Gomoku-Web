export type Move = {
  cell: number;
  piece: Piece;
};

type Piece = 'BLACK' | 'WHITE' | ' ';

export class Board {
  readonly moves: Move[];
}
