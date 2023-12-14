import * as React from 'react';
import {Navigate, useParams} from 'react-router-dom';
import {Game} from '../../../Domain/games/Game';
import * as GameService from '../../../Service/games/GamesServices';
import {User} from '../../../Domain/users/User';
import {PresentGame, ResultPresentation} from './GamePresentation';
import {checkTurn, convertToDomainGame, handleWinner, isDraw, isWin} from './GameUtils';
import {getUserName} from '../../Authentication/RequireAuthn';

let pollingTimeOut: number = 3000;

type State =
  | { tag: 'loading' }
  | { tag: 'myTurn'; game: Game }
  | { tag: 'loadingPlay'; game: Game; }
  | { tag: 'opponentTurn'; game: Game }
  | { tag: 'gameOver'; game: Game; winner: User }
  | { tag: 'redirect' }
  | { tag: 'error'; game?: Game, message: string};

type Action =
  | { type: 'makePlay'; game: Game; hasPlayed: boolean}
  | { type: 'error'; message: string; game: Game }
  | { type: 'waiting'; game:Game}
  | { type: 'gameOver'; game: Game; winner: User };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'waiting') {
          return { tag: 'opponentTurn', game: action.game };
      }else if( action.type == 'makePlay'){
          return { tag: 'myTurn', game: action.game };
      }
      else if (action.type === 'error') {
        return { tag: 'error', game: action.game ,message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'myTurn':
      if (action.type === 'makePlay') {
        if (action.hasPlayed)
          return { tag: 'loadingPlay', game: action.game };
        else
          return state;
      }
      else if (action.type === 'waiting') {
        return { tag: 'opponentTurn', game: action.game };
      }
      else if (action.type === 'gameOver') {
        return {tag: 'gameOver', game: action.game, winner: action.winner};
      }
      else if (action.type == 'error'){
        return { tag: 'error', game: action.game, message: action.message }
      }
      else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'opponentTurn':
      if (action.type === 'waiting') {
          return state;
      }
      else if (action.type === 'gameOver'){
        return {tag: 'gameOver', game: action.game, winner: action.winner}
      }
      else if(action.type === 'makePlay') {
        return { tag: 'myTurn', game: action.game };
      }
      else if (action.type == 'error'){
        return { tag: 'error', game: action.game, message: action.message }
      }
      else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'loadingPlay':
      if (action.type === 'gameOver') {
        return {tag: 'gameOver', game: action.game, winner: action.winner};
      }
      else if (action.type == 'waiting'){
          return { tag: 'opponentTurn', game: action.game };
      }
      else if (action.type == 'error'){
        return { tag: 'error', game: action.game, message: action.message }
      }
      else {
        logUnexpectedAction(state, action);
        return state;
      }
    case 'gameOver':
      if (action.type === 'gameOver') {
        return { tag: 'redirect' };
      }
      else if (action.type == 'error'){
        return { tag: 'error', game: action.game, message: action.message }
      }
      else {
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

  const fetchData = async () => {
      const response = (await GameService.getGame(gameId)).properties;
      pollingTimeOut = response.pollingTimeOut;
      const game = convertToDomainGame(response.game)
      if (isWin(game)){
        clearInterval(pollingTimeOut);
        dispatch({
          type: 'gameOver',
          game: game,
          winner: handleWinner(game)
        })
      }
      else if (isDraw(game)){
        clearInterval(pollingTimeOut);
        dispatch({
          type: 'gameOver',
          game: game,
          winner: undefined
        })
      }
      else {
        if (checkTurn(currentUser, response.game)){
          clearInterval(pollingTimeOut);
          dispatch({
            type: 'makePlay',
            game: game,
            hasPlayed: false
          });
        }else{
          dispatch({
            type: 'waiting',
            game: game
          });
        }
      }
  };

  React.useEffect(() => {
    try {
      console.log(`state: ${state.tag}`)
      fetchData().catch(error => handleErrors(undefined, error.message))
    } catch (error) {
      handleErrors(undefined, error.message)
    }
  });

  async function handlePlay(row: number, col: number) {
    if (state.tag === 'myTurn')
      try {
        // Send a request to the server to make a move
        await GameService.playGame(state.game.id, row, col);
        // Update the local game state based on the server's response
        fetchData().catch(error => handleErrors(state.game, error.message))
      } catch (error) {
        handleErrors(state.game, error.message)
      }
  }

  async function handleWaiting(pollingTimeOut: number){
      setInterval(()=> { fetchData() }, pollingTimeOut)
  }

  async function handleResign(): Promise<void> {
    if (state.tag === 'myTurn' || state.tag === 'opponentTurn') {
      try {
        await GameService.surrenderGame(state.game.id);
        fetchData().catch(error => handleErrors(state.game, error.message))
      } catch (error) {
        console.error('Failed to resign:', error);
      }
    }
  }

  function handleExit(){
   if (state.tag === 'gameOver') {
     dispatch({
       type: 'gameOver',
       game: state.game,
       winner: handleWinner(state.game),
     })
   }
  }

  function handleErrors(game: Game, error: string){
    dispatch({
      type:'error',
      game: game,
      message: error
    })
  }

  switch (state.tag) {
    case 'loading':
      return (
          <div>
            <h3>Loading...</h3>
          </div>
      );

    case 'myTurn': {
      return (
        <div>
          <PresentGame game={state.game} onPlay={ handlePlay } onResign={ handleResign }/>
        </div>
      );
    }

    case 'opponentTurn': {
      handleWaiting(pollingTimeOut).catch(error => dispatch({
        type: 'error',
        game: state.game,
        message: error.toString()
      }))
      return (
        <div>
          <PresentGame game={state.game} onPlay={ () => { } } onResign={ handleResign }/>
          <h3>Waiting for opponent to play...</h3>
        </div>
      );
    }

    case 'loadingPlay':
      return <div>
        <div>
          <PresentGame game={state.game} onPlay={ () => { } } onResign={ () => { } }/>
          <h3>loading Play...</h3>
        </div>
      </div>;

    case 'gameOver':
      return (
        <div>
          <PresentGame game={state.game} onPlay={ () => { } } onResign={ () => { } }/>
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
          <PresentGame game={state.game} onPlay={ () => { } } onResign={ () => { } }/>
          <h3>{state.message}</h3>
          <button onClick={() => fetchData()}>Retry</button>
        </div>
      );
    case 'redirect':
      return <Navigate to="/me" />;
  }
}
