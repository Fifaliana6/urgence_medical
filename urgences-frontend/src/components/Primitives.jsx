import { Loader2, Inbox } from 'lucide-react'

export function Card({ children, className = '' }) {
  return (
    <div className={`rounded-lg border border-border bg-surface ${className}`}>{children}</div>
  )
}

export function PageHeader({ title, description, actions }) {
  return (
    <div className="mb-6 flex flex-wrap items-start justify-between gap-4">
      <div>
        <h1 className="text-xl font-semibold text-ink">{title}</h1>
        {description && <p className="mt-1 text-sm text-ink-soft">{description}</p>}
      </div>
      {actions && <div className="flex items-center gap-2">{actions}</div>}
    </div>
  )
}

export function Spinner({ label = 'Chargement…' }) {
  return (
    <div className="flex items-center justify-center gap-2 py-16 text-sm text-ink-soft">
      <Loader2 className="h-4 w-4 animate-spin" />
      {label}
    </div>
  )
}

export function EmptyState({ title, description, icon: Icon = Inbox, action }) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 py-16 text-center">
      <Icon className="h-8 w-8 text-ink-faint" strokeWidth={1.5} />
      <p className="text-sm font-medium text-ink">{title}</p>
      {description && <p className="max-w-sm text-sm text-ink-soft">{description}</p>}
      {action}
    </div>
  )
}

export function ErrorBanner({ message }) {
  if (!message) return null
  return (
    <div className="mb-4 rounded-md border border-critique/20 bg-critique-soft px-3 py-2 text-sm text-critique">
      {message}
    </div>
  )
}

export function Button({ variant = 'primary', className = '', ...props }) {
  const base =
    'inline-flex items-center justify-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50'
  const variants = {
    primary: 'bg-accent text-white hover:bg-accent-dark',
    secondary: 'bg-white border border-border text-ink hover:bg-bg',
    danger: 'bg-critique text-white hover:bg-critique/90',
    ghost: 'text-ink-soft hover:bg-ink-soft/10',
  }
  return <button className={`${base} ${variants[variant]} ${className}`} {...props} />
}

export function Input({ label, error, className = '', ...props }) {
  return (
    <label className="block">
      {label && <span className="mb-1 block text-sm font-medium text-ink">{label}</span>}
      <input
        className={`w-full rounded-md border px-3 py-2 text-sm text-ink placeholder:text-ink-faint focus:border-accent ${
          error ? 'border-critique' : 'border-border'
        } ${className}`}
        {...props}
      />
      {error && <span className="mt-1 block text-xs text-critique">{error}</span>}
    </label>
  )
}

export function Select({ label, className = '', children, ...props }) {
  return (
    <label className="block">
      {label && <span className="mb-1 block text-sm font-medium text-ink">{label}</span>}
      <select
        className={`w-full rounded-md border border-border bg-white px-3 py-2 text-sm text-ink focus:border-accent ${className}`}
        {...props}
      >
        {children}
      </select>
    </label>
  )
}

export function Textarea({ label, className = '', ...props }) {
  return (
    <label className="block">
      {label && <span className="mb-1 block text-sm font-medium text-ink">{label}</span>}
      <textarea
        className={`w-full rounded-md border border-border px-3 py-2 text-sm text-ink placeholder:text-ink-faint focus:border-accent ${className}`}
        {...props}
      />
    </label>
  )
}
