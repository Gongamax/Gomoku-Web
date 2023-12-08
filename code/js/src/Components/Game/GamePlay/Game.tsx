import * as React from 'react';
import { Navigate } from 'react-router-dom';
import { Game, GameState } from '../../../Domain/games/Game';
import { GamesServices } from '../../../Service/games/GamesServices';
import {Board} from "../../../Domain/games/Board";

type State =
  | { tag: 'loading' }
  | { tag: 'turn'; game: Game; isMyTurn: boolean }
  | { tag: 'loadingPlay'; game: Game; resign: boolean }
  | { tag: 'gameOver'; game: Game; winner: string }
  | { tag: 'redirect' }
  | { tag: 'error'; message: string };

type Action =
  | { type: 'makePlay'; game: Game; isMyTurn: boolean; resign: boolean }
  | { type: 'error'; message: string; game?: Game; isMyTurn?: boolean }
  | { type: 'success'; game: Game; isMyTurn: boolean; isOver: boolean; winner: string };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'success') {
        return { tag: 'turn', game: action.game, isMyTurn: action.isMyTurn };
      }else if (action.type === 'error') {
        return { tag: 'error', message: action.message };
      }
      else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'turn':
      if (state.isMyTurn) {
        if (action.type === 'makePlay') {
          return { tag: 'loadingPlay', game: action.game, resign: action.resign };
        } else {
          logUnexpectedAction(state, action);
          return state;
        }
      } else {
        return state;
      }

    case 'loadingPlay':
      if (action.type === 'success') {
        if (action.isOver) {
          return { tag: 'gameOver', game: action.game, winner: action.winner };
        } else {
          return { tag: 'turn', game: action.game, isMyTurn: !action.isMyTurn };
        }
      } else if (action.type === 'error') {
        return { tag: 'turn', game: action.game, isMyTurn: action.isMyTurn };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'error':
      return state
    case 'redirect':
      logUnexpectedAction(state, action);
      return state;
  }
}

export function GamePage() {
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });
  async function fetchData() {
    const gameServices = new GamesServices();
    const response = (await gameServices.getGame(1)).properties;
    const isMyTurn = "username" == response.game.userBlack.username;
    dispatch({
      type: 'success',
      game: {
        id: response.game.id,
        players: [response.game.userBlack, response.game.userWhite],
        board: response.game.board,
        state: response.game.state,
        variant: {
          name: response.game.variant.name,
          board_dim: response.game.variant.board_dim,
          points: response.game.variant.points,
        },
      },
      isMyTurn: isMyTurn,
      isOver: response.game.state == GameState.PLAYER_BLACK_WON || response.game.state == GameState.PLAYER_WHITE_WON,
      winner:
        response.game.state == GameState.PLAYER_BLACK_WON
          ? response.game.userBlack.username
          : response.game.userWhite.username,
    });
  }
  React.useEffect(() => {
      fetchData()
          .catch((error) => {
              dispatch({ type: 'error', message: error.message });
          });
  }, []);

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'turn':
      return (
            <div>
              <h1>{state.game.players[0].username} vs {state.game.players[1].username}</h1>
            </div>
      );

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
            <h1>{state.game.players[0].username} vs {state.game.players[1].username}</h1>
            <h1>{state.winner} won!</h1>
            <h2>Points: +{state.game.variant.points}</h2>
          </div>
      );

    case 'redirect':
      return <Navigate to="/"/>;
  }
}

/*create a html function to create a board given the game.board*/
function createBoard(board: Board, board_dim: number) {
    let table: HTMLTableElement = document.createElement('table');
    for (let i: number = 0; i < board_dim; i++) {
        let row: HTMLTableRowElement = document.createElement('tr');
        for (let j: number = 0; board_dim; j++) {
            let cell: HTMLTableCellElement = document.createElement('td');
            cell.innerHTML = board.moves[i*board_dim+j].piece;
            row.appendChild(cell);
        }
        table.appendChild(row);
    }
    return table;
}