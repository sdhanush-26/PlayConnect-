import { Link } from 'react-router-dom';

// Demonstrates PROPS: this component receives isLoggedIn and userName
// as inputs from whatever parent renders it (App.jsx), rather than
// knowing about login state itself. That's the core idea of props —
// data flows one direction, parent to child.
function Navbar({ isLoggedIn, userName }) {
  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">PlayConnect 🏏</Link>
      <div className="navbar-links">
        <Link to="/">Home</Link>
        {isLoggedIn ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/profile">Profile</Link>
            <span className="navbar-user">Hi, {userName}</span>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
