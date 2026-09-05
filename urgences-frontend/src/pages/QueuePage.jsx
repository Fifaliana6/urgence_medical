import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { ClipboardList, ArrowRight, RefreshCw } from 'lucide-react'
import api from '../api/client'
import { Card, PageHeader, Spinner, EmptyState, ErrorBanner, Button } from '../components/Primitives'
import Badge from '../components/Badge'
import { URGENCY_META, formatDateTime } from '../lib/constants'

export default function QueuePage() {
  const navigate = useNavigate()
  const [visites, setVisites] = useState(null)
  const [error, setError] = useState(null)

  const fetchQueue = useCallback(async () => {
    try {
      const { data } = await api.get('/visits')
      setVisites(data)
      setError(null)
    } catch {
      setError("Impossible de charger la file d'attente.")
    }
  }, [])

  useEffect(() => {
    fetchQueue()
    const interval = setInterval(fetchQueue, 5000)
    return () => clearInterval(interval)
  }, [fetchQueue])

  return (
    <div>
      <PageHeader
        title="File d'attente"
        description="Patients en attente de prise en charge, triés par niveau d'urgence."
        actions={
          <Button variant="secondary" onClick={fetchQueue}>
            <RefreshCw className="h-4 w-4" />
            Actualiser
          </Button>
        }
      />

      <ErrorBanner message={error} />

      {visites === null ? (
        <Spinner />
      ) : visites.length === 0 ? (
        <Card>
          <EmptyState
            icon={ClipboardList}
            title="Aucun patient en attente"
            description="Les nouvelles admissions apparaîtront ici dès leur enregistrement au triage."
          />
        </Card>
      ) : (
        <Card className="overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border bg-bg/60 text-xs uppercase tracking-wide text-ink-soft">
              <tr>
                <th className="px-4 py-3 font-medium">Urgence</th>
                <th className="px-4 py-3 font-medium">Patient</th>
                <th className="px-4 py-3 font-medium">Symptômes</th>
                <th className="px-4 py-3 font-medium">Arrivée</th>
                <th className="px-4 py-3 font-medium">Médecin</th>
                <th className="px-4 py-3" />
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {visites.map((v) => (
                <tr
                  key={v.id}
                  onClick={() => navigate(`/visits/${v.id}`)}
                  className="cursor-pointer hover:bg-bg/60"
                >
                  <td className="px-4 py-3">
                    <Badge meta={URGENCY_META[v.niveauUrgence]} withDot />
                  </td>
                  <td className="px-4 py-3 font-medium text-ink">
                    {v.patient?.nom} {v.patient?.prenom}
                  </td>
                  <td className="max-w-xs truncate px-4 py-3 text-ink-soft">
                    {v.symptomes || '—'}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3 font-mono text-xs text-ink-soft">
                    {formatDateTime(v.dateArrivee)}
                  </td>
                  <td className="px-4 py-3 text-ink-soft">
                    {v.medecin ? `Dr ${v.medecin.nom}` : 'Non assigné'}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <ArrowRight className="ml-auto h-4 w-4 text-ink-faint" />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  )
}
