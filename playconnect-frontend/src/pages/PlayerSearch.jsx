import { useState, useEffect } from 'react';
import { useAuth } from '../api/AuthContext';
import { sportsApi, searchApi, profileApi } from '../api/client';

const SKILL_LEVELS = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'PRO'];

function PlayerSearch() {
  const { user } = useAuth();

  const [sports, setSports] = useState([]);
  const [sportId, setSportId] = useState('');
  const [skillLevel, setSkillLevel] = useState('');
  const [radiusKm, setRadiusKm] = useState('25');
  const [myLocation, setMyLocation] = useState(null);

  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [hasSearched, setHasSearched] = useState(false);

  // Load the sport list once for the dropdown, and the user's own saved
  // location once so distance filtering has something to compare against.
  useEffect(() => {
    sportsApi.getAll().then(setSports).catch(() => {});
    if (user) {
      profileApi.get(user.userId)
        .then((p) => setMyLocation({ latitude: p.latitude, longitude: p.longitude }))
        .catch(() => {});
    }
  }, [user]);

  async function handleSearch(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    setHasSearched(true);
    try {
      const filters = {
        sportId: sportId || undefined,
        skillLevel: skillLevel || undefined,
      };
      // Only add distance filtering if we actually have a location to
      // measure from — searching with radius but no coordinates would
      // silently return everyone, which is confusing rather than useful.
      if (myLocation?.latitude != null) {
        filters.latitude = myLocation.latitude;
        filters.longitude = myLocation.longitude;
        filters.radiusKm = radiusKm;
      }
      const data = await searchApi.searchPlayers(filters);
      setResults(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-wide">
      <h1>Find Players</h1>

      <form onSubmit={handleSearch} className="search-filters">
        <label>
          🏏 Sport
          <select value={sportId} onChange={(e) => setSportId(e.target.value)}>
            <option value="">Any sport</option>
            {sports.map((s) => (
              <option key={s.id} value={s.id}>{s.name}</option>
            ))}
          </select>
        </label>

        <label>
          ⭐ Skill
          <select value={skillLevel} onChange={(e) => setSkillLevel(e.target.value)}>
            <option value="">Any skill level</option>
            {SKILL_LEVELS.map((level) => (
              <option key={level} value={level}>{level}</option>
            ))}
          </select>
        </label>

        <label>
          📍 Distance
          <select
            value={radiusKm}
            onChange={(e) => setRadiusKm(e.target.value)}
            disabled={!myLocation?.latitude}
          >
            <option value="5">Within 5 km</option>
            <option value="10">Within 10 km</option>
            <option value="25">Within 25 km</option>
            <option value="50">Within 50 km</option>
          </select>
          {!myLocation?.latitude && (
            <span className="filter-hint">Set your location in Profile to filter by distance</span>
          )}
        </label>

        <label>
          📅 Availability
          <select disabled>
            <option>Any time</option>
          </select>
          <span className="filter-hint">Coming soon — needs match scheduling data</span>
        </label>

        <button type="submit" disabled={loading}>
          {loading ? 'Searching...' : 'Search'}
        </button>
      </form>

      {error && <p className="form-error">{error}</p>}

      {hasSearched && !loading && (
        <div className="search-results">
          {results.length === 0 ? (
            <p className="empty-state">No players match those filters yet.</p>
          ) : (
            <ul>
              {results.map((p) => (
                <li key={`${p.userId}-${p.sportName}`} className="search-result-item">
                  <strong>{p.name}</strong> — {p.sportName} ({p.skillLevel})
                  {p.distanceKm != null && <span> · {p.distanceKm} km away</span>}
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

export default PlayerSearch;
