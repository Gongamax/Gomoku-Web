import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';

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

export function useLoggedIn(): boolean {
  return !!getCookie('login');
}

export function getCookie(name: string): string | undefined {
  return document.cookie
    .split('; ')
    .find((row) => row.startsWith(name))
    ?.split('=')[1];
}