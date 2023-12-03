import * as React from 'react';
import { useEffect } from 'react';
import { useParams } from 'react-router-dom';

// Simulating API request
const fetchUserInfo = (userId: string) => {
  return new Promise<UserInfo>(resolve => {
    setTimeout(() => {
      // Simulated user information
      const user = {
        username: `Player ${userId}`,
        wins: 10,
        losses: 5,
        draws: 3,
        gamesPlayed: 18,
        matchHistory: [
          { opponent: 'Player 1', result: 'Win' },
          { opponent: 'Player 2', result: 'Loss' },
          { opponent: 'Player 3', result: 'Draw' },
        ],
      };
      resolve(user);
    }, 2000); // Simulating a 2-second delay for the API request
  });
};

//////////////////////////////////////////////////////////////////////////////////////////

type UserInfo = {
  username: string;
  wins: number;
  losses: number;
  draws: number;
  gamesPlayed: number;
  matchHistory: { opponent: string; result: string }[];
};

type State = { tag: 'loading' } | { tag: 'presenting'; userInfo: UserInfo } | { tag: 'error'; message: string };

type Action =
  | { type: 'startLoading' }
  | { type: 'loadSuccess'; userInfo: UserInfo }
  | { type: 'loadError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

const reduce = (state: State, action: Action): State => {
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
};

export function ProfilePage() {
  const { uid } = useParams<{ uid: string }>();
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });

  useEffect(() => {
    const fetchData = async () => {
      try {
        dispatch({ type: 'startLoading' });
        const userInfo = await fetchUserInfo(uid);
        dispatch({ type: 'loadSuccess', userInfo });
      } catch (error) {
        dispatch({ type: 'loadError', message: error.message });
      }
    };

    fetchData();
  }, [uid]);

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'presenting':
      return (
        <div>
          <h1>User Profile: {state.userInfo.username}</h1>
          <p>Wins: {state.userInfo.wins}</p>
          <p>Losses: {state.userInfo.losses}</p>
          <p>Draws: {state.userInfo.draws}</p>
          <p>Games Played: {state.userInfo.gamesPlayed}</p>

          <h2>Match History</h2>
          <ul>
            {state.userInfo.matchHistory.map((match, index) => (
              <li key={index}>
                Opponent: {match.opponent}, Result: {match.result}
              </li>
            ))}
          </ul>
        </div>
      );

    case 'error':
      return <div>Error: {state.message}</div>;

    default:
      return <div>Unexpected state</div>;
  }
}
