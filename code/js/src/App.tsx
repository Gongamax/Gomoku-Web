import * as React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Register } from './Components/Authentication/Register';
import { AboutPage } from './Components/About/About';
import { HomePage } from './Components/Home/Home';
import { Login } from './Components/Authentication/Login';
import { RankingPage } from './Components/Ranking/Ranking';
import { LobbyPage } from './Components/Game/Lobby/Lobby';
import { ProfilePage } from './Components/Profile/Profile';
import { MatchmakingPage } from './Components/Game/Matchmaking/Matchmaking';
import { RequireAuthn } from './Components/Authentication/RequireAuthn';
import { Me } from './Components/Home/Me';
import { NavBarWrapper } from './Layout/NavBar';
import { GamePage } from './Components/Game/GamePlay/Game';
import { MatchHistory } from './Components/Profile/MatchHistory';

export function App() {
  return (
    <RouterProvider router={router} />
  );
}

const router = createBrowserRouter([
  {
    'path': '/',
    'element': <NavBarWrapper />,
    'children': [
      {
        'path': '/',
        'element': <HomePage />,
      },
      {
        'path': '/users/:uid',
        'element': <RequireAuthn><ProfilePage /></RequireAuthn>,
        'children': [
          {
            'path': 'history',
            'element': <MatchHistory />,
          },
        ],
      },
      {
        'path': '/about',
        'element': <AboutPage />,
      },
      {
        'path': '/login',
        'element': <Login />,
      },
      {
        'path': '/register',
        'element': <Register />,
      },
      {
        'path': '/me',
        'element': <RequireAuthn><Me /></RequireAuthn>,
      },
      {
        'path': '/lobby',
        'element': <RequireAuthn><LobbyPage /></RequireAuthn>,
      },
      {
        'path': '/matchmaking/:mid',
        'element': <MatchmakingPage />,
      },
      {
        'path': '/ranking',
        'element': <RankingPage />,
      },
      {
        'path': '/game/:gid',
        'element': <RequireAuthn><GamePage /></RequireAuthn>,
      },
    ],
  },
]);