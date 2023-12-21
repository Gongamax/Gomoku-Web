import * as React from 'react';
import { cancelMatchmaking, getMatchmakingStatus } from '../../../Service/games/GamesServices';
import { Navigate, useNavigate, useParams } from 'react-router-dom';
import { QueueEntry } from '../../../Domain/games/QueueEntry';
import { isProblem } from '../../../Service/media/Problem';

type State =
  | { tag: 'readingStatus'; queueEntryId: number }
  | { tag: 'redirect'; gameId: number; queueEntryId: number; cancel: boolean }
  | { tag: 'error'; message: string ; queueEntryId: number};

type Action =
  | { type: 'readingStatus'; queueEntryId: number }
  | { type: 'readSuccess'; queueEntry: QueueEntry }
  | { type: 'redirect'; gameId: number, cancel: boolean }
  | { type: 'readError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

const defaultPollingTimeout = 4000;

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'readingStatus':
      if (action.type === 'readSuccess') {
        if (action.queueEntry.status === 'PENDING') {
          return state;
        } else if (action.queueEntry.status === 'MATCHED') {
          return {
            tag: 'redirect',
            gameId: action.queueEntry.gameId!,
            queueEntryId: state.queueEntryId,
            cancel: false,
          };
        } else {
          return state;
        }
      } else if (action.type === 'readError') {
        return { tag: 'error', message: action.message, queueEntryId: state.queueEntryId };
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
  const matchId = Number(mid);
  const [state, dispatch] = React.useReducer(reduce, { tag: 'readingStatus', queueEntryId: matchId });

  React.useEffect(() => {
    let pollingTimeout = defaultPollingTimeout; // by default, poll every 4 seconds
    const intervalFunction = async () => {
      try {
        const queueEntry = await getMatchmakingStatus(state.queueEntryId);
        pollingTimeout = queueEntry.properties.pollingTimeOut;
        if (queueEntry.properties.state === 'MATCHED') {
          dispatch({ type: 'redirect', gameId: queueEntry.properties.gid!, cancel: false });
          clearInterval(iid);
        }
      } catch (e) {
        clearInterval(iid)
        if (isProblem(e)) {
          dispatch({ type: 'readError', message: e.detail });
        } else {
          dispatch({ type: 'readError', message: 'An error occurred while fetching the matchmaking status.' });
        }
      }
    };

    const iid = setInterval(intervalFunction, pollingTimeout);
    return () => {
      clearInterval(iid);
    }
  }, [state.queueEntryId]);

  async function handleCancel() {
    try {
      await cancelMatchmaking(state.queueEntryId);
      dispatch({ type: 'redirect', gameId: undefined, cancel: true });
    } catch (e) {
      if (isProblem(e)) {
        dispatch({ type: 'readError', message: e.detail });
      } else {
        dispatch({ type: 'readError', message: 'An error occurred while cancelling the matchmaking.' });
      }
    }
  }

  const navigate = useNavigate();

  switch (state.tag) {
    case 'readingStatus':
      return (
        <div>
          <h3>Searching for a opponent...</h3>
          <button onClick={handleCancel}>Cancel</button>
        </div>
      );
    case 'redirect':
      return <div>
        <p>Opponent found! Redirecting to game {state.gameId}...</p>
        <Navigate to={state.cancel ? '/lobby' : `/game/${state.gameId}`} />;
      </div>;
    case 'error':
      return <div>
        <p>{state.message}</p>
        <button onClick={() => navigate('/lobby', { replace: true })}>Return to lobby</button>
      </div>;
  }
}
