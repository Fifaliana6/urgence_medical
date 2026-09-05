import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search, Users, Plus } from 'lucide-react'
import api from '../api/client'
import { Card, PageHeader, Spinner, EmptyState, ErrorBanner, Button, Input } from '../components/Primitives'

export default function PatientsPage() {
  const navigate = useNavigate()
  const [patients, setPatients] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [error, setError] = useState(null)

  const fetchAll = useCallback(async () => {
    try {
      const { data } = await api.get('/patients')
      setPatients(data)
    } catch {
      setError('Impossible de charger la liste des patients.')
    }
  }, [])

  useEffect(() => {
    fetchAll()
  }, [fetchAll])

  async function handleSearch(e) {
    e.preventDefault()
    if (!searchTerm.trim()) return fetchAll()
    try {
      const { data } = await api.get('/patients/search', { params: { nom: searchTerm } })
      setPatients(data)
    } catch {
      setError('La recherche a échoué.')
    }
  }

  return (
    <div>
      <PageHeader
        title="Patients"
        description="Dossiers administratifs des patients connus de l'établissement."
      />

      <ErrorBanner message={error} />

      <form onSubmit={handleSearch} className="mb-4 flex gap-2">
        <div className="max-w-sm flex-1">
          <Input
            placeholder="Rechercher par nom…"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <Button type="submit" variant="secondary">
          <Search className="h-4 w-4" />
          Rechercher
        </Button>
      </form>

      {patients === null ? (
        <Spinner />
      ) : patients.length === 0 ? (
        <Card>
          <EmptyState icon={Users} title="Aucun patient trouvé" />
        </Card>
      ) : (
        <Card className="overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border bg-bg/60 text-xs uppercase tracking-wide text-ink-soft">
              <tr>
                <th className="px-4 py-3 font-medium">Nom</th>
                <th className="px-4 py-3 font-medium">Téléphone</th>
                <th className="px-4 py-3 font-medium">N° sécu. sociale</th>
                <th className="px-4 py-3" />
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {patients.map((p) => (
                <tr key={p.id} className="hover:bg-bg/60">
                  <td className="px-4 py-3 font-medium text-ink">
                    {p.nom} {p.prenom}
                  </td>
                  <td className="px-4 py-3 text-ink-soft">{p.telephone || '—'}</td>
                  <td className="px-4 py-3 font-mono text-xs text-ink-soft">
                    {p.numeroSecuriteSociale || '—'}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <Button
                      variant="ghost"
                      onClick={() => navigate('/triage', { state: { patient: p } })}
                    >
                      <Plus className="h-4 w-4" />
                      Nouvelle visite
                    </Button>
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
