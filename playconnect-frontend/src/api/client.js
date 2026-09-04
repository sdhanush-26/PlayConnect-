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
