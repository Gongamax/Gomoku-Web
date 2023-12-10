import * as React from 'react';
import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { getVariantList, matchmaking } from '../../../Service/games/GamesServices';
import { getStatsByUsername } from '../../../Service/users/UserServices';
import { getCookie } from '../../Authentication/RequireAuthn';

async function fetchPlayerInfo(username: string): Promise<{ username: string, points: number }> {
  const user = await getStatsByUsername(username);
  return { username: user.properties.username, points: user.properties.points };
}

async function fetchGameVariants(): Promise<string[]> {
  const variants = await getVariantList();
  return variants.properties.variants.map((variant: { name: string; }) => variant.name);
}

async function initiateMatchmaking(variant: string): Promise<{ id: number; idType: string }> {
  console.log(`Initiating matchmaking for variant '${variant}'`);
  const response = await matchmaking(variant);
  return { id: response.properties.id, idType: response.properties.idType };
}

type State =
  | { tag: 'loading' }
  | { tag: 'present'; username: string; points: number; variants: string[] }
  | { tag: 'edit'; selectedVariant: string; variants: string[]; username: string; points: number }
  | { tag: 'redirect'; id: number; idType: string };

type Action =
  | { type: 'loadSuccess'; username: string; points: number; variants: string[] }
  | { type: 'selectVariant'; selectedVariant: string }
  | { type: 'initiateMatchmaking'; response: { id: number; idType: string } }
  | { type: 'loadError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

const reduce = (state: State, action: Action): State => {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return { tag: 'present', username: action.username, points: action.points, variants: action.variants };
      } else if (action.type === 'loadError') {
        return { tag: 'edit', selectedVariant: 'Standard', variants: [], username: 'Test', points: 1000 }; // Default to Standard variant in case of error
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'present':
      if (action.type === 'selectVariant') {
        return {
          tag: 'edit',
          selectedVariant: action.selectedVariant,
          variants: state.variants,
          username: state.username,
          points: state.points,
        };
      } else if (action.type === 'initiateMatchmaking') {
        return { tag: 'redirect', id: action.response.id, idType: action.response.idType };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'edit':
      if (action.type === 'selectVariant') {
        return {
          tag: 'edit',
          selectedVariant: action.selectedVariant,
          variants: state.variants,
          username: '', // Add username with a default value
          points: 0, // Add points with a default value
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
};

export function LobbyPage() {
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });
  const [selectedVariant, setSelectedVariant] = useState<string>('STANDARD');
  const currentUser = getCookie('login')

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [playerInfo, gameVariants] =
          await Promise.all([fetchPlayerInfo(currentUser), fetchGameVariants()]);
        dispatch({ type: 'loadSuccess', ...playerInfo, variants: gameVariants });
      } catch (error) {
        dispatch({ type: 'loadError', message: error.message });
      }
    };

    fetchData().catch(error => {
      dispatch({ type: 'loadError', message: error.message });
    });
  }, [currentUser]);

  const handleVariantSelect = (selectedVariant: string) => {
    setSelectedVariant(selectedVariant);
    dispatch({ type: 'selectVariant', selectedVariant });
  };

  async function handleMatchmaking() {
    const { selectedVariant } = state as { selectedVariant: string };
    initiateMatchmaking(selectedVariant).then(response => {
      dispatch({ type: 'initiateMatchmaking', response });
    }).catch(error => {
      dispatch({ type: 'loadError', message: error.message });
    });
  }

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'present':
      return (
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
                checked={selectedVariant === variant}
                onChange={() => handleVariantSelect(variant)}
              />
              <label htmlFor={`radio-${index}`}>{variant}</label>
            </div>
          ))}
          <button onClick={handleMatchmaking}>Find Game</button>
        </div>
      );

    case 'edit':
      return (
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
                checked={selectedVariant === variant}
                onChange={() => handleVariantSelect(variant)}
              />
              <label htmlFor={`radio-${index}`}>{variant}</label>
            </div>
          ))}
          <button onClick={handleMatchmaking}>Find Game</button>
        </div>
      );

    case 'redirect':
      if (state.idType === 'gid')
        return (
          <div>
            <p>Redirecting...</p>
            {/* Navigate to game page*/}
            <Navigate to={`/game/${state.id}`} replace={true} />
          </div>
        );
      else if (state.idType === 'mid')
        return (
          <div>
            <p>Redirecting...</p>
            {/* Navigate to matchmaking page*/}
            <Navigate to={`/matchmaking/${state.id}`} replace={true} />
          </div>
        );
      else
        return <div>Unexpected idType</div>;

    default:
      return <div>Unexpected state</div>;
  }
}
