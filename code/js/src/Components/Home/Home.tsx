import * as React from 'react';
import { Link } from 'react-router-dom';
import App_Icon from '../../Assets/app_icon.png'

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