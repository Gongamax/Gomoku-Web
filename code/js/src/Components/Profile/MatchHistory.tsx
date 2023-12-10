import * as React from 'react';
import { Link, useParams } from 'react-router-dom';
import { getAllGamesByUser } from '../../Service/games/GamesServices';
import { GameOutputModel } from '../../Service/games/models/GameModelsUtil';

// TEMPORARY
function calculateResult(game: GameOutputModel, userId: number): string {
  if (game.userWhite.id.value === userId && game.board.winner === 'WHITE') {
    return 'WIN';
  } else if (game.userBlack.id.value === userId && game.board.winner === 'BLACK') {
    return 'WIN';
  } else if (game.board.winner === 'DRAW') {
    return 'DRAW';
  } else if (game.board.winner === undefined) {
    return 'IN PROGRESS';
  } else {
    return 'LOSS';
  }
}

export function MatchHistory() {
  const { uid } = useParams<{ uid: string }>();
  const userId = Number(uid);
  const [matchHistory, setMatchHistory] = React.useState([]);

  React.useEffect(() => {
    async function fetchData() {
      try {
        const history = await getAllGamesByUser(userId, 1);
        const gamesArray = history.entities.map((entity) => {
          const game = entity.properties as unknown as GameOutputModel;
          const opponent = game.userBlack.id.value === userId ? game.userWhite : game.userBlack;
          const result = calculateResult(game, userId);
          return {
            opponent: opponent,
            result: result,
          };
        });
        setMatchHistory(gamesArray);
      } catch (error) {
        console.error('Failed to fetch match history:', error);
      }
    }

    fetchData();
  }, [userId]);

  return (
    <div>
      <h2>Match History</h2>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
        <tr style={{ borderBottom: '1px solid #ccc' }}>
          <th style={{ padding: '10px', textAlign: 'left' }}>Opponent</th>
          <th style={{ padding: '10px', textAlign: 'left' }}>Result</th>
        </tr>
        </thead>
        <tbody>
        {matchHistory.map((match, index) => (
          <tr key={index} style={{ borderBottom: '1px solid #eee' }}>
            <td style={{ padding: '10px' }}><Link
              to={`/users/${match.opponent.id.value}`}>{match.opponent.username}</Link></td>
            <td style={{ padding: '10px' }}>{match.result}</td>
          </tr>
        ))}
        </tbody>
      </table>
    </div>
  );
}