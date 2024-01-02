import * as React from 'react';
import { Game } from '../../../Domain/games/Game';
import { isTurnOf } from './GameUtils';

/**
 * Type representing the properties of the GameBoard component.
 * @type {Object} GameBoardProps
 * @property {Game} game - The current game.
 * @property {string[][]} board - The current state of the game board.
 * @property {(row: number, col: number) => void} onPlay - The function to be called when a play is made.
 * @property {boolean} onPlayEnabled - A flag indicating whether making a play is currently enabled.
 * @property {() => void} onResign - The function to be called when the player resigns.
 */
type GameBoardProps = {
  game?: Game;
  board?: string[][];
  onPlay?: (row: number, col: number) => void;
  onPlayEnabled: boolean;
  onResign?: () => void;
};

/**
 * Component for presenting a game.
 * @param {GameBoardProps} props - The properties of the GameBoard component.
 * @returns {React.ReactElement} The GameBoard component.
 */
export function PresentGame({ game, onPlay, onPlayEnabled, onResign }: GameBoardProps): React.ReactElement {
  const { board, turn } = deserializeBoard(game.board.moves, isTurnOf(game), game.variant.boardDim);
  return (
    <div>
      <h1>
        {game.userBlack.username} vs {game.userWhite.username}
      </h1>
      {turn && <h2>Turn: {turn}</h2>}
      <BoardPresentation board={board} onPlay={onPlay} onPlayEnabled={onPlayEnabled} />
      <button disabled={!onPlayEnabled} onClick={onResign} style={{ margin: '10px' }}>
        Surrender
      </button>
    </div>
  );
}

/**
 * Component for presenting a game board.
 * @param {GameBoardProps} props - The properties of the GameBoard component.
 * @returns {React.ReactElement} The GameBoard component.
 */
function BoardPresentation({ board, onPlay, onPlayEnabled }: GameBoardProps): React.ReactElement {
  const cellSize = 30;
  const boardSize = board.length * cellSize;
  return (
    <div style={{ position: 'relative', height: `${boardSize}px`, width: `${boardSize}px`, background: '#FFCC66' }}>
      {Array.from({ length: board.length + 1 }, (_, i) => (
        <div
          key={i}
          style={{
            position: 'absolute',
            top: `${i * cellSize}px`,
            left: '0',
            height: '1px',
            width: '100%',
            background: 'black',
          }}
        />
      ))}
      {Array.from({ length: board.length + 1 }, (_, i) => (
        <div
          key={i}
          style={{
            position: 'absolute',
            top: '0',
            left: `${i * cellSize}px`,
            height: '100%',
            width: '1px',
            background: 'black',
          }}
        />
      ))}
      {board.map((row, i) =>
        row.map((cell, j) => (
          <div
            key={`${i}-${j}`}
            onClick={() => {
              if (onPlayEnabled) onPlay(i, j);
            }}
            style={{
              position: 'absolute',
              top: `calc(${i * cellSize}px - 10px)`,
              left: `calc(${j * cellSize}px - 10px)`,
              height: '20px',
              width: '20px',
              borderRadius: '50%',
              backgroundColor: cell === 'BLACK' ? 'black' : cell === 'WHITE' ? 'white' : 'transparent',
            }}
          />
        ))
      )}
    </div>
  );
}

/**
 * Type representing the properties of the GameResult component.
 * @type {Object} GameResultProps
 * @property {string} me - The username of the current player.
 * @property {string} winner - The username of the winner, or undefined if the game was a draw.
 * @property {number} points - The number of points won or lost.
 * @property {() => void} onExit - The function to be called when the player exits the game.
 */
type GameResultProps = {
  me: string;
  winner: string | undefined;
  points: number;
  onExit: () => void;
};

/**
 * Component for presenting the result of a game.
 * @param {GameResultProps} props - The properties of the GameResult component.
 * @returns {React.ReactElement} The GameResult component.
 */
export function ResultPresentation({ me, winner, points, onExit }: GameResultProps): React.ReactElement {
  if (!winner) {
    return (
      <div>
        <h1>Game Over</h1>
        <h2>You Draw!</h2>
        <h2>You received 0 points</h2>
        <button onClick={onExit}>Exit</button>
      </div>
    );
  } else {
    return (
      <div>
        <h1>Game Over</h1>
        <h2>Player {winner} Won!</h2>
        <h2>
          You {me === winner ? 'received' : 'lost'} {points} points
        </h2>
        <button onClick={onExit}>Exit</button>
      </div>
    );
  }
}

/**
 * Type representing the properties of a game board.
 * @type {Object} BoardProps
 * @property {string[][]} board - The current state of the game board.
 * @property {string} turn - The current turn.
 */
type BoardProps = {
  board: string[][];
  turn: string;
};

/**
 * Function to deserialize a game board from a moves object.
 * @param {Object} moves - The moves made on the board.
 * @param {string} turn - The current turn.
 * @param {number} boardSize - The size of the board.
 * @returns {BoardProps} The deserialized board and the current turn.
 */
export function deserializeBoard(moves: { [key: string]: string }, turn: string, boardSize: number): BoardProps {
  const board: string[][] = Array(boardSize)
    .fill(null)
    .map(() => Array(boardSize).fill(''));
  for (const key in moves) {
    const [rowPart, colPart] = key.length === 2 ? key.split('') : [key.substring(0, 2), key.substring(2)];
    const row = parseInt(rowPart) - 1;
    const col = colPart.charCodeAt(0) - 'A'.charCodeAt(0);
    board[row][col] = moves[key];
  }

  return { board, turn };
}
