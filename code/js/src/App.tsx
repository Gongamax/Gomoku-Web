import * as React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Register } from './Components/Authentication/Register';
import { AboutPage } from './Components/About/About';
import { HomePage } from './Components/Home/Home';
import { Login } from './Components/Authentication/Login';
import { RankingPage } from './Components/Ranking/Ranking';
import { LobbyPage } from './Components/Game/Lobby/Lobby';
import { ProfilePage } from './Components/Profile/Profile';
import { MatchmakingPage } from './Components/Game/Matchmaking/Matchmaking';
import { NavBar } from './Layout/NavBar';

export function App() {
  return (
    <div className="App">
      <Router>
        <NavBar />

        <div className="App-body">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/ranking" element={<RankingPage />} />
            <Route path="/lobby" element={<LobbyPage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="/users/:uid" element={<ProfilePage />} />
            <Route path="/matchmaking/:mid" element={<MatchmakingPage />} />
            {/* TODO: ADD MORE ROUTS */}
          </Routes>
        </div>
      </Router>
    </div>
  );
}
