import * as React from 'react';
import { cancelMatchmaking, getMatchmakingStatus } from '../../../Service/games/GamesServices';
import { Navigate, useNavigate, useParams } from 'react-router-dom';
import { isProblem } from '../../../Service/media/Problem';

type State =
  | { tag: 'readingStatus'; queueEntryId: number}
  | { tag: 'redirecting'; to: string; queueEntryId?: number }
  | { tag: 'error'; message: string; queueEntryId: number };

type Action =
  | { type: 'success'; gameId: number }
  | { type: 'cancel'; queueEntryId: number }
  | { type: 'readError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'readingStatus':
      if (action.type === 'success') {
        return { tag: 'redirecting', to: `/game/${action.gameId}` };
      } else if (action.type === 'cancel') {
        return { tag: 'redirecting', to: '/lobby' };
      } else if (action.type === 'readError') {
        return { tag: 'error', message: action.message, queueEntryId: state.queueEntryId };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'redirecting':
      logUnexpectedAction(state, action);
      return state;
  }
}

export function MatchmakingPage() {
  const { mid } = useParams();
  const [state, dispatch] = React.useReducer(reducer, { tag: 'readingStatus', queueEntryId: Number(mid) });
  const [pollingTimeout, setPollingTimeout] = React.useState<number>(3000);
  const [cancelled, setCancelled] = React.useState<boolean>(false);

  React.useEffect(() => {
    if (state.tag !== 'readingStatus' || cancelled)
      return;

    const iid = setInterval(() => {
      getMatchmakingStatus(state.queueEntryId)
        .then(queueEntry => {
          if (queueEntry) {
            const { pollingTimeOut, state: status, gid } = queueEntry.properties;
            if (pollingTimeout !== pollingTimeOut) {
              setPollingTimeout(pollingTimeOut);
            }
            if (status === 'MATCHED') {
              dispatch({ type: 'success', gameId: gid });
            }
          } else {
            dispatch({ type: 'readError', message: 'Queue entry not found' });
          }
        })
        .catch(e => {
          dispatch({ type: 'readError', message: isProblem(e) ? e.detail : e.message });
        });
    }, pollingTimeout);

    return () => {
      clearInterval(iid);
    };
  }, [pollingTimeout, state.queueEntryId, state.tag, cancelled]);

  function onCancellingHandler() {
    if (state.tag !== 'readingStatus') {
      return;
    }
    cancelMatchmaking(state.queueEntryId)
      .then(() => {
        dispatch({ type: 'cancel', queueEntryId: state.queueEntryId });
        setCancelled(true);
      })
      .catch(e => dispatch({ type: 'readError', message: isProblem(e) ? e.detail : e.message }));
  }

  const navigate = useNavigate();
  return (
    <div>
      {state.tag === 'readingStatus' && (
        <div>
          <h3>Searching for a opponent...</h3>
          <button onClick={onCancellingHandler}>Cancel</button>
        </div>
      )}
      {state.tag === 'redirecting' && <Navigate to={state.to} />}
      {state.tag === 'error' && (
        <div>
          <p>{state.message}</p>
          <button onClick={() => navigate('/lobby', { replace: true })}>Return to lobby</button>
        </div>
      )}
    </div>
  );
}
