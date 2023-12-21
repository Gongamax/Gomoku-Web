import * as React from 'react';
import { Link } from 'react-router-dom';
import App_Icon from '../../Assets/AppIcon.png';

/**
 * `HomePage` is a React functional component that renders the home page of the application.
 * It displays a welcome message, the application's logo, a brief description of the game, and a button to start a new game.
 * The `Link` component is used to create a link to the lobby page, which is where the user can start a new game.
 *
 * @returns {React.ReactElement} - The rendered home page.
 */
export function HomePage() {
  return (
    <div>
      <h1>Welcome to Gomoku Royale</h1>
      <img src={App_Icon} alt="Gomoku Royale Logo" style={{ width: '200px', height: 'auto' }} />
      <p>Play the most exciting Gomoku game and climb to the top of the rankings!</p>
      <Link to="/lobby">
        <button>Play a Game</button>
      </Link>
    </div>
  );
}