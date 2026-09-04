import { Link, useLocation } from 'react-router-dom';

// Only shown when logged in — matches the plan's Dashboard sidebar
// (Nearby Matches, Nearby Players, Your Matches, Recommended Players).
// Those become real, clickable sections starting Day 48; today this is
// just the navigational shell.
function Sidebar({ isLoggedIn }) {
  const location = useLocation();

  if (!isLoggedIn) return null;

  const links = [
    { to: '/dashboard', label: '🏠 Dashboard' },
    { to: '/dashboard/matches', label: '📍 Nearby Matches' },
    { to: '/dashboard/players', label: '👥 Nearby Players' },
    { to: '/dashboard/my-matches', label: '📅 Your Matches' },
    { to: '/profile', label: '⭐ Profile' },
  ];

  return (
    <aside className="sidebar">
      {links.map((link) => (
        <Link
          key={link.to}
          to={link.to}
          className={location.pathname === link.to ? 'sidebar-link active' : 'sidebar-link'}
        >
          {link.label}
        </Link>
      ))}
    </aside>
  );
}

export default Sidebar;
