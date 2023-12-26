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

type State = { tag: 'loading' } | { tag: 'presenting'; userInfo: UserInfo } | { tag: 'error'; message: string };

type Action =
  | { type: 'startLoading' }
  | { type: 'loadSuccess'; userInfo: UserInfo }
  | { type: 'loadError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return { tag: 'presenting', userInfo: action.userInfo };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'presenting':
    case 'error':
      logUnexpectedAction(state, action);
      return state;
  }
}

export function Me() {
  const currentUser = getUserName();
  const [state, dispatch] = React.useReducer(reducer, { tag: 'loading' });
  const [ongoingGames, setOngoingGames] = React.useState<GameSimpleInfo[]>([]);

  useEffect(() => {
    async function fetchData() {
      try {
        const userResponse = await getStatsByUsername(currentUser);
        const userInfo = {
          username: userResponse.properties.username,
          wins: userResponse.properties.wins,
          losses: userResponse.properties.losses,
          draws: userResponse.properties.gamesPlayed - (userResponse.properties.wins + userResponse.properties.losses),
          gamesPlayed: userResponse.properties.gamesPlayed,
        };
        const uid = userResponse.properties.uid;
        const ongoingGamesResponse = await getAllGamesByUser(uid);
        const games = convertToDomainGames(ongoingGamesResponse, uid);
        setOngoingGames(games.filter(game => game.result === 'IN PROGRESS'));
        dispatch({ type: 'loadSuccess', userInfo });
      } catch (error) {
        dispatch({ type: 'loadError', message: error.message });
      }
    }

    fetchData();
  }, [currentUser]);

  const navigate = useNavigate();

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'presenting':
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
              {ongoingGames.map(game => (
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
