import * as React from 'react';
import {Link, Outlet, useNavigate} from 'react-router-dom';
import {useLoggedIn} from '../Components/Authentication/RequireAuthn';
import {logout} from '../Service/users/UserServices';

/**
 * `NavBar` is a React functional component that renders a navigation bar.
 * It uses the `useNavigate` hook from `react-router-dom` to programmatically navigate to different routes.
 * It also uses the `useLoggedIn` hook to check if the user is logged in.
 * If the user is logged in, it displays links to the Lobby and Logout.
 * If the user is not logged in, it displays links to Login and Register.
 * The `handleLogout` function logs the user out and navigates to the home page.
 *
 * @returns {React.ReactElement} - The rendered navigation bar.
 */
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

  const bStyle = {
    color: 'black',
    textDecoration: 'none',
  };

  const navigate = useNavigate();
  const loggedIn = useLoggedIn();

  const handleLogout = async () => {
    await logout();
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
            <li style={liStyle}><Link to="/me" style={linkStyle}>Profile</Link></li>
            <li style={liStyle}>
              <button onClick={handleLogout} style={bStyle}>Logout</button>
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

/**
 * `NavBarWrapper` is a React functional component that wraps the `NavBar` component and an `Outlet`.
 * The `Outlet` component is where the child routes of this component will be rendered.
 *
 * @returns {React.ReactElement} - The `NavBar` component and an `Outlet`.
 */
export function NavBarWrapper() {
  return (
    <div>
      <NavBar />
      <Outlet />
    </div>
  );
}