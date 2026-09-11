import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../api/AuthContext';
import { matchesApi } from '../api/client';

const TABS = [
  { key: 'upcoming', label: 'Upcoming' },
  { key: 'completed', label: 'Completed' },
  { key: 'cancelled', label: 'Cancelled' },
];

function MyMatches() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('upcoming');
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function load() {
      setLoading(true);
      setError('');
      try {
        let data;
        if (activeTab === 'upcoming') data = await matchesApi.getUpcomingForUser(user.userId);
        else if (activeTab === 'completed') data = await matchesApi.getCompletedForUser(user.userId);
        else data = await matchesApi.getCancelledForUser(user.userId);
        setMatches(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    if (user) load();
  }, [activeTab, user]);

  return (
    <div className="page-wide">
      <h1>Your Matches</h1>

      <div className="tabs">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={activeTab === tab.key ? 'tab active' : 'tab'}
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {loading && <p>Loading...</p>}
      {error && <p className="form-error">{error}</p>}

      {!loading && matches.length === 0 && (
        <p className="empty-state">No {activeTab} matches.</p>
      )}

      <ul className="roster-list">
        {matches.map((m) => (
          <li key={m.id}>
            <Link to={`/matches/${m.id}`}>
              <strong>{m.title}</strong> — {m.sportName}, {m.matchDate} at {m.startTime}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default MyMatches;
