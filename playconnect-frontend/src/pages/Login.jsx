import { useState } from 'react';

// Demonstrates STATE + HOOKS: useState gives this component its own
// local memory (email, password) that persists between re-renders and
// triggers a re-render whenever it changes — that's what makes typing
// into these inputs actually show up on screen.
function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  function handleSubmit(e) {
    e.preventDefault();
    // Real API call to POST /api/auth/login arrives Day 47.
    console.log('Login attempt:', { email, password });
  }

  return (
    <div className="page">
      <h1>Login</h1>
      <form onSubmit={handleSubmit} className="auth-form">
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit">Log In</button>
      </form>
    </div>
  );
}

export default Login;
