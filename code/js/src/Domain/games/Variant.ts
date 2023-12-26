/**
 * Type representing a game variant.
 * @property {string} name - The name of the variant.
 * @property {number} boardDim - The dimensions of the game board.
 * @property {string} playRule - The rules for playing the game.
 * @property {string} openingRule - The rules for the opening moves of the game.
 * @property {number} points - The points associated with the variant.
 */
export type Variant = {
  name: string;
  boardDim: number;
  playRule: string;
  openingRule: string;
  points: number;
};
