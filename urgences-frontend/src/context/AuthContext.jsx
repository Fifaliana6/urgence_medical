import { createContext, useContext, useState, useCallback } from 'react';
import { loginRequest } from '../api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('urgences_token'));
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('urgences_user');
    return stored ? JSON.parse(stored) : null;
  });

  const login = useCallback(async (email, password) => {
    const data = await loginRequest(email, password);
    const userData = { userId: data.userId, nom: data.nom, role: data.role };
    localStorage.setItem('urgences_token', data.token);
    localStorage.setItem('urgences_user', JSON.stringify(userData));
    setToken(data.token);
    setUser(userData);
    return userData;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('urgences_token');
    localStorage.removeItem('urgences_user');
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}