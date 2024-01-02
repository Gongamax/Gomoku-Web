import * as React from 'react';
import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { getVariantList, matchmaking } from '../../../Service/games/GamesServices';
import { getStatsByUsername } from '../../../Service/users/UserServices';
import { getUserName } from '../../Authentication/RequireAuthn';
import { isProblem } from '../../../Service/media/Problem';

type State =
  | { tag: 'loading' }
  | { tag: 'editing'; selectedVariant: string; variants: string[]; username: string; points: number }
  | { tag: 'redirect'; id: number; idType: string }
  | { tag: 'error'; message: string };

type Action =
  | { type: 'loadSuccess'; username: string; points: number; variants: string[]; defaultVariant: string }
  | { type: 'selectVariant'; selectedVariant: string; username: string; points: number }
  | { type: 'initiateMatchmaking'; response: { id: number; idType: string } }
  | { type: 'loadError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return {
          tag: 'editing',
          selectedVariant: action.defaultVariant,
          username: action.username,
          points: action.points,
          variants: action.variants,
        };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'editing':
      if (action.type === 'selectVariant') {
        return {
          tag: 'editing',
          selectedVariant: action.selectedVariant,
          variants: state.variants,
          username: action.username,
          points: action.points,
        };
      } else if (action.type === 'initiateMatchmaking') {
        return { tag: 'redirect', id: action.response.id, idType: action.response.idType };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'redirect':
      logUnexpectedAction(state, action);
      return state;

    default:
      return state;
  }
}

export function LobbyPage() {
  const [state, dispatch] = React.useReducer(reducer, { tag: 'loading' });
  const currentUser = getUserName();

  useEffect(() => {
    if (state.tag !== 'loading') {
      return;
    }
    getStatsByUsername(currentUser)
      .then(playerInfo => {
        const [username, points] = [playerInfo.properties.username, playerInfo.properties.points];
        getVariantList().then(res => {
          const gameVariants = res.properties.variants.map((variant: { name: string }) => variant.name);
          dispatch({
            type: 'loadSuccess',
            username: username,
            points: points,
            variants: gameVariants,
            defaultVariant: gameVariants[0],
          });
        });
      })
      .catch(e => dispatch({ type: 'loadError', message: isProblem(e) ? e.detail : e.message }));
  }, [currentUser, state.tag]);

  if (state.tag === 'redirect') {
    if (state.idType === 'gid') return <Navigate to={`/game/${state.id}`} replace={true} />;
    else if (state.idType === 'mid') return <Navigate to={`/matchmaking/${state.id}`} replace={true} />;
  }

  function handleVariantSelect(variant: string, username: string, points: number) {
    dispatch({ type: 'selectVariant', selectedVariant: variant, username: username, points: points });
  }

  function handleMatchmaking() {
    if (state.tag !== 'editing') {
      return;
    }
    matchmaking(state.selectedVariant)
      .then(response =>
        dispatch({
          type: 'initiateMatchmaking',
          response: { id: response.properties.id, idType: response.properties.idType },
        })
      )
      .catch(error => dispatch({ type: 'loadError', message: error.message }));
  }

  return (
    <div>
      {state.tag === 'loading' && <div>Loading...</div>}
      {state.tag === 'editing' && (
        <div>
          <h1>Lobby</h1>
          <p>Username: {state.username}</p>
          <p>Points: {state.points}</p>
          <p>Choose a Game Variant:</p>
          {state.variants.map((variant, index) => (
            <div key={index}>
              <input
                type="radio"
                id={`radio-${index}`}
                name="variants"
                value={variant}
                checked={state.selectedVariant === variant}
                onChange={() => handleVariantSelect(variant, state.username, state.points)}
              />
              <label htmlFor={`radio-${index}`}>{variant}</label>
            </div>
          ))}
          <button onClick={handleMatchmaking}>Find Game</button>
        </div>
      )}
      {state.tag === 'error' && (
        <div>
          <p>Something went wrong. Please try again later.</p>
          <p>
            <small>Error details: {state.message}</small>
          </p>
        </div>
      )}
    </div>
  );
}
