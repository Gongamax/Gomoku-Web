import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useCurrentUser, useSetUser } from './Authn';

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
  const location = useLocation();
  const currentUser = useCurrentUser();
  const setUser = useSetUser();
  const loginCookie = document.cookie
    .split('; ')
    .find((row) => row.startsWith('login'))
    ?.split('=')[1];

  if (loginCookie) {
    if (!currentUser)
      setUser(loginCookie);
    return <>{children}</>;
  } else {
    console.log('redirecting to login');
    return <Navigate to="/login" state={{ source: location.pathname }} replace={true} />;
  }
}

export function useLoggedIn(): boolean {
  return !!useCurrentUser();
}