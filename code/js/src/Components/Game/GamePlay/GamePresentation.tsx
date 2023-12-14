import * as React from 'react';
import {deserializeBoard} from "./GameUtils";
import {Game} from "../../../Domain/games/Game";

export function PresentGame(
    {game, onPlay,onResign}: GameBoardProps
){
    if (game) {
        const {
            board,
            turn,
        } = deserializeBoard(
            game.board.moves,
            game.board.turn?.toString() ?? game.board.winner.toString(),
            game.variant.boardDim,
        );
        return (
            <div>
                <h1>{game.players[0].username} vs {game.players[1].username}</h1>
                <h2>Turn: {turn}</h2>
                <BoardPresentation board={board} onPlay={onPlay}/>
                <button
                    onClick={onResign}>Surrender
                </button>
            </div>
        );
    }
}

interface GameBoardProps {
  game?: Game;
  board?: string[][];
  onPlay: (row: number, col: number) => void;
  onResign?: () => void;
}

function BoardPresentation({ board, onPlay }: GameBoardProps) {
  return (
    <table style={{ borderCollapse: 'collapse' }}>
      <tbody>
      {board.map((row, i) => (
        <tr key={i}>
          {row.map((cell, j) => (
            <td key={j} onClick={() => onPlay(i, j)}
                style={{ width: '30px', height: '30px', border: '1px solid black', textAlign: 'center' }}>
              {cell === 'BLACK' ? 'B' : cell === 'WHITE' ? 'W' : ' '}
            </td>
          ))}
        </tr>
      ))}
      </tbody>
    </table>
  );
}

interface GameResultProps {
    me: string
    winner: string | undefined;
    points: number;
    onExit: () => void;
}

export function ResultPresentation({ me, winner, points, onExit }: GameResultProps){
    if (!winner){
        return (
            <div>
                <h1>Game Over</h1>
                <h2>You Draw!</h2>
                <h2>You received 0 points</h2>
                <button onClick={onExit}>Exit</button>
            </div>
        );
    }else{
        return (
            <div>
                <h1>Game Over</h1>
                <h2>Player {winner} Won!</h2>
                <h2>You { me === winner ? 'received' : 'lost' } {points} points</h2>
                <button onClick={onExit}>Exit</button>
            </div>
        );
    }
}