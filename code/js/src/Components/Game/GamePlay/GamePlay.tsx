import * as React from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { getGame, playGame } from '../../../Service/games/GamesServices';
import { GameState } from '../../../Domain/games/Game';
import { getUserName } from '../../Authentication/RequireAuthn';
import { GameOutputModel } from '../../../Service/games/models/GameModelsUtil';
import { isProblem } from '../../../Service/media/Problem';
import { PresentGame, ResultPresentation } from './GamePresentation';
import { User } from '../../../Domain/users/User';
import { StrictMode } from 'react';

type State =
  | { tag: 'loading' }
  | { tag: 'myTurn' }
  | { tag: 'waitingForOpponent' }
  | { tag: 'waitingForPlayResult' }
  | { tag: 'gameOver'; pointsAttribution: number; winner?: User }
  | { tag: 'redirect'; to: string }
  | { tag: 'error'; error: string };

type Action =
  | { type: 'waiting' }
  | { type: 'play'; result?: string; hasPlayed?: boolean }
  | { type: 'gameOver'; pointsAttribution?: number; winner?: User }
  | { type: 'redirect'; to: string }
  | { type: 'error'; error: string };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type} on state '${state.tag}'`);
}

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'waiting') {
        return { tag: 'waitingForOpponent' };
      } else if (action.type === 'play') {
        return { tag: 'myTurn' };
      } else if (action.type === 'gameOver') {
        return { tag: 'gameOver', pointsAttribution: action.pointsAttribution, winner: action.winner };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'myTurn':
      if (action.type === 'play') {
        if (action.hasPlayed) {
          return { tag: 'waitingForPlayResult' };
        } else {
          return state;
        }
      } else if (action.type === 'waiting') {
        return state;
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error };
      } else if (action.type === 'gameOver' && action.pointsAttribution) {
        return { tag: 'gameOver', pointsAttribution: action.pointsAttribution, winner: action.winner };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'waitingForOpponent':
      if (action.type === 'play') {
        return { tag: 'myTurn' };
      } else if (action.type === 'waiting') {
        return state;
      } else if (action.type === 'gameOver' && action.pointsAttribution) {
        return { tag: 'gameOver', pointsAttribution: action.pointsAttribution, winner: action.winner };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'waitingForPlayResult':
      if (action.type === 'gameOver' && action.pointsAttribution) {
        return { tag: 'gameOver', pointsAttribution: action.pointsAttribution, winner: action.winner };
      } else if (action.type === 'waiting') {
        return { tag: 'waitingForOpponent' };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'gameOver':
      if (action.type === 'gameOver') {
        return { tag: 'gameOver', pointsAttribution: action.pointsAttribution, winner: action.winner };
      } else if (action.type === 'redirect') {
        return { tag: 'redirect', to: action.to };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    default:
      logUnexpectedAction(state, action);
      return state;
  }
}

export function GamePlay() {
  const [state, dispatch] = React.useReducer(reducer, { tag: 'loading' });
  const { gid } = useParams();
  const gameId = Number(gid);
  const currentUser = getUserName();
  const [game, setGame] = React.useState<GameOutputModel | null>(null);

  React.useEffect(() => {
    if (state.tag === 'myTurn' || state.tag === 'redirect' || state.tag === 'gameOver' || state.tag === 'error') {
      return;
    }
    let pollingTimeout = 4000;
    let ignore = false; //TODO: PENSAR MELHOR NISTO @see https://react.dev/reference/react/useEffect#fetching-data-with-effects
    const iid = setInterval(async () => {
      const res = (await getGame(gameId)).properties;
      const game = res.game;
      pollingTimeout = res.pollingTimeOut;
      if (isMyTurn(currentUser, game)) {
        dispatch({ type: 'play', hasPlayed: false });
      } else if (isGameOver(game)) {
        dispatch({ type: 'gameOver', pointsAttribution: game.variant.points, winner: handleWinner(game) });
      } else {
        dispatch({ type: 'waiting' });
      }
      if (!ignore) {
        setGame(game);
      }
    }, pollingTimeout);

    return () => {
      ignore = true;
      clearInterval(iid);
    };
  }, [state.tag, gameId, currentUser]);

  //TODO: PENSAR MELHOR NISTO
  const handlePlay = async (row: number, col: number) => {
    //React.MouseEventHandler<HTMLTableDataCellElement>
    try {
      const game = (await playGame(gameId, row, col)).properties.game;
      if (isGameOver(game)) {
        dispatch({ type: 'gameOver', pointsAttribution: game.variant.points, winner: handleWinner(game) });
      } else {
        dispatch({ type: 'play', hasPlayed: true });
      }
      setGame(game);
    } catch (error) {
      dispatch({ type: 'error', error: isProblem(error) ? error.detail : error.message });
    }
  };

  //TODO: IMPLEMENT LOGIC
  function handleResign() {
    console.log('Resign');
  }

  //TODO: IMPROVE VISUALS 
  return (
    <div>
      {/* StrictMode to enable additional development behaviors and warnings for the component tree inside */}
      <StrictMode> 
        {state.tag === 'loading' && <div>Loading...</div>}
        {state.tag === 'waitingForOpponent' && <div>Waiting for opponent...</div>}
        {state.tag === 'waitingForPlayResult' && <div>Waiting for play result...</div>}
        {state.tag === 'myTurn' && <PresentGame game={game} onPlay={handlePlay} onResign={handleResign} />}
        {state.tag === 'gameOver' && (
          <div>
            <PresentGame game={game} onPlay={() => {}} />
            <ResultPresentation
              me={currentUser}
              winner={state.winner?.username}
              points={state.pointsAttribution}
              onExit={() => dispatch({ type: 'redirect', to: '/me' })}
            />
          </div>
        )}
        {state.tag === 'redirect' && <Navigate to={state.to} />}
        {state.tag === 'error' && <div>Error: {state.error}</div>}
      </StrictMode>
    </div>
  );
}

// Auxiliary functions

function isMyTurn(username: string, game: GameOutputModel): boolean {
  return (
    (game.state == GameState.NEXT_PLAYER_WHITE && game.userWhite.username == username) ||
    (game.state == GameState.NEXT_PLAYER_BLACK && game.userBlack.username == username)
  );
}

function isGameOver(game: GameOutputModel): boolean {
  return (
    game.state == GameState.PLAYER_BLACK_WON || game.state == GameState.PLAYER_WHITE_WON || game.state == GameState.DRAW
  );
}

function handleWinner(game: GameOutputModel): User | null {
  switch (game.state) {
    case GameState.PLAYER_WHITE_WON:
      return game.userWhite;
    case GameState.PLAYER_BLACK_WON:
      return game.userBlack;
    case GameState.DRAW:
      return null;
  }
}
