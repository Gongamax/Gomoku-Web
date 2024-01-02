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
import { NotFoundPage } from './Components/NotFound/NotFoundPage';
import { Gameplay } from './Components/Game/Gameplay/Gameplay';

/**
 * `App` is the root component of the application.
 * It sets up the router for the application using the `RouterProvider` component from `react-router-dom`.
 *
 * @returns {React.ReactElement} - The root component of the application.
 */
export function App(): React.ReactElement {
  return (
      <RouterProvider router={router} />
  );
}

/**
 * `router` is the configuration for the application's router.
 * It defines the paths for the application and the components that should be rendered for each path.
 * It uses the `createBrowserRouter` function from `react-router-dom` to create the router.
 */
const router = createBrowserRouter([
  {
    path: '/',
    element: <NavBarWrapper />,
    children: [
      {
        path: '/',
        element: <HomePage />,
      },
      {
        path: '/users/:uid',
        element: (
          <RequireAuthn>
            <ProfilePage />
          </RequireAuthn>
        ),
      },
      {
        path: '/about',
        element: <AboutPage />,
      },
      {
        path: '/login',
        element: <Login />,
      },
      {
        path: '/register',
        element: <Register />,
      },
      {
        path: '/me',
        element: (
          <RequireAuthn>
            <Me />
          </RequireAuthn>
        ),
      },
      {
        path: '/lobby',
        element: (
          <RequireAuthn>
            <LobbyPage />
          </RequireAuthn>
        ),
      },
      {
        path: '/matchmaking/:mid',
        element: <MatchmakingPage />,
      },
      {
        path: '/ranking',
        element: <RankingPage />,
      },
      {
        path: '/game/:gid',
        element: (
          <RequireAuthn>
            <Gameplay />
          </RequireAuthn>
        ),
      },
      {
        path: '*',
        element: <NotFoundPage />,
      },
    ],
  },
]);
