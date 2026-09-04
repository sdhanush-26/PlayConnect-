import { useState } from 'react';

function Register() {
  const [form, setForm] = useState({ name: '', email: '', password: '' });

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function handleSubmit(e) {
    e.preventDefault();
    // Real API call to POST /api/auth/register arrives Day 47.
    console.log('Register attempt:', form);
  }

  return (
    <div className="page">
      <h1>Register</h1>
      <form onSubmit={handleSubmit} className="auth-form">
        <input name="name" placeholder="Name" value={form.name} onChange={handleChange} />
        <input name="email" placeholder="Email" value={form.email} onChange={handleChange} />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
        />
        <button type="submit">Create Account</button>
      </form>
    </div>
  );
}

export default Register;
