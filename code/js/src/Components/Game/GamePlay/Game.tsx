import * as React from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { Game } from '../../../Domain/games/Game';
import * as GameService from '../../../Service/games/GamesServices';
import { User } from '../../../Domain/users/User';
import { PresentGame, ResultPresentation } from './GamePresentation';
import { checkTurn, convertToDomainGame, handleWinner, isDraw, isWin } from './GameUtils';
import { getUserName } from '../../Authentication/RequireAuthn';
import { isProblem } from '../../../Service/media/Problem';

type State =
  | { tag: 'loading' }
  | { tag: 'myTurn'; game: Game }
  | { tag: 'loadingPlay'; game: Game }
  | { tag: 'opponentTurn'; game: Game; pollingTimeOut: number }
  | { tag: 'gameOver'; game: Game; winner: User }
  | { tag: 'redirect' }
  | { tag: 'error'; game?: Game; message: string };

type Action =
  | { type: 'makePlay'; game: Game; hasPlayed: boolean }
  | { type: 'error'; message: string; game: Game }
  | { type: 'waiting'; game: Game; pollingTimeOut: number }
  | { type: 'gameOver'; game: Game; winner: User; redirect: boolean }
  | { type: 'retry' };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'waiting') {
        return { tag: 'opponentTurn', game: action.game, pollingTimeOut: action.pollingTimeOut };
      } else if (action.type == 'makePlay') {
        return { tag: 'myTurn', game: action.game };
      } else if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else if (action.type === 'error') {
        return { tag: 'error', game: action.game, message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'myTurn':
      if (action.type === 'makePlay') {
        if (action.hasPlayed) return { tag: 'loadingPlay', game: action.game };
        else return state;
      } else if (action.type === 'waiting') {
        return { tag: 'opponentTurn', game: action.game, pollingTimeOut: action.pollingTimeOut };
      } else if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else if (action.type == 'error') {
        return { tag: 'error', game: action.game, message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'opponentTurn':
      if (action.type === 'waiting') {
        return state;
      } else if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else if (action.type === 'makePlay') {
        return { tag: 'myTurn', game: action.game };
      } else if (action.type == 'error') {
        return { tag: 'error', game: action.game, message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'loadingPlay':
      if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else if (action.type == 'waiting') {
        return { tag: 'opponentTurn', game: action.game, pollingTimeOut: action.pollingTimeOut };
      } else if (action.type == 'error') {
        return { tag: 'error', game: action.game, message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'gameOver':
      if (action.type === 'gameOver') {
        if (action.redirect) {
          return { tag: 'redirect' };
        } else {
          return state;
        }
      } else if (action.type == 'error') {
        return { tag: 'error', game: action.game, message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'error':
      if (action.type === 'retry') {
        return { tag: 'loading' };
      } else return state;
    case 'redirect':
      logUnexpectedAction(state, action);
      return state;
  }
}

export function GamePage() {
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });
  const { gid } = useParams<{ gid: string }>();
  const gameId = Number(gid);
  const currentUser = getUserName();
  const intervalId = React.useRef<NodeJS.Timeout>();

  const fetchData = async () => {
    // Clear the interval before setting a new one
    if (intervalId.current) {
      clearInterval(intervalId.current);
    }
    const response = (await GameService.getGame(gameId)).properties;
    const game = convertToDomainGame(response.game);
    if (isWin(game)) {
      clearInterval(intervalId.current);
      dispatch({
        type: 'gameOver',
        game: game,
        winner: handleWinner(game),
        redirect: false,
      });
    } else if (isDraw(game)) {
      clearInterval(intervalId.current);
      dispatch({
        type: 'gameOver',
        game: game,
        winner: undefined,
        redirect: false,
      });
    } else {
      if (checkTurn(currentUser, response.game)) {
        clearInterval(intervalId.current);
        dispatch({
          type: 'makePlay',
          game: game,
          hasPlayed: false,
        });
      } else {
        dispatch({
          type: 'waiting',
          game: game,
          pollingTimeOut: response.pollingTimeOut,
        });
      }
    }
  };

  async function handleLoading() {
    try {
      console.log(`state: ${state.tag}`);
      await fetchData();
    } catch (error) {
      handleErrors(undefined, error.message);
    }
    return () => clearInterval(intervalId.current);
  }

  async function handlePlay(row: number, col: number) {
    if (state.tag === 'myTurn') {
      clearInterval(intervalId.current);
      try {
        // Send a request to the server to make a move
        await GameService.playGame(state.game.id, row, col);
        // Update the local game state based on the server's response
        fetchData().catch(error => handleErrors(state.game, error.message));
      } catch (e) {
        handleErrors(state.game, isProblem(e) ? e.detail : e.message);
      }
    }
  }

  async function handleWaiting(pollingTimeOut: number) {
    intervalId.current = setInterval(() => {
      fetchData();
    }, pollingTimeOut);
  }

  async function handleResign(): Promise<void> {
    if (state.tag === 'myTurn' || state.tag === 'opponentTurn') {
      try {
        await GameService.surrenderGame(state.game.id);
        fetchData().catch(error => handleErrors(state.game, error.message));
      } catch (error) {
        console.error('Failed to resign:', error);
      }
    }
  }

  function handleExit() {
    if (state.tag === 'gameOver') {
      dispatch({
        type: 'gameOver',
        game: state.game,
        winner: handleWinner(state.game),
        redirect: true,
      });
    }
  }

  function handleErrors(game: Game, error: string) {
    clearInterval(intervalId.current);
    dispatch({
      type: 'error',
      game: game,
      message: error,
    });
  }

  function handleRetry() {
    clearInterval(intervalId.current);
    if (state.tag === 'error')
      dispatch({
        type: 'retry',
      });
  }

  switch (state.tag) {
    case 'loading': {
      handleLoading().catch(error => console.log(`Error while loading game: ${error.message}`));
      return (
        <div>
          <h3>Loading...</h3>
        </div>
      );
    }

    case 'myTurn': {
      return (
        <div>
          <PresentGame game={state.game} onPlay={handlePlay} onResign={handleResign} />
        </div>
      );
    }

    case 'opponentTurn': {
      handleWaiting(state.pollingTimeOut).catch(error =>
        dispatch({
          type: 'error',
          game: state.game,
          message: error.toString(),
        }),
      );
      return (
        <div>
          <PresentGame game={state.game} onPlay={() => {
          }} onResign={() => {
          }} />
          <h3>Waiting for opponent to play...</h3>
        </div>
      );
    }

    case 'loadingPlay':
      return (
        <div>
          <div>
            <PresentGame game={state.game} onPlay={() => {
            }} onResign={() => {
            }} />
            <h3>loading Play...</h3>
          </div>
        </div>
      );

    case 'gameOver':
      return (
        <div>
          <PresentGame game={state.game} onPlay={() => {
          }} onResign={() => {
          }} />
          <ResultPresentation
            me={currentUser}
            winner={state.winner.username}
            points={state.game.variant.points}
            onExit={handleExit}
          />
        </div>
      );
    case 'error':
      return (
        <div>
          <PresentGame game={state.game} onPlay={() => {
          }} onResign={() => {
          }} />
          <h3>{state.message}</h3>
          <button onClick={handleRetry}>Retry</button>
        </div>
      );
    case 'redirect':
      clearInterval(intervalId.current);
      return <Navigate to='/me' />;
  }
}