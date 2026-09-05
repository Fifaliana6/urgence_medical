import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Activity, LogIn } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { Button, Input, ErrorBanner } from '../components/Primitives'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await login(username, password)
      navigate('/')
    } catch {
      setError('Identifiants incorrects. Vérifiez votre nom d\u2019utilisateur et mot de passe.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-ink px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex flex-col items-center gap-2 text-white">
          <div className="rounded-lg bg-white/10 p-3">
            <Activity className="h-6 w-6 text-accent" strokeWidth={2.5} />
          </div>
          <h1 className="text-lg font-semibold">Urgences · Hôpital</h1>
          <p className="text-sm text-white/50">Espace personnel soignant</p>
        </div>

        <form onSubmit={handleSubmit} className="rounded-lg border border-white/10 bg-surface p-6 shadow-xl">
          <ErrorBanner message={error} />
          <div className="space-y-4">
            <Input
              label="Nom d'utilisateur"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoFocus
              required
            />
            <Input
              label="Mot de passe"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <Button type="submit" className="mt-6 w-full" disabled={loading}>
            <LogIn className="h-4 w-4" />
            {loading ? 'Connexion…' : 'Se connecter'}
          </Button>
          <p className="mt-4 text-center text-xs text-ink-faint">
            Compte créé au premier démarrage du backend : admin / admin123
          </p>
        </form>
      </div>
    </div>
  )
}
