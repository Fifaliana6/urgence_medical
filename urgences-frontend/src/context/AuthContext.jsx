import { createContext, useContext, useState, useCallback } from 'react'
import axios from 'axios'
import { getStoredAuth, storeAuth, clearAuth } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => getStoredAuth())

  const login = useCallback(async (username, password) => {
    const token = btoa(`${username}:${password}`)
    // Validate the credentials directly against a lightweight authenticated
    // endpoint before persisting them -- this is what turns a wrong password
    // into a clear "Identifiants incorrects" instead of a silent bad session.
    await axios.get('/api/doctors', {
      headers: { Authorization: `Basic ${token}` },
    })
    const stored = storeAuth(username, password)
    setAuth(stored)
  }, [])

  const logout = useCallback(() => {
    clearAuth()
    setAuth(null)
  }, [])

  return (
    <AuthContext.Provider value={{ auth, isAuthenticated: !!auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
