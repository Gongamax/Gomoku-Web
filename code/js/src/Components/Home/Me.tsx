import * as React from 'react';
import { useCurrentUser } from '../Authentication/Authn';

export function Me() {
  const currentUser = useCurrentUser();
  return (
    <div>
      {`Hello ${currentUser}`}
    </div>
  );
}