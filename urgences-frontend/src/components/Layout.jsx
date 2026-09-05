import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import {
  Activity,
  ClipboardList,
  UserPlus,
  Users,
  Building2,
  Receipt,
  History,
  LogOut,
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'

const NAV_ITEMS = [
  { to: '/', label: "File d'attente", icon: ClipboardList, end: true },
  { to: '/triage', label: 'Triage', icon: UserPlus },
  { to: '/patients', label: 'Patients', icon: Users },
  { to: '/resources', label: 'Ressources', icon: Building2 },
  { to: '/invoices', label: 'Facturation', icon: Receipt },
  { to: '/audit', label: 'Audit', icon: History },
]

export default function Layout() {
  const { auth, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="flex min-h-screen">
      <aside className="flex w-64 shrink-0 flex-col bg-ink text-white/90">
        <div className="flex items-center gap-2 px-5 py-5">
          <Activity className="h-5 w-5 text-accent" strokeWidth={2.5} />
          <span className="text-sm font-semibold tracking-tight text-white">
            Urgences · Hôpital
          </span>
        </div>

        <nav className="flex-1 space-y-0.5 px-3 py-2">
          {NAV_ITEMS.map(({ to, label, icon: Icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-md px-3 py-2 text-sm transition-colors ${
                  isActive
                    ? 'bg-white/10 text-white font-medium'
                    : 'text-white/60 hover:bg-white/5 hover:text-white'
                }`
              }
            >
              <Icon className="h-4 w-4" strokeWidth={2} />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="border-t border-white/10 px-3 py-3">
          <div className="flex items-center justify-between rounded-md px-2 py-2">
            <div className="min-w-0">
              <p className="truncate text-sm font-medium text-white">{auth?.username}</p>
              <p className="text-xs text-white/50">Connecté</p>
            </div>
            <button
              onClick={handleLogout}
              aria-label="Se déconnecter"
              title="Se déconnecter"
              className="rounded-md p-2 text-white/60 hover:bg-white/10 hover:text-white"
            >
              <LogOut className="h-4 w-4" />
            </button>
          </div>
        </div>
      </aside>

      <main className="min-w-0 flex-1 overflow-y-auto px-8 py-8">
        <Outlet />
      </main>
    </div>
  )
}
