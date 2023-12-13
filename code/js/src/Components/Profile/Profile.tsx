import * as React from 'react';
import {useEffect} from 'react';
import {getStatsById} from '../../Service/users/UserServices';
import {UserInfo} from '../../Domain/users/UserInfo';
import {getAllGamesByUser} from "../../Service/games/GamesServices";
import {convertToDomainMatchHistory, convertToDomainUser} from "./ProfileUtil";
import {useParams} from "react-router-dom";

/**
 * `Match` is a type that represents a match.
 * It includes properties for `opponent` and `result`.
 */

type State =
    | { tag: 'loading' }
    | { tag: 'presentOnlyInfo'; userInfo: UserInfo}
    | { tag: 'presentEverything'; userInfo: UserInfo }
    | { tag: 'error'; message: string };

type Action =
  | { type: 'loadSuccess'; userInfo: UserInfo, presentHistory: boolean}
  | { type: 'loadError'; message: string };

/**
 * `logUnexpectedAction` is a function that logs an unexpected action.
 * It is called when an action is dispatched that is not expected in the current state.
 *
 * @param {State} state - The current state.
 * @param {Action} action - The action that was dispatched.
 */
const logUnexpectedAction = (state: State, action: Action) => {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
};

/**
 * `reduce` is a reducer function for the ProfilePage component.
 * It takes the current state and an action, and returns the new state.
 *
 * @param {State} state - The current state.
 * @param {Action} action - The action to handle.
 * @returns {State} - The new state.
 */
const reduce = (state: State, action: Action): State => {
  switch (state.tag) {
    case 'loading':
      if (action.type === 'loadSuccess') {
        return { tag: 'presentOnlyInfo', userInfo: action.userInfo };
      } else if (action.type === 'loadError') {
        return { tag: 'error', message: action.message };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'presentOnlyInfo':
      if(action.type === 'loadSuccess'){
          if (action.presentHistory){
             return  { tag: "presentEverything", userInfo: action.userInfo }
          }
          else  return state
      }
      else if(action.type === 'loadError'){
        return { tag: 'error', message: action.message };
      }
      else  logUnexpectedAction(state, action);
      return state;

    case "presentEverything":
      return state;

    case 'error':
      logUnexpectedAction(state, action);
      return state;
  }
};

/**
 * `ProfilePage` is a React functional component that renders the profile page of a user.
 * It fetches the user's information and match history from the server when the component mounts and stores it in state.
 * The user's information and match history are displayed in a table.
 *
 * @returns {React.ReactElement} - The rendered profile page.
 */
export function ProfilePage() {
  const { uid } = useParams<{ uid: string }>();
  const [state, dispatch] = React.useReducer(reduce, { tag: 'loading' });
  const [matchHistory, setMatchHistory] = React.useState([]);

  useEffect(() => {
    async function fetchData() {
      try {
        const userResponse = await getStatsById(Number(uid));
        const historyResponse = await getAllGamesByUser(Number(uid));
        const userInfo = convertToDomainUser(userResponse);
        const history = convertToDomainMatchHistory(historyResponse, Number(uid))
        setMatchHistory(history);
        dispatch({ type: 'loadSuccess', userInfo: userInfo, presentHistory: false });
      } catch (error) {
        dispatch({ type: 'loadError', message: error.message });
      }
    }
    fetchData();
  }, [uid]);

  async function handleHistoryRequest(){
    const userState = state as { tag: 'presentOnlyInfo'; userInfo: UserInfo}
    dispatch({ type: 'loadSuccess', userInfo: userState.userInfo, presentHistory: true });
  }

  switch (state.tag) {
    case 'loading':
      return <div>Loading...</div>;

    case 'presentOnlyInfo':
      return (
        <div>
          <h1>User Profile: {state.userInfo.username}</h1>
          <p>Wins: {state.userInfo.wins}</p>
          <p>Losses: {state.userInfo.losses}</p>
          <p>Draws: {state.userInfo.draws}</p>
          <p>Games Played: {state.userInfo.gamesPlayed}</p>
          <button onClick={handleHistoryRequest}>Match History</button>
        </div>
      );

    case 'presentEverything':
      return (
          <div>
            <h1>User Profile: {state.userInfo.username}</h1>
            <p>Wins: {state.userInfo.wins}</p>
            <p>Losses: {state.userInfo.losses}</p>
            <p>Draws: {state.userInfo.draws}</p>
            <p>Games Played: {state.userInfo.gamesPlayed}</p>
            <h2>Match History</h2>
            <table style={{width: '100%', borderCollapse: 'collapse'}}>
              <thead>
              <tr style={{borderBottom: '1px solid #ccc'}}>
                <th style={{padding: '10px', textAlign: 'left'}}>Opponent</th>
                <th style={{padding: '10px', textAlign: 'left'}}>Result</th>
              </tr>
              </thead>
              <tbody>
              {matchHistory.map((match, index) => (
                  <tr key={index} style={{borderBottom: '1px solid #eee'}}>
                    <td style={{padding: '10px'}}><a
                        href={`/users/${match.opponent.id.value}`}>{match.opponent.username}</a></td>
                    <td style={{padding: '10px'}}>{match.result}</td>
                  </tr>
              ))}
              </tbody>
            </table>
          </div>
      );

    case 'error':
      return <div>Error: {state.message}</div>;

    default:
      return <div>Unexpected state</div>;
  }
}
