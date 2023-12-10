import { GameOutputModel } from '../../../Service/games/models/GameModelsUtil';
import { Game, GameState } from '../../../Domain/games/Game';
import { User } from '../../../Domain/users/User';

export function checkTurn(username: string, game: GameOutputModel) {
  console.log("White user: " + game.userWhite.username + " Black user: " + game.userBlack.username + " Current user: " + username)
  if (game.state == GameState.NEXT_PLAYER_BLACK && game.userBlack.username == username) {
    return true;
  } else{
    return game.state == GameState.NEXT_PLAYER_WHITE && game.userWhite.username == username;
  }
}

export function deserializeBoard(moves: { [key: string]: string }, turn: string, boardSize: number): {
  board: string[][],
  turn: string
} {
  const board: string[][] = Array(boardSize).fill(null).map(() => Array(boardSize).fill(''));

  // Iterate over the keys of the moves object
  for (const key in moves) {
    // Split the key into row and column parts
    const [rowPart, colPart] = key.split('');
    console.log("Row: " + rowPart + " Col: " + colPart);
    // Convert the row and column parts into indices
    const row = parseInt(rowPart) - 1; // Subtract 1 because array indices are 0-based
    const col = parseInt(colPart) - 1; // Subtract 1 because array indices are 0-based

    // Place the corresponding piece in the board
    board[row][col] = moves[key];
    //console.log("Board: " + JSON.stringify(board));
  }

  return { board, turn };
}

/*check who is the winner*/
export function handleWinner(game: Game): User {
  if (game.state == GameState.PLAYER_BLACK_WON) {
    return game.players[0];
  } else return game.players[1];
}

export function convertToDomainGame(gameOutputModel: GameOutputModel): Game {
  return {
    id: gameOutputModel.id,
    players: [gameOutputModel.userBlack, gameOutputModel.userWhite],
    board: gameOutputModel.board,
    state: gameOutputModel.state,
    variant: gameOutputModel.variant,
  };
}