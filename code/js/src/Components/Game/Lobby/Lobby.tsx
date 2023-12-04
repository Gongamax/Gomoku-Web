import * as React from 'react';
import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';

// Simulating API request for player information
const fetchPlayerInfo = () => {
  return new Promise<{ username: string; points: number }>(resolve => {
    setTimeout(() => {
      // Simulated player information
      const playerInfo = {
        username: 'Player123',
        points: 1500,
      };
      resolve(playerInfo);
    }, 2000); // Simulating a 2-second delay for the API request
  });
};

// Simulating API request for game variants
const fetchGameVariants = () => {
  return new Promise<string[]>(resolve => {
    setTimeout(() => {
      // Simulated list of game variants
      const gameVariants = ['Standard', 'Blitz', 'Rapid'];
      resolve(gameVariants);
    }, 1000); // Simulating a 1-second delay for the API request
  });
};

// Simulating API request for matchmaking
const initiateMatchmaking = (selectedVariant: string) => {
  return new Promise<{ id: string; idType: string /*'game' | 'queue'*/ }>(resolve => {
    setTimeout(() => {
      console.log('Initiating matchmaking... with variant: ' + selectedVariant);
      // Simulated response for matchmaking
      const response = {
        id: '123456789',
        idType: Math.random() < 0.5 ? 'game' : 'queue', // Simulating a random response type
      };
      resolve(response);
    }, 2000); // Simulating a 2-second delay for the API request
  });
};

///////////////////////////////////////////////////////////////////////////////////

type State =
  | { tag: 'loading' }
  | { tag: 'present'; username: string; points: number; variants: string[] }
  | { tag: 'edit'; selectedVariant: string; variants: string[]; username: string; points: number }
  | { tag: 'redirect'; id: string; idType: string/*'game' | 'queue'*/ };

type Action =
  | { type: 'startLoading' }
  | { type: 'loadSuccess'; username: string; points: number; variants: string[] }
  | { type: 'selectVariant'; selectedVariant: string }
  | { type: 'initiateMatchmaking'; response: { id: string; idType: string /*'game' | 'queue'*/ } }
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

  useEffect(() => {
    const fetchData = async () => {
      try {
        dispatch({ type: 'startLoading' });
        const [playerInfo, gameVariants] = await Promise.all([fetchPlayerInfo(), fetchGameVariants()]);
        dispatch({ type: 'loadSuccess', ...playerInfo, variants: gameVariants });
      } catch (error) {
        dispatch({ type: 'loadError', message: error.message });
      }
    };

    fetchData();
  }, []);

  const handleVariantSelect = (selectedVariant: string) => {
    dispatch({ type: 'selectVariant', selectedVariant });
  };

  async function handleMatchmaking() {
    const { selectedVariant } = state as { selectedVariant: string };
    const response = await initiateMatchmaking(selectedVariant);
    dispatch({ type: 'initiateMatchmaking', response });
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
          <ul>
            {state.variants.map(variant => (
              <li key={variant} onClick={() => handleVariantSelect(variant)}>
                {variant}
              </li>
            ))}
          </ul>
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
          <ul>
            {state.variants.map(variant => (
              <li
                key={variant}
                onClick={() => handleVariantSelect(variant)}
                className={state.selectedVariant === variant ? 'selected' : ''}
              >
                {variant}
              </li>
            ))}
          </ul>
          <button onClick={handleMatchmaking}>Find Game</button>
        </div>
      );

    case 'redirect':
      // Redirect logic based on state.gameId and state.idType
      return (
        <div>
          <p>Redirecting...</p>
          {/* Navigate to matchmaking page for now*/}
          <Navigate to={`/matchmaking/${state.id}`} replace={true} />
        </div>
      );

    default:
      return <div>Unexpected state</div>;
  }
}
