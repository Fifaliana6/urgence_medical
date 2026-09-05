export default function Badge({ meta, fallbackLabel, withDot = false }) {
  if (!meta) {
    return (
      <span className="inline-flex items-center rounded-md bg-ink-soft/10 px-2 py-0.5 text-xs font-medium text-ink-soft">
        {fallbackLabel ?? '—'}
      </span>
    )
  }
  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-md px-2 py-0.5 text-xs font-medium ${meta.bg} ${meta.fg}`}
    >
      {withDot && <span className={`h-1.5 w-1.5 rounded-full ${meta.dot ?? ''}`} />}
      {meta.label}
    </span>
  )
}
