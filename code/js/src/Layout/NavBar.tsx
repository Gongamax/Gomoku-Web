import * as React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useLoggedIn } from '../Components/Authentication/RequireAuthn';
import { logout } from '../Service/users/UserServices';
import { useSetUser } from '../Components/Authentication/Authn';

function NavBar() {
  const navStyle = {
    backgroundColor: '#333',
    display: 'flex',
    justifyContent: 'space-around',
    padding: '1em',
    listStyleType: 'none',
  };

  const ulStyle = {
    display: 'flex',
    justifyContent: 'space-around',
    listStyleType: 'none',
    padding: 0,
    margin: 0,
  };

  const liStyle = {
    display: 'inline',
    margin: '0 1em',
  };

  const linkStyle = {
    color: 'white',
    textDecoration: 'none',
  };

  const navigate = useNavigate();
  const loggedIn = useLoggedIn();
  const setUser = useSetUser();

  const handleLogout = async () => {
    await logout();
    setUser(null);
    navigate('/');
  }

  return (
    <nav style={navStyle}>
      <ul style={ulStyle}>
        <li style={liStyle}><Link to="/" style={linkStyle}>Home</Link></li>
        <li style={liStyle}><Link to="/about" style={linkStyle}>About</Link></li>
        <li style={liStyle}><Link to="/ranking" style={linkStyle}>Ranking</Link></li>
        {loggedIn ? (
          <>
            <li style={liStyle}><Link to="/lobby" style={linkStyle}>Lobby</Link></li>
            {/*<li style={liStyle}><Link to="/profile" style={linkStyle}>Profile</Link></li>*/}
            <li style={liStyle}>
              <button onClick={handleLogout} style={linkStyle}>Logout</button>
            </li>
          </>
        ) : (
          <>
            <li style={liStyle}><Link to="/login" style={linkStyle}>Login</Link></li>
            <li style={liStyle}><Link to="/register" style={linkStyle}>Register</Link></li>
          </>
        )}
      </ul>
    </nav>
  );
}

export function NavBarWrapper() {
  return (
    <div>
      <NavBar />
      <Outlet />
    </div>
  );
}