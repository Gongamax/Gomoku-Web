import * as React from 'react';

interface GameBoardProps {
  board: string[][];
  onPlay: (row: number, col: number) => void;
}

export function GameBoard({ board, onPlay }: GameBoardProps) {
  return (
    <table style={{ borderCollapse: 'collapse' }}>
      <tbody>
      {board.map((row, i) => (
        <tr key={i}>
          {row.map((cell, j) => (
            <td key={j} onClick={() => onPlay(i + 1, j)}
                style={{ width: '30px', height: '30px', border: '1px solid black', textAlign: 'center' }}>
              {cell === 'BLACK' ? '#' : cell === 'WHITE' ? '*' : '-'}
            </td>
          ))}
        </tr>
      ))}
      </tbody>
    </table>
  );
}