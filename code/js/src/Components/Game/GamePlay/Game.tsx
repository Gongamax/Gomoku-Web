import * as React from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { Game, GameState } from '../../../Domain/games/Game';
import * as GameService from '../../../Service/games/GamesServices';
import { User } from '../../../Domain/users/User';
import { GameBoard } from './GameBoard';
import { checkTurn, convertToDomainGame, deserializeBoard, handleWinner } from './GameUtils';
import { getUserName } from '../../Authentication/RequireAuthn';

type State =
  | { tag: 'loading' }
  | { tag: 'myTurn'; game: Game }
  | { tag: 'loadingPlay'; game: Game; resign: boolean }
  | { tag: 'opponentTurn'; game: Game }
  | { tag: 'gameOver'; game: Game; winner: User }
  | { tag: 'redirect' }
  | { tag: 'error'; message: string };

type Action =
  | { type: 'makePlay'; game: Game; resign: boolean }
  | { type: 'error'; message: string; game?: Game; isMyTurn?: boolean }
  | { type: 'success'; game: Game; isMyTurn: boolean; isOver: boolean; winner?: User };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'success') {
        if (action.isMyTurn)
          return { tag: 'myTurn', game: action.game };
        else
          return { tag: 'opponentTurn', game: action.game };
      } else if (action.type === 'error') {
        return { tag: 'error', message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'myTurn':
      if (action.type === 'makePlay') {
        return { tag: 'loadingPlay', game: action.game, resign: action.resign };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'opponentTurn':
      if (action.type === 'success') {
        if (action.isOver) {
          return { tag: 'gameOver', game: action.game, winner: action.winner };
        } else {
          return { tag: 'myTurn', game: action.game };
        }
      } else if (action.type === 'error') {
        return state;
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'loadingPlay':
      if (action.type === 'success') {
        if (action.isOver) {
          return { tag: 'gameOver', game: action.game, winner: action.winner };
        } else {
          return { tag: 'myTurn', game: action.game };
        }
      } else if (action.type === 'error') {
        return { tag: 'myTurn', game: action.game };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'gameOver':
      if (action.type === 'success') {
        return { tag: 'redirect' };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'error':
      return state;
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
  const intervalId = React.useRef<NodeJS.Timeout | null>(null);

  const fetchData = React.useCallback(async () => {
    const response = (await GameService.getGame(gameId)).properties;
    const isMyTurn: boolean = checkTurn(currentUser, response.game);
    dispatch({
      type: 'success',
      game: convertToDomainGame(response.game),
      isMyTurn: isMyTurn,
      isOver: response.game.state === GameState.PLAYER_BLACK_WON || response.game.state === GameState.PLAYER_WHITE_WON,
    });
  }, [gameId, currentUser, dispatch]);

  React.useEffect(() => {
      intervalId.current = setInterval(async () => {
        try {
          await fetchData();
        } catch (error) {
          console.warn('Failed to fetch game:', error);
        }
      }, 5000);
    return () => {
      if (intervalId.current) {
        clearInterval(intervalId.current);
      }
    };
  }, [gameId, currentUser, state, fetchData]);

  async function handlePlay(row: number, col: number) {
    if (state.tag === 'myTurn')
      try {
        // Send a request to the server to make a move
        const updatedGame = await GameService.playGame(state.game.id, row, col);
        // Update the local game state based on the server's response
        dispatch({
          type: 'makePlay',
          game: convertToDomainGame(updatedGame.properties.game),
          resign: false,
        });
      } catch (error) {
        console.error('Failed to make a move:', error);
      }
  }

  async function handleResign(): Promise<void> {
    const gameState = state as { tag: 'myTurn'; game: Game; isMyTurn: boolean };
    try {
      await GameService.surrenderGame(gameState.game.id);
      dispatch(
        {
          type: 'success',
          game: gameState.game,
          isMyTurn: gameState.isMyTurn,
          isOver: true,
          winner: handleWinner(gameState.game),
        },
      );
    } catch (error) {
      console.error('Failed to resign:', error);
    }
  }

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'myTurn': {
      const {
        board,
        turn,
      } = deserializeBoard(
        state.game.board.moves,
        state.game.board.turn?.toString() ?? state.game.board.winner.toString(),
        state.game.variant.boardDim,
      );
      return (
        <div>
          <h1>{state.game.players[0].username} vs {state.game.players[1].username}</h1>
          <h2>Turn: {turn}</h2>
          <GameBoard board={board} onPlay={handlePlay} />
          <button
            onClick={handleResign}>Surrender
          </button>
        </div>
      );
    }

    case 'opponentTurn': {
      const {
        board,
        turn,
      } = deserializeBoard(
        state.game.board.moves,
        state.game.board.turn?.toString() ?? state.game.board.winner.toString(),
        state.game.variant.boardDim,
      );
      return (
        <div>
          <h1>{state.game.players[0].username} vs {state.game.players[1].username}</h1>
          <h2>Turn: {turn}</h2>
          <GameBoard board={board} onPlay={handlePlay} />
          <button
            onClick={handleResign}>Surrender
          </button>
        </div>
      );
    }

    case 'loadingPlay':
      return <div>
        <div>
          <h1>{state.game.players[0].username} vs {state.game.players[1].username}</h1>
          <h2>loading...</h2>
        </div>
      </div>;

    case 'gameOver':
      return (
        <div>
          <h1>Game Over</h1>
          <h2>{state.winner.username} won!</h2>
          <h2>Points: +{state.game.variant.points}</h2>
          <Navigate to="/" />
        </div>
      );
    case 'error':
      return (
        <div>
          <h3>{state.message}</h3>
          {<button onClick={() => fetchData()}>Retry</button>}
        </div>
      );
    case 'redirect':
      return <Navigate to="/" />;
  }
}


