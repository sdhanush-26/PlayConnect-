import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../api/AuthContext';
import { sportsApi, matchesApi } from '../api/client';

function CreateMatch() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [sports, setSports] = useState([]);
  const [form, setForm] = useState({
    title: '',
    sportId: '',
    location: '',
    matchDate: '',
    startTime: '',
    endTime: '',
    maxPlayers: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    sportsApi.getAll().then(setSports).catch(() => {});
  }, []);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      // Backend (Day 23) expects HH:mm:ss but <input type="time"> only
      // gives HH:mm — append seconds so the payload matches what
      // MatchRequest's LocalTime parsing expects.
      const created = await matchesApi.create({
        title: form.title,
        creatorId: user.userId,
        sportId: Number(form.sportId),
        location: form.location,
        matchDate: form.matchDate,
        startTime: `${form.startTime}:00`,
        endTime: `${form.endTime}:00`,
        maxPlayers: Number(form.maxPlayers),
      });
      navigate(`/matches/${created.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-wide">
      <h1>Create a Match</h1>
      <form onSubmit={handleSubmit} className="match-form">
        <label>
          Title
          <input name="title" value={form.title} onChange={handleChange} placeholder="Sunday Cricket" />
        </label>

        <label>
          Sport
          <select name="sportId" value={form.sportId} onChange={handleChange}>
            <option value="">Select a sport</option>
            {sports.map((s) => (
              <option key={s.id} value={s.id}>{s.name}</option>
            ))}
          </select>
        </label>

        <label>
          Location
          <input name="location" value={form.location} onChange={handleChange} placeholder="City Ground, Anantapur" />
        </label>

        <label>
          Date
          <input type="date" name="matchDate" value={form.matchDate} onChange={handleChange} />
        </label>

        <div className="match-form-row">
          <label>
            Start time
            <input type="time" name="startTime" value={form.startTime} onChange={handleChange} />
          </label>
          <label>
            End time
            <input type="time" name="endTime" value={form.endTime} onChange={handleChange} />
          </label>
        </div>

        <label>
          Max players
          <input type="number" name="maxPlayers" min="2" value={form.maxPlayers} onChange={handleChange} />
        </label>

        {error && <p className="form-error">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? 'Creating...' : 'Create Match'}
        </button>
      </form>
    </div>
  );
}

export default CreateMatch;
