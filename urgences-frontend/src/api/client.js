import axios from 'axios'

const AUTH_STORAGE_KEY = 'urgences_auth'

export function getStoredAuth() {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY)
  return raw ? JSON.parse(raw) : null
}

export function storeAuth(username, password) {
  const value = { username, token: btoa(`${username}:${password}`) }
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(value))
  return value
}

export function clearAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY)
}

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
  const auth = getStoredAuth()
  if (auth?.token) {
    config.headers.Authorization = `Basic ${auth.token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearAuth()
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default api
