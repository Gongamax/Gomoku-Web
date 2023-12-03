import * as React from 'react';
import { useEffect} from 'react';
import { Link} from 'react-router-dom';

// Simulating API request
const fetchPlayers = () => {
  return new Promise<Player[]>(resolve => {
    setTimeout(() => {
      // Simulated list of players
      const players = [
        { name: 'Alice', score: 10, id: 1 },
        { name: 'Bob', score: 20, id: 2 },
        { name: 'Charlie', score: 30, id: 3 },
      ]
      resolve(players);
    }, 2000); // Simulating a 2-second delay for the API request
  });
};

//define the player type with name, score and id
type Player = {
  name: string;
  score: number;
  id: number;
};

//////////////////////////////////////////////////////////////////////////////////////////

type State = { tag: 'loading' } | { tag: 'presenting'; players: Player[] } | { tag: 'error'; message: string };

type Action =
  | { type: 'startLoading' }
  | { type: 'loadSuccess'; players: Player[] }
  | { type: 'loadError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

const reduce = (state: State, action: Action): State => {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return { tag: 'presenting', players: action.players };
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

export function RankingPage() {
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });

  useEffect(() => {
    const fetchData = async () => {
      try {
        dispatch({ type: 'startLoading' });
        const players = await fetchPlayers();
        dispatch({ type: 'loadSuccess', players });
      } catch (error) {
        dispatch({ type: 'loadError', message: error.message });
      }
    };

    fetchData();
  }, []);

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'presenting':
      return (
        <div>
          <h1>Player Rankings</h1>
          <ul>
            {state.players.map(player => (
              <li key={player.id}>
                <Link to={`/users/${player.id}`}>{player.name}</Link>
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
