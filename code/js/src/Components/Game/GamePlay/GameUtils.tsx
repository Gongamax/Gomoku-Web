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
