import { createContext, useContext, useState, useEffect } from 'react';

// React Context solves the "prop drilling" problem — without it, login
// state would need to be manually passed down through App -> Layout ->
// Navbar/Sidebar/every page as props. Context lets any component
// anywhere in the tree read/update auth state directly via useAuth().
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  // On first load, check if a previous login left a token/user behind
  // in localStorage — this is what keeps someone logged in after a
  // page refresh, rather than losing their session every reload.
  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  function login(loginResponse) {
    localStorage.setItem('token', loginResponse.token);
    localStorage.setItem('user', JSON.stringify({
      userId: loginResponse.userId,
      name: loginResponse.name,
      email: loginResponse.email,
    }));
    setUser({
      userId: loginResponse.userId,
      name: loginResponse.name,
      email: loginResponse.email,
    });
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, isLoggedIn: !!user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook — lets any component just call useAuth() instead of
// importing useContext + AuthContext separately every time.
export function useAuth() {
  return useContext(AuthContext);
}
