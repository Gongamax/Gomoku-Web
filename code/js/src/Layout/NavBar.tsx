import * as React from 'react';
import { Link } from 'react-router-dom';

export function NavBar() {
  return (
    <nav>
      <ul>
        <li>
          <Link to="/">Home</Link>
        </li>
        <li>
          <Link to="/login">Login</Link>
        </li>
        <li>
          <Link to="/register">Register</Link>
        </li>
        <li>
          <Link to="/ranking">Ranking</Link>
        </li>
        <li>
          <Link to="/lobby">Lobby</Link>
        </li>
        <li>
          <Link to="/about">About</Link>
        </li>

        {/* Add a Logout option here if the user is authenticated */}
      </ul>
    </nav>
  );
}
