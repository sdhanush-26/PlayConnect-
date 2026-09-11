const BASE_URL = 'http://localhost:8080/api';

// Small wrapper so every call doesn't repeat the same fetch boilerplate.
// Attaches the stored JWT automatically when present, so callers never
// have to think about auth headers themselves.
async function request(path, options = {}) {
  const token = localStorage.getItem('token');

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    // Backend's GlobalExceptionHandler (Day 14) always includes a
    // "message" field — surface that directly so error messages
    // shown to the user match exactly what the API actually says.
    const message = data?.message || data?.error || 'Something went wrong';
    throw new Error(message);
  }

  return data;
}

export const authApi = {
  register: (payload) =>
    request('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),

  login: (payload) =>
    request('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),
};

export const sportsApi = {
  getAll: () => request('/sports'),
};

export const searchApi = {
  // Mirrors backend's optional-filter design (Day 20) — only params
  // that are actually set get included in the query string, so an
  // empty filter still returns everyone rather than an empty result.
  searchPlayers: ({ sportId, skillLevel, latitude, longitude, radiusKm }) => {
    const params = new URLSearchParams();
    if (sportId) params.set('sportId', sportId);
    if (skillLevel) params.set('skillLevel', skillLevel);
    if (latitude != null) params.set('latitude', latitude);
    if (longitude != null) params.set('longitude', longitude);
    if (radiusKm) params.set('radiusKm', radiusKm);
    return request(`/players?${params.toString()}`);
  },
};

export const profileApi = {
  get: (userId) => request(`/profile/${userId}`),
};

export const matchesApi = {
  // "Nearby matches within 25km" — matches the plan's default radius options.
  getNearby: (latitude, longitude, radiusKm = 25) =>
    request(`/matches/nearby?latitude=${latitude}&longitude=${longitude}&radiusKm=${radiusKm}`),

  getUpcomingForUser: (userId) =>
    request(`/matches/user/${userId}/upcoming`),

  getCompletedForUser: (userId) =>
    request(`/matches/user/${userId}/completed`),

  getCancelledForUser: (userId) =>
    request(`/matches/user/${userId}/cancelled`),

  getById: (matchId) =>
    request(`/matches/${matchId}`),

  create: (payload) =>
    request('/matches', { method: 'POST', body: JSON.stringify(payload) }),

  join: (matchId, userId) =>
    request(`/matches/${matchId}/join?userId=${userId}`, { method: 'POST' }),

  leave: (matchId, userId) =>
    request(`/matches/${matchId}/leave?userId=${userId}`, { method: 'DELETE' }),

  getPlayers: (matchId) =>
    request(`/matches/${matchId}/players`),
};

export const playersApi = {
  // Uses the logged-in user's own saved profile location (Day 35) —
  // no need to ask the browser for GPS permission just for the dashboard.
  getNearbyForMe: (userId, radiusKm = 25) =>
    request(`/players/nearby/me?userId=${userId}&radiusKm=${radiusKm}`),
};