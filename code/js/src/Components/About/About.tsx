import * as React from 'react';

/**
 * `AboutPage` is a React functional component that renders the About page of the application.
 * It provides information about the application, including the technologies used to build it and where to find its source code.
 */
export function AboutPage() {
  return (
    <div>
      <h1>About</h1>
      <p>Gomoku Royale is an engaging game, this client application was developed using the power of React and
        TypeScript.</p>
      <p>
        The source code is available on <a href="https://github.com/isel-leic-daw/2023-daw-leic51d-02">GitHub</a>.
      </p>
      <p>Authors: Gonçalo Frutuoso and Daniel Carvalho</p>
    </div>
  );
}
