import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';

/**
 * `RequireAuthn` is a higher-order component that wraps around other components to provide authentication.
 * It checks if the user is logged in by looking for a 'login' cookie.
 * If the user is logged in, it renders the children components.
 * If the user is not logged in, it redirects the user to the login page.
 *
 * @param {React.ReactNode} children - The components to be rendered if the user is authenticated.
 * @returns {React.ReactElement} - The children components if the user is authenticated, otherwise a redirection to the login page.
 */
export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
  const location = useLocation();
  const loginCookie = getCookie('login')
  if (loginCookie) {
    return <>{children}</>;
  } else {
    console.log('redirecting to login');
    return <Navigate to="/login" state={{ source: location.pathname }} replace={true} />;
  }
}

/**
 * `useLoggedIn` is a custom hook that checks if the user is logged in.
 * It uses the `getCookie` function to check for a 'login' cookie.
 *
 * @returns {boolean} - True if the user is logged in, false otherwise.
 */
export function useLoggedIn(): boolean {
  return !!getCookie('login');
}

/**
 * `getCookie` is a function that retrieves a cookie by its name.
 *  This function was taken from https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie
 * @param {string} name - The name of the cookie to retrieve.
 * @returns {string | null} - The value of the cookie if it exists, null otherwise.
 * @see https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie
 */
export function getCookie(name: string): string | null {
  return document.cookie
    .split('; ')
    .find((row) => row.startsWith(name))
    ?.split('=')[1];
}

/**
 * `getUserName` is a function that retrieves the username of the logged in user.
 * It uses the `getCookie` function to retrieve the 'login' cookie, which contains the username.
 *
 * @returns {string | null} - The username of the logged in user if they are logged in, undefined otherwise.
 */
export function getUserName(): string | null{
  return getCookie('login');
}