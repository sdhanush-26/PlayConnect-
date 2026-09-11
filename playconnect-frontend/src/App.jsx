import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './api/AuthContext';
import Layout from './components/Layout';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Profile from './pages/Profile';
import PlayerSearch from './pages/PlayerSearch';
import CreateMatch from './pages/CreateMatch';
import MatchDetails from './pages/MatchDetails';
import MyMatches from './pages/MyMatches';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<Layout />}>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/dashboard/players" element={<PlayerSearch />} />
            <Route path="/dashboard/my-matches" element={<MyMatches />} />
            <Route path="/matches/create" element={<CreateMatch />} />
            <Route path="/matches/:matchId" element={<MatchDetails />} />
            <Route path="/profile" element={<Profile />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;