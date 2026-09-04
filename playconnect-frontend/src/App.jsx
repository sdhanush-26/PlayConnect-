import { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import './App.css';

function App() {
  // Placeholder auth state — real login wiring arrives Day 47.
   const [isLoggedIn] = useState(true);
  const [userName] = useState('Guest');

  return (
    <BrowserRouter>
      <Routes>
        {/* Every child route below renders inside Layout's <Outlet />,
            so Navbar/Sidebar/Footer wrap all of them automatically. */}
        <Route element={<Layout isLoggedIn={isLoggedIn} userName={userName} />}>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/profile" element={<Profile />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;