import * as React from 'react';

// Simulating API request for macthmaking entry, example of entry:
// {
//   "id": 1,
//   userId: 1,
//   variant: "Standard",
//   status: "Pending",
//   gameId: null,
//   createdAt: "2021-03-23T15:00:00.000Z",
// }

const fetchMatchmakingEntry = (queueEntryId: number) => {
  return new Promise<QueueEntry>(resolve => {
    setTimeout(() => {
      // Simulated matchmaking entry
      const matchmakingEntry: QueueEntry = {
        id: queueEntryId,
        userId: 1,
        variant: 'Standard',
        status: 'Pending',
        gameId: null,
        createdAt: '2021-03-23T15:00:00.000Z',
      };
      resolve(matchmakingEntry);
    }, 2000); // Simulating a 2-second delay for the API request
  });
};

type QueueEntry = {
  id: number;
  userId: number;
  variant: string;
  status: string;
  gameId: number | null;
  createdAt: string;
};

/////////////////////////////////////////////////////////////////////

type State = { tag: 'readingStatus'; queueEntryId: number } | { tag: 'redirect'; gameId: number; queueEntryId: number };

type Action =
  | { type: 'readingStatus'; queueEntryId: number }
  | { type: 'readSuccess'; queueEntry: QueueEntry }
  | { type: 'redirect'; gameId: number }
  | { type: 'readError'; message: string };

const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

// complete version of the reducer
function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'readingStatus':
      if (action.type === 'readSuccess') {
        if (action.queueEntry.status === 'Pending') {
          return state;
        } else if (action.queueEntry.status === 'Matched') {
          return { tag: 'redirect', gameId: action.queueEntry.gameId!, queueEntryId: state.queueEntryId };
        } else {
          return state;
        }
      } else if (action.type === 'readError') {
        return state;
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'redirect':
      return state;
  }
}

// Complete version of the component using the reducer without hardcoding the queue entry id
export function MatchmakingPage() {
  const [state, dispatch] = React.useReducer(reduce, { tag: 'readingStatus', queueEntryId: 1 });

  // Simulating polling for the matchmaking status
  // if status is pending, do nothing
  // if status is matched, redirect to game
  // if status is error, show error message
  React.useEffect(() => {
    const interval = setInterval(async () => {
      console.log('Polling for matchmaking status... on id ' + state.queueEntryId);
      const queueEntry = await fetchMatchmakingEntry(state.queueEntryId);
      if (queueEntry.status === 'Matched') {
        dispatch({ type: 'redirect', gameId: queueEntry.gameId! });
      }
    }, 1000); // Simulating a 1-second delay for the API request
    return () => clearInterval(interval);
  }, [state.queueEntryId]);

  switch (state.tag) {
    case 'readingStatus':
      return <div>Loading...</div>;
    case 'redirect':
      return <div>Redirecting to game {state.gameId}...</div>;
  }
}
