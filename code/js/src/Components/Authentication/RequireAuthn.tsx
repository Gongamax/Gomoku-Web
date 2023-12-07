import * as React from 'react';
import { Navigate, useLocation } from 'react-router-dom';

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
  const location = useLocation();
  const loginCookie = document.cookie
    .split('; ')
    .find((row) => row.startsWith('login'))
    ?.split('=')[1];

  console.log(`loginCookie = ${loginCookie}`);
  if (loginCookie) {
    return <>{children}</>;
  } else {
    console.log('redirecting to login');
    return <Navigate to='/login' state={{ source: location.pathname }} replace={true} />;
  }
}