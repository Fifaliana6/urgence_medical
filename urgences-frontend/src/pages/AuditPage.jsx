import { useEffect, useState, useCallback } from 'react'
import { History } from 'lucide-react'
import api from '../api/client'
import { Card, PageHeader, Spinner, EmptyState, ErrorBanner, Select } from '../components/Primitives'
import { formatDateTime } from '../lib/constants'

const ENTITIES = ['EmergencyVisit', 'Consultation']

export default function AuditPage() {
  const [logs, setLogs] = useState(null)
  const [entite, setEntite] = useState('')
  const [error, setError] = useState(null)

  const fetchLogs = useCallback(async (filter) => {
    try {
      const { data } = await api.get(filter ? `/audit/entite/${filter}` : '/audit')
      setLogs(data)
    } catch {
      setError("Impossible de charger le journal d'audit.")
    }
  }, [])

  useEffect(() => { fetchLogs(entite) }, [entite, fetchLogs])

  return (
    <div>
      <PageHeader
        title="Audit"
        description="Traçabilité des actions effectuées sur le dossier des patients."
        actions={
          <Select value={entite} onChange={(e) => setEntite(e.target.value)}>
            <option value="">Toutes les entités</option>
            {ENTITIES.map((e) => <option key={e} value={e}>{e}</option>)}
          </Select>
        }
      />

      <ErrorBanner message={error} />

      {logs === null ? (
        <Spinner />
      ) : logs.length === 0 ? (
        <Card><EmptyState icon={History} title="Aucune entrée d'audit" /></Card>
      ) : (
        <Card className="overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border bg-bg/60 text-xs uppercase tracking-wide text-ink-soft">
              <tr>
                <th className="px-4 py-3 font-medium">Date / heure</th>
                <th className="px-4 py-3 font-medium">Utilisateur</th>
                <th className="px-4 py-3 font-medium">Action</th>
                <th className="px-4 py-3 font-medium">Entité</th>
                <th className="px-4 py-3 font-medium">Détails</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {logs
                .slice()
                .sort((a, b) => new Date(b.dateHeure) - new Date(a.dateHeure))
                .map((log) => (
                  <tr key={log.id} className="hover:bg-bg/60">
                    <td className="whitespace-nowrap px-4 py-3 font-mono text-xs text-ink-soft">
                      {formatDateTime(log.dateHeure)}
                    </td>
                    <td className="px-4 py-3 text-ink">{log.utilisateur}</td>
                    <td className="px-4 py-3 text-ink-soft">{log.action}</td>
                    <td className="px-4 py-3 text-ink-soft">
                      {log.entite} #{log.entiteId}
                    </td>
                    <td className="px-4 py-3 text-ink-soft">{log.details}</td>
                  </tr>
                ))}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  )
}
