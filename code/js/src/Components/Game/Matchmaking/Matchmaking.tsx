import * as React from 'react';
import {useRef} from 'react';
import {cancelMatchmaking, getMatchmakingStatus} from '../../../Service/games/GamesServices';
import {Navigate, useParams} from 'react-router-dom';
import {QueueEntry} from '../../../Domain/games/QueueEntry';

type State =
    | { tag: 'readingStatus'; queueEntryId: number }
    | { tag: 'redirect'; gameId: number; queueEntryId: number; cancel: boolean };

type Action =
  | { type: 'readingStatus'; queueEntryId: number }
  | { type: 'readSuccess'; queueEntry: QueueEntry }
  | { type: 'redirect'; gameId: number, cancel: boolean }
  | { type: 'readError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'readingStatus':
      if (action.type === 'readSuccess') {
        if (action.queueEntry.status === 'PENDING') {
          return state;
        } else if (action.queueEntry.status === 'MATCHED') {
          return { tag: 'redirect', gameId: action.queueEntry.gameId!, queueEntryId: state.queueEntryId, cancel: false };
        } else {
          return state;
        }
      } else if (action.type === 'readError') {
        return state;
      } else if (action.type === 'redirect') {
        // Handle 'redirect' action in 'readingStatus' state
        return { tag: 'redirect', gameId: action.gameId, queueEntryId: state.queueEntryId, cancel: action.cancel };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'redirect':
      // Handle the 'redirect' case
      return state;
  }
}

export function MatchmakingPage() {
  const { mid } = useParams<{ mid: string }>();
  const [state, dispatch] = React.useReducer(reduce, { tag: 'readingStatus', queueEntryId: Number(mid) });
  const pollingTimeout = useRef(4000);

  React.useEffect(() => {
    const interval = setInterval(async () => {
      console.log('Polling for matchmaking status... on id ' + state.queueEntryId);
      const queueEntry = await getMatchmakingStatus(state.queueEntryId);
      pollingTimeout.current = queueEntry.properties.pollingTimOut;
      if (queueEntry.properties.state === 'MATCHED') {
        dispatch({ type: 'redirect', gameId: queueEntry.properties.gid!, cancel: false });
        // leave the interval running
        clearInterval(interval);
      }
    }, pollingTimeout.current);
    return () => clearInterval(interval);
  }, [state.queueEntryId]);

  async function handleCancel() {
    clearInterval(pollingTimeout.current);
    await cancelMatchmaking(state.queueEntryId);
    dispatch({ type: 'redirect', gameId: undefined, cancel: true });
  }

  switch (state.tag) {
    case 'readingStatus':
      return(
          <div>
               <h3>Searching for a opponent...</h3>
                <button onClick={handleCancel}>Cancel</button>
          </div>
      );
    case 'redirect':
      return <div>
        <p>Opponent found! Redirecting to game {state.gameId}...</p>
        <Navigate to={ state.cancel ? '/lobby' : `/game/${state.gameId}`}/>;
      </div>;
  }
}
