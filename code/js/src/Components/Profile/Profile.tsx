import * as React from 'react';
import { useEffect } from 'react';
import { Link, Outlet, useParams } from 'react-router-dom';
import { getStatsById } from '../../Service/users/UserServices';
import { UserInfo } from '../../Domain/users/UserInfo';

/**
 * `Match` is a type that represents a match.
 * It includes properties for `opponent` and `result`.
 */
type Match = {
  opponent: string;
  result: string;
}

type State = { tag: 'loading' } | { tag: 'presenting'; userInfo: UserInfo } | { tag: 'error'; message: string };

type Action =
  | { type: 'startLoading' }
  | { type: 'loadSuccess'; userInfo: UserInfo }
  | { type: 'loadMatchHistory'; matchHistory: Match[] }
  | { type: 'loadError'; message: string };

/**
 * `logUnexpectedAction` is a function that logs an unexpected action.
 * It is called when an action is dispatched that is not expected in the current state.
 *
 * @param {State} state - The current state.
 * @param {Action} action - The action that was dispatched.
 */
const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

/**
 * `reduce` is a reducer function for the ProfilePage component.
 * It takes the current state and an action, and returns the new state.
 *
 * @param {State} state - The current state.
 * @param {Action} action - The action to handle.
 * @returns {State} - The new state.
 */
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

/**
 * `ProfilePage` is a React functional component that renders the profile page of a user.
 * It fetches the user's information and match history from the server when the component mounts and stores it in state.
 * The user's information and match history are displayed in a table.
 *
 * @returns {React.ReactElement} - The rendered profile page.
 */
export function ProfilePage() {
  const { uid } = useParams<{ uid: string }>();
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });

  useEffect(() => {
    async function fetchData() {
      try {
        dispatch({ type: 'startLoading' });
        const userResponse = await getStatsById(Number(uid));
        const userInfo = {
          username: userResponse.properties.username,
          wins: userResponse.properties.wins,
          losses: userResponse.properties.losses,
          draws: userResponse.properties.gamesPlayed - (userResponse.properties.wins + userResponse.properties.losses),
          gamesPlayed: userResponse.properties.gamesPlayed,
        };
        dispatch({ type: 'loadSuccess', userInfo });
      } catch (error) {
        dispatch({ type: 'loadError', message: error.message });
      }
    }

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

          {/* Button to link to match history */}
          <button><Link to="history">Match History</Link></button>
          <Outlet />
        </div>
      );

    case 'error':
      return <div>Error: {state.message}</div>;

    default:
      return <div>Unexpected state</div>;
  }
}
