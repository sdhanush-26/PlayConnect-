import { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import './App.css';

function App() {
  // Placeholder auth state — real login wiring (reading a stored JWT,
  // etc.) arrives Day 47. For now this just proves Navbar's props
  // actually change what renders.
  const [isLoggedIn] = useState(false);
  const [userName] = useState('Guest');

  return (
    <BrowserRouter>
      <Navbar isLoggedIn={isLoggedIn} userName={userName} />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/profile" element={<Profile />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
