import * as React from 'react';
import {Navigate, useLocation} from 'react-router-dom';
import {login} from '../../Service/users/UserServices';
import { isProblem } from '../../Service/media/Problem';

type State =
  | { tag: 'editing'; error?: string; inputs: { username: string; password: string } }
  | { tag: 'submitting'; username: string }
  | { tag: 'redirect' };

type Action =
  | { type: 'edit'; inputName: string; inputValue: string }
  | { type: 'submit' }
  | { type: 'error'; message: string }
  | { type: 'success' };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type} on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      if (action.type === 'edit') {
        return { tag: 'editing', error: undefined, inputs: { ...state.inputs, [action.inputName]: action.inputValue } };
      } else if (action.type === 'submit') {
        return { tag: 'submitting', username: state.inputs.username };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'submitting':
      if (action.type === 'success') {
        return { tag: 'redirect' };
      } else if (action.type === 'error') {
        return { tag: 'editing', error: action.message, inputs: { username: state.username, password: '' } };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'redirect':
      logUnexpectedAction(state, action);
      return state;
  }
}

/**
 * `Login` is a React component that renders a login form.
 * It uses a reducer to manage its state, which can be in one of three states: 'editing', 'submitting', or 'redirect'.
 * In the 'editing' state, the user can enter their username and password.
 * In the 'submitting' state, the component attempts to log the user in.
 * In the 'redirect' state, the user is redirected to their previous location, or to the '/me' page if no previous location is available.
 */
export function Login() {
  const [state, dispatch] = React.useReducer(reduce, { tag: 'editing', inputs: { username: '', password: '' } });
  const location = useLocation();
  if (state.tag === 'redirect') {
    return <Navigate to={location.state?.source?.pathname || '/me'} replace={true} />;
  }

  /**
   * Handles changes to the form inputs.
   * Dispatches an 'edit' action to the reducer with the input name and new value.
   *
   * @param {React.FormEvent<HTMLInputElement>} ev - The form event.
   */
  function handleChange(ev: React.FormEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', inputName: ev.currentTarget.name, inputValue: ev.currentTarget.value });
  }

  /**
   * Handles form submission.
   * Dispatches a 'submit' action to the reducer and attempts to log the user in.
   * If the login is successful, dispatches a 'success' action.
   * If the login fails, dispatches an 'error' action with the error message.
   *
   * @param {React.FormEvent<HTMLFormElement>} ev - The form event.
   */
  function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault();
    if (state.tag !== 'editing') {
      return;
    }
    dispatch({ type: 'submit' });
    const username = state.inputs.username;
    const password = state.inputs.password;
    login(username, password)
      .then(res => {
        if (res) {
          dispatch({ type: 'success' });
        } else {
          dispatch({ type: 'error', message: 'Invalid username or password' });
        }
      })
      .catch((e) => {
        dispatch({ type: 'error', message: isProblem(e) ? e.detail : e.message });
      });
  }

  const username = state.tag === 'submitting' ? state.username : state.inputs.username
  const password = state.tag === 'submitting' ? "" : state.inputs.password
  return (
    <form onSubmit={handleSubmit}>
      <fieldset disabled={state.tag !== 'editing'}>
        <div>
          <label htmlFor="username">Username</label>
          <input id="username" type="text" name="username" value={username} onChange={handleChange} />
        </div>
        <div>
          <label htmlFor="password">Password</label>
          <input id="password" type="password" name="password" value={password} onChange={handleChange} />
        </div>
        <div>
          <button type="submit">Login</button>
        </div>
      </fieldset>
      {state.tag === 'editing' && state.error}
    </form>
  );
}
