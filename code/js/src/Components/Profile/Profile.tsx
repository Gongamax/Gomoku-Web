import * as React from 'react';
import { useEffect } from 'react';
import { getStatsById } from '../../Service/users/UserServices';
import { UserInfo } from '../../Domain/users/UserInfo';
import { getAllGamesByUser } from '../../Service/games/GamesServices';
import { convertToDomainMatchHistory, convertToDomainUser } from './ProfileUtil';
import { useParams } from 'react-router-dom';
import { isProblem } from '../../Service/media/Problem';
import { User } from '../../Domain/users/User';

type State =
  | { tag: 'loading' }
  | { tag: 'presenting'; userInfo: UserInfo; matchHistory: Match[]; presentHistory: boolean }
  | { tag: 'error'; message: string };

type Action =
  | { type: 'loadSuccess'; userInfo: UserInfo; matchHistory: Match[] }
  | { type: 'loadError'; message: string }
  | { type: 'showHistory' };

type Match = {
  opponent: User;
  result: string;
};

/**
 * `logUnexpectedAction` is a function that logs an unexpected action.
 * It is called when an action is dispatched that is not expected in the current state.
 *
 * @param {State} state - The current state.
 * @param {Action} action - The action that was dispatched.
 */
function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type} on state '${state.tag}'`);
}

/**
 * `reduce` is a reducer function for the ProfilePage component.
 * It takes the current state and an action, and returns the new state.
 *
 * @param {State} state - The current state.
 * @param {Action} action - The action to handle.
 * @returns {State} - The new state.
 */
function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return {
          tag: 'presenting',
          userInfo: action.userInfo,
          matchHistory: action.matchHistory,
          presentHistory: false,
        };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'presenting':
      if (action.type === 'showHistory') {
        return { ...state, presentHistory: !state.presentHistory };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'error':
      logUnexpectedAction(state, action);
      return state;
  }
}

/**
 * `ProfilePage` is a React functional component that renders the profile page of a user.
 * It fetches the user's information and match history from the server when the component mounts and stores it in state.
 * The user's information and match history are displayed in a table.
 *
 * @returns {React.ReactElement} - The rendered profile page.
 */
export function ProfilePage(): React.ReactElement {
  const { uid } = useParams<{ uid: string }>();
  const userId = Number(uid);
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });

  useEffect(() => {
    async function fetchData() {
      try {
        const userResponse = await getStatsById(userId);
        const historyResponse = await getAllGamesByUser(userId);
        const userInfo = convertToDomainUser(userResponse);
        const matchHistory: Match[] = convertToDomainMatchHistory(historyResponse, userId);
        dispatch({ type: 'loadSuccess', userInfo: userInfo, matchHistory: matchHistory });
      } catch (error) {
        dispatch({ type: 'loadError', message: isProblem(error) ? error.detail : error.message });
      }
    }
    fetchData();
  }, [userId]);

  function handleHistoryRequest() {
    dispatch({ type: 'showHistory' });
  }

  return (
    <div>
      {state.tag === 'loading' && <div>Loading...</div>}
      {state.tag === 'presenting' && (
        <div>
          <h1>User Profile: {state.userInfo.username}</h1>
          <p>Wins: {state.userInfo.wins}</p>
          <p>Losses: {state.userInfo.losses}</p>
          <p>Draws: {state.userInfo.draws}</p>
          <p>Games Played: {state.userInfo.gamesPlayed}</p>
          <button onClick={handleHistoryRequest}>Match History</button>
          {state.presentHistory && (
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
                  {state.matchHistory.map((match, index) => (
                    <tr key={index} style={{ borderBottom: '1px solid #eee' }}>
                      <td style={{ padding: '10px' }}>
                        <a href={`/users/${match.opponent.id.value}`}>{match.opponent.username}</a>
                      </td>
                      <td style={{ padding: '10px' }}>{match.result}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
      {state.tag === 'error' && <div>Error: {state.message}</div>}
    </div>
  );
}
