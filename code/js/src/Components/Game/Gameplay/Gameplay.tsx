import * as React from 'react';
import { useReducer } from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { getGame, playGame, surrenderGame } from '../../../Service/games/GamesServices';
import { getUserName } from '../../Authentication/RequireAuthn';
import { isProblem } from '../../../Service/media/Problem';
import { User } from '../../../Domain/users/User';
import { Game, convertToDomainGame } from '../../../Domain/games/Game';
import { useInterval } from './useInterval';
import { PresentGame, ResultPresentation } from './GamePresentation';
import { handleWinner, isGameOver, isMyTurn } from './GameUtils';
import { ProblemType } from '../../../Service/media/ProblemTypes';

type State =
  | { tag: 'loading'; pollDelay: number }
  | { tag: 'myTurn'; game: Game; pollDelay: number; badMove?: string }
  | { tag: 'waitingForOpponent'; game: Game; pollDelay: number }
  | { tag: 'waitingForPlayResult'; game: Game; pollDelay: number }
  | { tag: 'gameOver'; winner?: User; game: Game }
  | { tag: 'redirect'; to: string }
  | { tag: 'error'; error: string; game: Game; previousState: State };

type Action =
  | { type: 'waiting'; game: Game; pollDelay: number }
  | { type: 'play'; game: Game; hasPlayed?: boolean; resign?: boolean; pollDelay?: number; badMove?: string }
  | { type: 'gameOver'; game: Game; winner?: User }
  | { type: 'redirect'; to: string }
  | { type: 'error'; error: string; game?: Game };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type} on state '${state.tag}'`);
}

function reducer(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'waiting') {
        return { tag: 'waitingForOpponent', game: action.game, pollDelay: action.pollDelay };
      } else if (action.type === 'play') {
        return { tag: 'myTurn', game: action.game, pollDelay: action.pollDelay };
      } else if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error, game: action.game, previousState: state };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'myTurn':
      if (action.type === 'play') {
        if (action.hasPlayed) return { tag: 'waitingForPlayResult', game: action.game, pollDelay: action.pollDelay };
        else if (action.resign) return { tag: 'redirect', to: '/me' };
        else if (action.badMove)
          return { tag: 'myTurn', game: action.game, pollDelay: action.pollDelay, badMove: action.badMove };
        else return state;
      } else if (action.type === 'waiting') {
        return state;
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error, game: action.game, previousState: state };
      } else if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'waitingForOpponent':
      if (action.type === 'play') {
        return { tag: 'myTurn', game: action.game, pollDelay: action.pollDelay };
      } else if (action.type === 'waiting') {
        return state;
      } else if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error, game: action.game, previousState: state };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'waitingForPlayResult':
      if (action.type === 'gameOver') {
        return { tag: 'gameOver', game: action.game, winner: action.winner };
      } else if (action.type === 'waiting') {
        return { tag: 'waitingForOpponent', game: action.game, pollDelay: action.pollDelay };
      } else if (action.type === 'play') {
        return { tag: 'myTurn', game: action.game, pollDelay: action.pollDelay };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error, game: action.game, previousState: state };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'gameOver':
      if (action.type === 'gameOver') {
        return state;
      } else if (action.type === 'redirect') {
        return { tag: 'redirect', to: action.to };
      } else if (action.type === 'error') {
        return { tag: 'error', error: action.error, game: action.game, previousState: state };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    default:
      logUnexpectedAction(state, action);
      return state;
  }
}

export function Gameplay() {
  const [state, dispatch] = useReducer(reducer, { tag: 'loading', pollDelay: 4000 });
  const { gid } = useParams();
  const gameId = Number(gid);
  const currentUser = getUserName();
  const activeTags = ['waitingForOpponent', 'waitingForPlayResult', 'loading'];
  const isActiveState = (state: State): state is State & { pollDelay: number } => activeTags.includes(state.tag);

  useInterval(
    async () => {
      try {
        const res = await getGame(gameId);
        const currentGame = res.properties.game;
        const pollingTimeout = res.properties.pollingTimeOut;

        if (isMyTurn(currentUser, currentGame)) {
          dispatch({ type: 'play', hasPlayed: false, game: currentGame, pollDelay: pollingTimeout });
        } else if (isGameOver(currentGame)) {
          dispatch({ type: 'gameOver', game: currentGame, winner: handleWinner(currentGame) });
        } else {
          dispatch({ type: 'waiting', game: currentGame, pollDelay: pollingTimeout });
        }
      } catch (error) {
        dispatch({ type: 'error', error: isProblem(error) ? error.detail : error.message });
      }
    },
    isActiveState(state) ? state.pollDelay : null
  );

  // Handlers

  function onPlayHandler(row: number, col: number) {
    if (state.tag !== 'myTurn') return;
    playGame(gameId, row, col)
      .then(result => {
        const currentGame = convertToDomainGame(result.properties.game);
        if (isGameOver(currentGame)) {
          dispatch({ type: 'gameOver', game: currentGame, winner: handleWinner(currentGame) });
        } else {
          dispatch({ type: 'play', hasPlayed: true, game: currentGame, pollDelay: state.pollDelay });
        }
      })
      .catch(error => {
        const badMovesProblems = [ProblemType.INVALID_POSITION, ProblemType.INVALID_TURN, ProblemType.INVALID_TIME];
        if (badMovesProblems.includes(error.typeUri)) {
          dispatch({
            type: 'play',
            hasPlayed: false,
            game: state.game,
            pollDelay: state.pollDelay,
            badMove: error.detail,
          });
        } else {
          dispatch({ type: 'error', error: isProblem(error) ? error.detail : error.message, game: state.game });
        }
      });
  }

  function onResignHandler() {
    if (state.tag !== 'myTurn') return;
    surrenderGame(gameId)
      .then(() => dispatch({ type: 'play', resign: true, game: state.game }))
      .catch(error =>
        dispatch({
          type: 'error',
          error: isProblem(error) ? error.detail : error.message,
          game: state.game,
        })
      );
  }

  const onPlayEnabled = state.tag === 'myTurn';
  return (
    <div>
      {state.tag === 'loading' && <div>Loading...</div>}
      {state.tag === 'waitingForOpponent' && (
        <div>
          <PresentGame game={state.game} onPlayEnabled={onPlayEnabled} />
          <h3>Waiting for opponent...</h3>
        </div>
      )}
      {state.tag === 'waitingForPlayResult' && (
        <div>
          <PresentGame game={state.game} onPlayEnabled={onPlayEnabled} />
          <h3>Waiting for play result...</h3>
        </div>
      )}
      {state.tag === 'myTurn' && (
        <div>
          <PresentGame
            game={state.game}
            onPlay={onPlayHandler}
            onPlayEnabled={onPlayEnabled}
            onResign={onResignHandler}
          />
          <h3>{state.badMove}</h3>
        </div>
      )}
      {state.tag === 'gameOver' && (
        <div>
          <PresentGame game={state.game} onPlayEnabled={onPlayEnabled} />
          <ResultPresentation
            me={currentUser}
            winner={state.winner?.username}
            points={state.game.variant.points}
            onExit={() => dispatch({ type: 'redirect', to: '/me' })}
          />
        </div>
      )}
      {state.tag === 'redirect' && <Navigate to={state.to} />}
      {state.tag === 'error' && state.error && (
        <div>
          <PresentGame game={state.game} onPlay={onPlayHandler} onPlayEnabled={onPlayEnabled} />
          <h3>{state.error}</h3>
        </div>
      )}
    </div>
  );
}
