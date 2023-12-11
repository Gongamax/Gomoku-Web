import * as React from 'react';
import { useEffect } from 'react';
import { getUserName } from '../Authentication/RequireAuthn';
import { getStatsByUsername } from '../../Service/users/UserServices';
import { UserInfo } from '../../Domain/users/UserInfo';

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

export function Me() {
  const currentUser = getUserName();
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });

  useEffect(() => {
    async function fetchData() {
      try {
        dispatch({ type: 'startLoading' });
        const userResponse = await getStatsByUsername(currentUser);
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
  }, [currentUser]);

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'presenting':
      return (
        <div>
          <h1>Hello, {state.userInfo.username}</h1>
          <p>Wins: {state.userInfo.wins}</p>
          <p>Losses: {state.userInfo.losses}</p>
          <p>Draws: {state.userInfo.draws}</p>
          <p>Games Played: {state.userInfo.gamesPlayed}</p>
        </div>
      );

    case 'error':
      return <div>Error: {state.message}</div>;

    default:
      return <div>Unexpected state</div>;
  }
}