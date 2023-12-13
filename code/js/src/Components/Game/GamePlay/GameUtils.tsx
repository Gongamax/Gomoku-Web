import {GameOutputModel} from '../../../Service/games/models/GameModelsUtil';
import {Game, GameState} from '../../../Domain/games/Game';
import {User} from '../../../Domain/users/User';

export function checkTurn(username: string, game: GameOutputModel) {
  if (game.state == GameState.NEXT_PLAYER_BLACK && game.userBlack.username == username) {
    return true;
  } else{
    return game.state == GameState.NEXT_PLAYER_WHITE && game.userWhite.username == username;
  }
}

export function isWin(game: Game): boolean{
    return game.state === GameState.PLAYER_BLACK_WON ||
          game.state === GameState.PLAYER_WHITE_WON
}

export function isDraw(game: Game): boolean{
    return game.state === GameState.DRAW
}

export function deserializeBoard(moves: { [key: string]: string }, turn: string, boardSize: number): {
  board: string[][],
  turn: string
} {
  const board: string[][] = Array(boardSize).fill(null).map(() => Array(boardSize).fill(''));
  // Iterate over the keys of the moves object
  for (const key in moves) {
    const [rowPart, colPart] = key.split('');
    const row = parseInt(rowPart) - 1; // Subtract 1 because array indices are 0-based
    const col = colPart.charCodeAt(0) - 'A'.charCodeAt(0); // Subtract 'A'.charCodeAt(0) to get a 0-based index
    board[row][col] = moves[key];
  }

  return { board, turn };
}

/*check who is the winner*/
export function handleWinner(game: Game): User {
  switch (game.state){
    case  GameState.PLAYER_WHITE_WON:
      return game.players[1];
    case GameState.PLAYER_BLACK_WON:
      return game.players[0];
  }
}

export function convertToDomainGame(gameOutputModel: GameOutputModel ): Game {
  return {
    id: gameOutputModel.id,
    players: [gameOutputModel.userBlack, gameOutputModel.userWhite],
    board: gameOutputModel.board,
    state: gameOutputModel.state,
    variant: gameOutputModel.variant
  };
}
