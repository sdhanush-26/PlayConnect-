import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../api/AuthContext';
import { matchesApi } from '../api/client';

function MatchDetails() {
  const { matchId } = useParams();
  const { user } = useAuth();

  const [match, setMatch] = useState(null);
  const [players, setPlayers] = useState([]);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [loading, setLoading] = useState(true);

  async function loadMatch() {
    try {
      const [matchData, playersData] = await Promise.all([
        matchesApi.getById(matchId),
        matchesApi.getPlayers(matchId),
      ]);
      setMatch(matchData);
      setPlayers(playersData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadMatch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [matchId]);

  const alreadyJoined = players.some((p) => p.userId === user?.userId);

  async function handleJoin() {
    setActionLoading(true);
    setError('');
    try {
      await matchesApi.join(matchId, user.userId);
      await loadMatch(); // re-fetch so the roster and status reflect the join immediately
    } catch (err) {
      setError(err.message);
    } finally {
      setActionLoading(false);
    }
  }

  async function handleLeave() {
    setActionLoading(true);
    setError('');
    try {
      await matchesApi.leave(matchId, user.userId);
      await loadMatch();
    } catch (err) {
      setError(err.message);
    } finally {
      setActionLoading(false);
    }
  }

  if (loading) return <div className="page"><p>Loading match...</p></div>;
  if (!match) return <div className="page"><p className="form-error">{error || 'Match not found'}</p></div>;

  return (
    <div className="page-wide">
      <h1>{match.title}</h1>
      <div className="match-details-card">
        <p><strong>Sport:</strong> {match.sportName}</p>
        <p><strong>Created by:</strong> {match.creatorName}</p>
        <p><strong>Location:</strong> {match.location || 'Not specified'}</p>
        <p><strong>Date:</strong> {match.matchDate}</p>
        <p><strong>Time:</strong> {match.startTime} – {match.endTime}</p>
        <p><strong>Players:</strong> {players.length} / {match.maxPlayers}</p>
        <p><strong>Status:</strong> <span className={`status-badge status-${match.status.toLowerCase()}`}>{match.status}</span></p>

        {error && <p className="form-error">{error}</p>}

        {match.creatorId !== user?.userId && (
          alreadyJoined ? (
            <button onClick={handleLeave} disabled={actionLoading} className="btn-leave">
              {actionLoading ? 'Leaving...' : 'Leave Match'}
            </button>
          ) : (
            <button onClick={handleJoin} disabled={actionLoading || match.status !== 'OPEN'} className="btn-join">
              {actionLoading ? 'Joining...' : match.status === 'OPEN' ? 'Join Match' : `Cannot join (${match.status})`}
            </button>
          )
        )}
      </div>

      <h2>Roster</h2>
      {players.length === 0 ? (
        <p className="empty-state">No one has joined yet.</p>
      ) : (
        <ul className="roster-list">
          {players.map((p) => (
            <li key={p.id}>
              {p.userName} — <span className={`status-badge status-${p.joinStatus.toLowerCase()}`}>{p.joinStatus}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default MatchDetails;
