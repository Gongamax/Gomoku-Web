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
import {GamePage} from "./Components/Game/GamePlay/Game";

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
        'children': [
          {
            'path': 'about',
            'element': <AboutPage />,
          },
          {
            'path': 'users/:uid',
            'element': <ProfilePage />,
            'children': [
              // {
              //   'path': 'edit',
              //   'element': <p>Edit</p>,
              // },
              // {
              //   'path': '/history',
              //   'element': <p>UserHistory</p>,
              // },
              // {
              //   'path': '/stats',
              //   'element': <p>UserStats</p>,
              // },
            ],
          },
        ],
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
        'children': [
          {
            'path': 'matchmaking',
            'element': <MatchmakingPage />,
          },
        ],
      },
      {
        'path': '/ranking',
        'element': <RankingPage />,
      },
      {
            'path': '/game:gameId',
            'element': <RequireAuthn><GamePage/></RequireAuthn>,
      },
    ],
  },
]);