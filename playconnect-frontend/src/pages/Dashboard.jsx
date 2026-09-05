import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../api/AuthContext';
import { profileApi, matchesApi, playersApi } from '../api/client';

function Dashboard() {
  const { user } = useAuth();

  const [nearbyMatches, setNearbyMatches] = useState([]);
  const [nearbyPlayers, setNearbyPlayers] = useState([]);
  const [myMatches, setMyMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadDashboard() {
      try {
        // Profile gives us the saved lat/long needed for match search —
        // player search already has a "for me" shortcut (Day 35), but
        // matches doesn't, so we look the coordinates up ourselves.
        const profile = await profileApi.get(user.userId);

        const [matchesResult, playersResult, myMatchesResult] = await Promise.all([
          profile.latitude != null
            ? matchesApi.getNearby(profile.latitude, profile.longitude)
            : Promise.resolve([]),
          playersApi.getNearbyForMe(user.userId),
          matchesApi.getUpcomingForUser(user.userId),
        ]);

        setNearbyMatches(matchesResult.slice(0, 5));
        setNearbyPlayers(playersResult.slice(0, 5));
        setMyMatches(myMatchesResult.slice(0, 5));
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    if (user) loadDashboard();
  }, [user]);

  if (loading) {
    return <div className="page"><p>Loading your dashboard...</p></div>;
  }

  return (
    <div className="dashboard">
      <h1>Welcome {user.name} 👋</h1>
      {error && <p className="form-error">{error}</p>}

      <div className="dashboard-grid">
        <section className="dashboard-card">
          <h2>📍 Nearby Matches</h2>
          {nearbyMatches.length === 0 ? (
            <p className="empty-state">No matches nearby yet. Be the first to create one!</p>
          ) : (
            <ul>
              {nearbyMatches.map((m) => (
                <li key={m.id}>
                  <strong>{m.title}</strong> — {m.sportName}, {m.distanceKm} km away
                </li>
              ))}
            </ul>
          )}
        </section>

        <section className="dashboard-card">
          <h2>👥 Nearby Players</h2>
          {nearbyPlayers.length === 0 ? (
            <p className="empty-state">No players found nearby.</p>
          ) : (
            <ul>
              {nearbyPlayers.map((p) => (
                <li key={`${p.userId}-${p.sportName}`}>
                  {p.name} — {p.sportName} ({p.skillLevel}), {p.distanceKm} km
                </li>
              ))}
            </ul>
          )}
        </section>

        <section className="dashboard-card">
          <h2>📅 Your Matches</h2>
          {myMatches.length === 0 ? (
            <p className="empty-state">You haven't joined or created any matches yet.</p>
          ) : (
            <ul>
              {myMatches.map((m) => (
                <li key={m.id}>
                  <strong>{m.title}</strong> — {m.matchDate} at {m.startTime}
                </li>
              ))}
            </ul>
          )}
        </section>

        <section className="dashboard-card">
          <h2>⭐ Recommended Players</h2>
          {/* Real scoring (sport/distance/skill/availability match) arrives
              Day 56 — showing nearby players here as a placeholder so the
              dashboard layout is complete today. */}
          {nearbyPlayers.length === 0 ? (
            <p className="empty-state">No recommendations yet.</p>
          ) : (
            <ul>
              {nearbyPlayers.slice(0, 3).map((p) => (
                <li key={`rec-${p.userId}-${p.sportName}`}>{p.name} — {p.sportName}</li>
              ))}
            </ul>
          )}
        </section>
      </div>

      <Link to="/profile" className="dashboard-profile-link">View your profile →</Link>
    </div>
  );
}

export default Dashboard;