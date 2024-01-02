import * as React from 'react';
import { useEffect } from 'react';
import { getUserName } from '../Authentication/RequireAuthn';
import { getStatsByUsername } from '../../Service/users/UserServices';
import { UserInfo } from '../../Domain/users/UserInfo';
import { getAllGamesByUser } from '../../Service/games/GamesServices';
import { calculateResult } from '../Profile/ProfileUtil';
import { GetAllGamesByUserOutput } from '../../Service/games/models/GetUserGamesOutput';
import { GameOutputModel } from '../../Service/games/models/GameModelsUtil';
import { User } from '../../Domain/users/User';
import { useNavigate } from 'react-router-dom';
import { GetStatsOutput } from '../../Service/users/models/GetStatsOutput';
import { isProblem } from '../../Service/media/Problem';

type State =
  | { tag: 'loading' }
  | { tag: 'present'; userInfo: UserInfo; ongoingGames: GameSimpleInfo[] }
  | { tag: 'error'; message: string };

type Action =
  | { type: 'loadSuccess'; userInfo: UserInfo; ongoingGames: GameSimpleInfo[] }
  | { type: 'loadError'; message: string };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type} on state '${state.tag}'`);
}

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return { tag: 'present', userInfo: action.userInfo, ongoingGames: action.ongoingGames };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'present':
    case 'error':
      logUnexpectedAction(state, action);
      return state;
  }
}

export function Me() {
  const currentUser = getUserName();
  const [state, dispatch] = React.useReducer(reducer, { tag: 'loading' });

  useEffect(() => {
    async function fetchData() {
      try {
        const userResponse: GetStatsOutput = await getStatsByUsername(currentUser);
        const userInfo: UserInfo = createUserInfo(userResponse);
        const ongoingGames: GameSimpleInfo[] = await fetchOngoingGames(userResponse.properties.uid);
        dispatch({ type: 'loadSuccess', userInfo, ongoingGames });
      } catch (error) {
        dispatch({ type: 'loadError', message: isProblem(error) ? error.detail : error.message });
      }
    }

    fetchData();
  }, [currentUser]);

  const navigate = useNavigate();
  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'present':
      return (
        <div>
          <h1>Hello {state.userInfo.username}</h1>
          <p>Wins: {state.userInfo.wins}</p>
          <p>Losses: {state.userInfo.losses}</p>
          <p>Draws: {state.userInfo.draws}</p>
          <p>Games Played: {state.userInfo.gamesPlayed}</p>
          <h2>Ongoing Games</h2>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid #ccc' }}>
                <th style={{ padding: '10px', textAlign: 'left' }}>Game ID</th>
                <th style={{ padding: '10px', textAlign: 'left' }}>Opponent</th>
                <th style={{ padding: '10px', textAlign: 'left' }}>Result</th>
                <th style={{ padding: '10px', textAlign: 'left' }}>Join</th>
              </tr>
            </thead>
            <tbody>
              {state.ongoingGames.map(game => (
                <tr key={game.id} style={{ borderBottom: '1px solid #ddd' }}>
                  <td style={{ padding: '10px' }}>{game.id}</td>
                  <td style={{ padding: '10px' }}>{game.opponent.username}</td>
                  <td style={{ padding: '10px' }}>{game.result}</td>
                  <td style={{ padding: '10px' }}>
                    <button onClick={() => navigate(`/game/${game.id}`)}>Join</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      );

    case 'error':
      return <div>Error: {state.message}</div>;

    default:
      return <div>Unexpected state</div>;
  }
}

// Auxiliary functions

type GameSimpleInfo = {
  id: number;
  opponent: User;
  result: string;
};

function convertToDomainGames(response: GetAllGamesByUserOutput, userId: number): GameSimpleInfo[] {
  return response.entities.map(entity => {
    const game = entity.properties as unknown as GameOutputModel;
    const opponent = game.userBlack.id.value === userId ? game.userWhite : game.userBlack;
    const result = calculateResult(game, userId);
    return {
      id: game.id,
      opponent: opponent,
      result: result,
    };
  });
}

function createUserInfo(userResponse: GetStatsOutput): UserInfo {
  const { username, wins, losses, gamesPlayed } = userResponse.properties;
  const draws = gamesPlayed - (wins + losses);
  return { username, wins, losses, draws, gamesPlayed };
}

async function fetchOngoingGames(uid: number): Promise<GameSimpleInfo[]> {
  const ongoingGamesResponse = await getAllGamesByUser(uid);
  const games = convertToDomainGames(ongoingGamesResponse, uid);
  return games.filter(game => game.result === 'IN PROGRESS');
}
