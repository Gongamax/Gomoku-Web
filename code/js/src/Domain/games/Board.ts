/**
 * Type representing a piece in a game.
 * @type {'BLACK' | 'WHITE'} Piece
 */
type Piece = 'BLACK' | 'WHITE';

/**
 * Type representing the moves in a game.
 * @type {Object} Moves
 * @property {Piece} key - The key represents the position on the board and the value represents the piece at that position.
 */
type Moves = {
  [key: string]: Piece;
};

/**
 * Type representing a game board.
 * @type {Object} Board
 * @property {Moves} moves - The moves made on the board.
 */
export type Board = {
  moves: Moves;
};