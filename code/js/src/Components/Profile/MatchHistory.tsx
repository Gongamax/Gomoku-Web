import * as React from 'react';
import { Link, useParams } from 'react-router-dom';
import { getAllGamesByUser } from '../../Service/games/GamesServices';
import { GameOutputModel } from '../../Service/games/models/GameModelsUtil';

/**
 * `calculateResult` is a function that calculates the result of a game for a given user.
 * It checks the game state and the user's ID to determine whether the user won, lost, or if the game is still in progress.
 *
 * @param {GameOutputModel} game - The game to calculate the result for.
 * @param {number} userId - The ID of the user to calculate the result for.
 * @returns {string} - The result of the game ('WIN', 'LOSS', 'DRAW', or 'IN PROGRESS').
 */
function calculateResult(game: GameOutputModel, userId: number): string {
  switch (game.state) {
    case 'PLAYER_WHITE_WON':
      return game.userWhite.id.value === userId ? 'WIN' : 'LOSS';
    case 'PLAYER_BLACK_WON':
      return game.userBlack.id.value === userId ? 'WIN' : 'LOSS';
    case 'DRAW':
      return 'DRAW';
    default:
      return 'IN PROGRESS';
  }
}

/**
 * `MatchHistory` is a React functional component that renders the match history of a user.
 * It fetches the match history from the server when the component mounts and stores it in state.
 * The match history is displayed in a table, with each row representing a match.
 * Each row displays the opponent's username (as a link to their profile) and the result of the match.
 *
 * @returns {React.ReactElement} - The rendered match history.
 */
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