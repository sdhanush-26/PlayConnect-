import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import Footer from './Footer';

// <Outlet /> is React Router's way of saying "render whichever child
// route matched here" — this lets every page share the same Navbar/
// Sidebar/Footer shell without each page file repeating that layout
// code itself.
function Layout({ isLoggedIn, userName }) {
  return (
    <div className="app-shell">
      <Navbar isLoggedIn={isLoggedIn} userName={userName} />
      <div className="app-body">
        <Sidebar isLoggedIn={isLoggedIn} />
        <main className="app-content">
          <Outlet />
        </main>
      </div>
      <Footer />
    </div>
  );
}

export default Layout;
