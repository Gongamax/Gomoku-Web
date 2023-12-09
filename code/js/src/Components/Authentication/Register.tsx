import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useSetUser } from './Authn';
import { register } from '../../Service/users/UserServices';

type State =
  | {
      tag: 'editing';
      error?: string;
      inputs: { username: string; password: string; confirmPassword: string; email: string };
    }
  | { tag: 'submitting'; username: string; email: string }
  | { tag: 'redirect' };

type Action =
  | { type: 'edit'; inputName: string; inputValue: string }
  | { type: 'submit' }
  | { type: 'error'; message: string }
  | { type: 'success' };

function logUnexpectedAction(state: State, action: Action) {
  console.log(`Unexpected action '${action.type}' on state '${state.tag}'`);
}

function reduce(state: State, action: Action): State {
  switch (state.tag) {
    case 'editing':
      if (action.type === 'edit') {
        return {
          tag: 'editing',
          error: undefined,
          inputs: { ...state.inputs, [action.inputName]: action.inputValue },
        };
      } else if (action.type === 'submit') {
        const { password, confirmPassword } = state.inputs;
        if (password !== confirmPassword) {
          return { tag: 'editing', error: "Passwords don't match", inputs: state.inputs };
        } else {
          return { tag: 'submitting', username: state.inputs.username, email: state.inputs.email };
        }
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'submitting':
      if (action.type === 'success') {
        return { tag: 'redirect' };
      } else {
        logUnexpectedAction(state, action);
        return state;
      }

    case 'redirect':
      logUnexpectedAction(state, action);
      return state;
  }
}
export function Register() {
  console.log('Register');
  const [state, dispatch] = React.useReducer(reduce, {
    tag: 'editing',
    inputs: { username: '', password: '', confirmPassword: '', email: '' },
  });
  const setUser = useSetUser();
  const location = useLocation();
  if (state.tag === 'redirect') {
    // Redirect to a specific page after successful registration
    return <Navigate to={location.state?.source?.pathname || '/me'} replace={true} />;
  }

  function handleChange(ev: React.FormEvent<HTMLInputElement>) {
    dispatch({ type: 'edit', inputName: ev.currentTarget.name, inputValue: ev.currentTarget.value });
  }

  function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
    ev.preventDefault();
    if (state.tag !== 'editing') {
      return;
    }

    dispatch({ type: 'submit' });
    const { username, password, email } = state.inputs;

    register(username, password, email)
      .then(res => {
        if (res) {
          console.log(`setUser(${res})`);
          setUser(username);
          dispatch({ type: 'success' });
        } else {
          dispatch({ type: 'error', message: 'Registration failed. Please try again.' });
        }
      })
      .catch(error => {
        dispatch({ type: 'error', message: error.message });
      });
  }

  const { username, password, confirmPassword, email } =
    state.tag === 'submitting'
      ? { username: state.username, password: '', confirmPassword: '', email: state.email }
      : state.inputs;

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
          <label htmlFor="confirmPassword">Confirm Password</label>
          <input
            id="confirmPassword"
            type="password"
            name="confirmPassword"
            value={confirmPassword}
            onChange={handleChange}
          />
        </div>
        <div>
          <label htmlFor="email">Email</label>
          <input id="email" type="email" name="email" value={email} onChange={handleChange} />
        </div>
        <div>
          <button type="submit">Register</button>
        </div>
      </fieldset>
      {state.tag === 'editing' && state.error && <div>{state.error}</div>}
    </form>
  );
}
