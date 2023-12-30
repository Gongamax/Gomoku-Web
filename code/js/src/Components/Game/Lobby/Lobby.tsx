import * as React from 'react';
import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { getVariantList, matchmaking } from '../../../Service/games/GamesServices';
import { getStatsByUsername } from '../../../Service/users/UserServices';
import { getUserName } from '../../Authentication/RequireAuthn';
import { isProblem } from '../../../Service/media/Problem';

type State =
  | { tag: 'loading' }
  | { tag: 'present'; username: string; points: number; variants: string[] }
  | { tag: 'edit'; selectedVariant: string; variants: string[]; username: string; points: number }
  | { tag: 'redirect'; id: number; idType: string }
  | { tag: 'error'; message: string };

type Action =
  | { type: 'loadSuccess'; username: string; points: number; variants: string[] }
  | { type: 'selectVariant'; selectedVariant: string; username: string; points: number }
  | { type: 'initiateMatchmaking'; response: { id: number; idType: string } }
  | { type: 'loadError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

/**
 * `fetchPlayerInfo` is an asynchronous function that fetches the player's information.
 * It calls the `getStatsByUsername` function with the provided username and returns an object containing the username and points.
 *
 * @param {string} username - The username of the player.
 * @returns {Promise<{ username: string, points: number }>} - A promise that resolves to an object containing the username and points.
 */
async function fetchPlayerInfo(username: string): Promise<{ username: string; points: number }> {
  const user = await getStatsByUsername(username);
  return { username: user.properties.username, points: user.properties.points };
}

/**
 * `fetchGameVariants` is an asynchronous function that fetches the game variants.
 * It calls the `getVariantList` function and maps the returned variants to their names.
 *
 * @returns {Promise<string[]>} - A promise that resolves to an array of variant names.
 */
async function fetchGameVariants(): Promise<string[]> {
  const variants = await getVariantList();
  return variants.properties.variants.map((variant: { name: string }) => variant.name);
}

/**
 * `initiateMatchmaking` is an asynchronous function that initiates matchmaking for a given variant.
 * It calls the `matchmaking` function with the provided variant and returns an object containing the id and idType of the response.
 *
 * @param {string} variant - The variant for which to initiate matchmaking.
 * @returns {Promise<{ id: number; idType: string }>} - A promise that resolves to an object containing the id and idType.
 */
async function initiateMatchmaking(variant: string): Promise<{ id: number; idType: string }> {
  const response = await matchmaking(variant);
  return { id: response.properties.id, idType: response.properties.idType };
}

const reduce = (state: State, action: Action): State => {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return { tag: 'present', username: action.username, points: action.points, variants: action.variants };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
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
          username: action.username, // Add username with a default value
          points: action.points, // Add points with a default value
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
  //const [selectedVariant, setSelectedVariant] = useState<string>('STANDARD');
  const currentUser = getUserName();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [playerInfo, gameVariants] = await Promise.all([fetchPlayerInfo(currentUser), fetchGameVariants()]);
        dispatch({ type: 'loadSuccess', ...playerInfo, variants: gameVariants });
      } catch (e) {
        dispatch({ type: 'loadError', message: isProblem(e) ? e.detail : e.message });
      }
    };

    fetchData().catch(error => {
      dispatch({ type: 'loadError', message: error.message });
    });
  }, [currentUser]);

  const handleVariantSelect = (selectedVariant: string, username: string, points: number) => {
    //setSelectedVariant(selectedVariant);
    dispatch({ type: 'selectVariant', selectedVariant, username: username, points: points });
  };

  function handleMatchmaking() {
    if (state.tag !== 'edit') {
      return;
    }
    initiateMatchmaking(state.selectedVariant)
      .then(response => dispatch({ type: 'initiateMatchmaking', response }))
      .catch(error => dispatch({ type: 'loadError', message: error.message }));
  }

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'present':
      //dispatch({ type: 'selectVariant', selectedVariant, username: state.username, points: state.points });
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
                checked={false} // state.selectedVariant === variant
                onChange={() => handleVariantSelect(variant, state.username, state.points)}
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
                checked={state.selectedVariant === variant}
                onChange={() => handleVariantSelect(variant, state.username, state.points)}
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
      else return <div>Unexpected idType</div>;

    case 'error':
      return (
        <div
          style={{
            padding: '20px',
            backgroundColor: '#f8d7da',
            color: '#721c24',
            borderRadius: '5px',
            marginTop: '20px',
          }}
        >
          <h2>Oops!</h2>
          <p>Something went wrong. Please try again later.</p>
          <p>
            <small>Error details: {state.message}</small>
          </p>
        </div>
      );

    default:
      return <div>Unexpected state</div>;
  }
}
