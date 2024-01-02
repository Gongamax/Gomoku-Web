import * as React from 'react';
import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getRanking } from '../../Service/users/UserServices';
import { UserStatsOutputModel } from '../../Service/users/models/GetStatsOutput';
import { isProblem } from '../../Service/media/Problem';

/**
 * @typedef Player
 * @property {string} name - The name of the player.
 * @property {number} score - The score of the player.
 * @property {number} rank - The rank of the player.
 * @property {number} id - The id of the player.
 */
type Player = {
  name: string;
  score: number;
  rank: number;
  id: number;
};

/**
 * Defines the possible states of the component.
 * @type State
 * @type {object}
 * @property {string} tag - The current state tag.
 * @property {Player[]} players - The list of players.
 * @property {string} message - The error message.
 */
type State = { tag: 'loading' } | { tag: 'presenting'; players: Player[] } | { tag: 'error'; message: string };

/**
 * Defines the actions that can be dispatched to the reducer.
 * @type Action
 * @property type - The type of the action.
 * @property players - The list of players.
 * @property message - The error message.
 */
type Action =
  | { type: 'startLoading' }
  | { type: 'loadSuccess'; players: Player[] }
  | { type: 'loadError'; message: string };

/**
 * Logs an unexpected action.
 * @param {State} state - The current state.
 * @param {Action} action - The action that was dispatched.
 */
const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

/**
 * The reducer function for the useReducer hook.
 * @param {State} state - The current state.
 * @param {Action} action - The action that was dispatched.
 * @returns {State} The new state.
 */
function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return { tag: 'presenting', players: action.players };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
      } else if (action.type === 'startLoading') {
        return state;
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'presenting':
      if (action.type === 'loadSuccess') {
        return { tag: 'presenting', players: action.players };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
      } else if (action.type === 'startLoading') {
        return { tag: 'loading' };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
  }
}

/**
 * The RankingPage component displays a list of players and their rankings.
 * It fetches the player data from an API and updates its state based on the result of the API call.
 */
export function RankingPage() {
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });
  const [currentPage, setCurrentPage] = React.useState(1);
  const [links, setLinks] = React.useState<Record<string, string>>({});

  const fetchData = async (page: number) => {
    dispatch({ type: 'startLoading' });
    try {
      const players = await getRanking(page);
      const playersArray: Player[] = players.entities.map((entity) => {
        const player = entity.properties as unknown as UserStatsOutputModel;
        return {
          name: player.username,
          score: player.points,
          rank: player.rank,
          id: player.uid,
        };
      });

      dispatch({ type: 'loadSuccess', players: playersArray });

      // Save the links for pagination
      const paginationLinks: Record<string, string> = {};
      players.links.forEach(link => {
        paginationLinks[link.rel[0]] = link.href;
      });
      setLinks(paginationLinks);
    } catch (e) {
      const message = isProblem(e) ? e.detail : e.message;
      dispatch({ type: 'loadError', message: message });
    }
  };

  useEffect(() => {
    fetchData(currentPage);
  }, [currentPage]);

  const handlePagination = (rel: string) => {
    const page = links[rel].split('=')[1];
    setCurrentPage(parseInt(page));
  };

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'presenting':
      return (
        <div>
          <h1>Player Rankings</h1>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
            <tr style={{ borderBottom: '1px solid black' }}>
              <th style={{ padding: '10px', textAlign: 'left' }}>Rank</th>
              <th style={{ padding: '10px', textAlign: 'left' }}>Name</th>
              <th style={{ padding: '10px', textAlign: 'left' }}>Points</th>
            </tr>
            </thead>
            <tbody>
            {state.players.map(player => (
              <tr key={player.id} style={{ borderBottom: '1px solid #ddd' }}>
                <td style={{ padding: '10px' }}>{player.rank}</td>
                <td style={{ padding: '10px' }}><Link to={`/users/${player.id}`}>{player.name}</Link></td>
                <td style={{ padding: '10px' }}>{player.score}</td>
              </tr>
            ))}
            </tbody>
          </table>
          <div>
            <button onClick={() => handlePagination('first')} disabled={!links['first']}>First</button>
            <button onClick={() => handlePagination('previous')} disabled={!links['previous']}>Prev</button>
            <button onClick={() => handlePagination('next')} disabled={!links['next']}>Next</button>
            <button onClick={() => handlePagination('last')} disabled={!links['last']}>Last</button>
          </div>
        </div>
      );

    case 'error':
      return <div>Error: {state.message}</div>;

    default:
      return <div>Unexpected state</div>;
  }
}
