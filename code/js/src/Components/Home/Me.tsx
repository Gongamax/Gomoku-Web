import * as React from 'react';
import { getCookie } from '../Authentication/RequireAuthn';

export function Me() {
  const currentUser = getCookie('login')
  return (
    <div>
      {`Hello ${currentUser}`}
    </div>
  );
}