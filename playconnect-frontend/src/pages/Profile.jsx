import { useState, useEffect } from 'react';
import { useAuth } from '../api/AuthContext';
import { profileApi, sportsApi, playerSportsApi } from '../api/client';

const SKILL_LEVELS = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'PRO'];

function Profile() {
  const { user } = useAuth();

  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [editing, setEditing] = useState(false);
  const [editForm, setEditForm] = useState(null);
  const [saving, setSaving] = useState(false);

  const [allSports, setAllSports] = useState([]);
  const [newSportId, setNewSportId] = useState('');
  const [newSkillLevel, setNewSkillLevel] = useState('BEGINNER');
  const [addingSport, setAddingSport] = useState(false);
  const [sportError, setSportError] = useState('');

  async function loadProfile() {
    try {
      const data = await profileApi.get(user.userId);
      setProfile(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (user) loadProfile();
    sportsApi.getAll().then(setAllSports).catch(() => {});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user]);

  function startEditing() {
    setEditForm({
      name: profile.name,
      email: profile.email,
      phone: profile.phone || '',
      latitude: profile.latitude ?? '',
      longitude: profile.longitude ?? '',
    });
    setEditing(true);
  }

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      // Backend's UserRequest requires a password field even though
      // profile edits never actually change it (Day 11's updateUser
      // deliberately ignores password/email) — sending a placeholder
      // satisfies validation without any real effect.
      await profileApi.update(user.userId, {
        name: editForm.name,
        email: editForm.email,
        password: 'unchanged',
        phone: editForm.phone,
        latitude: editForm.latitude === '' ? null : Number(editForm.latitude),
        longitude: editForm.longitude === '' ? null : Number(editForm.longitude),
      });
      await loadProfile();
      setEditing(false);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  async function handleAddSport(e) {
    e.preventDefault();
    setSportError('');
    setAddingSport(true);
    try {
      await playerSportsApi.add(user.userId, Number(newSportId), newSkillLevel);
      await loadProfile();
      setNewSportId('');
    } catch (err) {
      setSportError(err.message);
    } finally {
      setAddingSport(false);
    }
  }

  if (loading) return <div className="page"><p>Loading profile...</p></div>;
  if (!profile) return <div className="page"><p className="form-error">{error}</p></div>;

  const availableSports = allSports.filter(
    (s) => !profile.sports.some((ps) => ps.sportId === s.id)
  );

  return (
    <div className="page-wide">
      <h1>Your Profile</h1>

      {!editing ? (
        <div className="profile-card">
          <p><strong>Name:</strong> {profile.name}</p>
          <p><strong>Email:</strong> {profile.email}</p>
          <p><strong>Phone:</strong> {profile.phone || 'Not set'}</p>
          <p><strong>Location:</strong> {profile.latitude != null ? `${profile.latitude}, ${profile.longitude}` : 'Not set'}</p>
          <p><strong>Matches played:</strong> {profile.matchesPlayed}</p>
          <p><strong>Average rating:</strong> {profile.averageRating ?? 'No ratings yet'}</p>
          <button onClick={startEditing}>Edit Profile</button>
        </div>
      ) : (
        <form onSubmit={handleSave} className="match-form profile-edit-form">
          <label>
            Name
            <input value={editForm.name} onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} />
          </label>
          <label>
            Phone
            <input value={editForm.phone} onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })} />
          </label>
          <div className="match-form-row">
            <label>
              Latitude
              <input type="number" step="any" value={editForm.latitude} onChange={(e) => setEditForm({ ...editForm, latitude: e.target.value })} />
            </label>
            <label>
              Longitude
              <input type="number" step="any" value={editForm.longitude} onChange={(e) => setEditForm({ ...editForm, longitude: e.target.value })} />
            </label>
          </div>
          {error && <p className="form-error">{error}</p>}
          <div className="match-form-row">
            <button type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save'}</button>
            <button type="button" onClick={() => setEditing(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      <h2>Your Sports</h2>
      {profile.sports.length === 0 ? (
        <p className="empty-state">You haven't added any sports yet.</p>
      ) : (
        <ul className="roster-list">
          {profile.sports.map((s) => (
            <li key={s.id}>{s.sportName} — <span className="status-badge status-open">{s.skillLevel}</span></li>
          ))}
        </ul>
      )}

      {availableSports.length > 0 && (
        <form onSubmit={handleAddSport} className="add-sport-form">
          <select value={newSportId} onChange={(e) => setNewSportId(e.target.value)} required>
            <option value="">Add a sport...</option>
            {availableSports.map((s) => (
              <option key={s.id} value={s.id}>{s.name}</option>
            ))}
          </select>
          <select value={newSkillLevel} onChange={(e) => setNewSkillLevel(e.target.value)}>
            {SKILL_LEVELS.map((level) => (
              <option key={level} value={level}>{level}</option>
            ))}
          </select>
          <button type="submit" disabled={addingSport || !newSportId}>
            {addingSport ? 'Adding...' : 'Add'}
          </button>
        </form>
      )}
      {sportError && <p className="form-error">{sportError}</p>}
    </div>
  );
}

export default Profile;