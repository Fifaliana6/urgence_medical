import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Search, UserPlus, Check, X, Siren } from 'lucide-react'
import api from '../api/client'
import {
  Card,
  PageHeader,
  Button,
  Input,
  Select,
  Textarea,
  ErrorBanner,
} from '../components/Primitives'
import { URGENCY_LEVELS, URGENCY_META } from '../lib/constants'

const emptyPatient = {
  nom: '',
  prenom: '',
  dateNaissance: '',
  sexe: '',
  telephone: '',
  adresse: '',
  numeroSecuriteSociale: '',
}

export default function TriagePage() {
  const navigate = useNavigate()
  const location = useLocation()

  const [searchTerm, setSearchTerm] = useState('')
  const [results, setResults] = useState([])
  const [searching, setSearching] = useState(false)

  // Arriving from the Patients page with a pre-selected patient skips the search step.
  const [selectedPatient, setSelectedPatient] = useState(location.state?.patient ?? null)
  const [showNewPatientForm, setShowNewPatientForm] = useState(false)
  const [newPatient, setNewPatient] = useState(emptyPatient)

  const [symptomes, setSymptomes] = useState('')
  const [niveauUrgence, setNiveauUrgence] = useState('MOYENNE')

  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSearch(e) {
    e.preventDefault()
    if (!searchTerm.trim()) return
    setSearching(true)
    setError(null)
    try {
      const { data } = await api.get('/patients/search', { params: { nom: searchTerm } })
      setResults(data)
    } catch {
      setError('La recherche de patient a échoué.')
    } finally {
      setSearching(false)
    }
  }

  async function handleCreatePatient(e) {
    e.preventDefault()
    setError(null)
    try {
      const payload = { ...newPatient, dateNaissance: newPatient.dateNaissance || null }
      const { data } = await api.post('/patients', payload)
      setSelectedPatient(data)
      setShowNewPatientForm(false)
    } catch {
      setError("La création du patient a échoué. Vérifiez que le nom et prénom sont renseignés.")
    }
  }

  async function handleCreateVisit(e) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      const { data } = await api.post('/visits', {
        patientId: selectedPatient.id,
        symptomes,
        niveauUrgence,
      })
      navigate(`/visits/${data.id}`)
    } catch {
      setError("L'enregistrement de l'admission a échoué.")
      setSubmitting(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl">
      <PageHeader
        title="Accueil et triage"
        description="Identifiez le patient puis renseignez son motif d'admission et son niveau d'urgence."
      />

      <ErrorBanner message={error} />

      {!selectedPatient ? (
        <Card className="p-5">
          <form onSubmit={handleSearch} className="flex gap-2">
            <div className="flex-1">
              <Input
                placeholder="Rechercher un patient par nom…"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <Button type="submit" variant="secondary" disabled={searching}>
              <Search className="h-4 w-4" />
              Rechercher
            </Button>
          </form>

          {results.length > 0 && (
            <ul className="mt-4 divide-y divide-border border-t border-border">
              {results.map((p) => (
                <li
                  key={p.id}
                  onClick={() => setSelectedPatient(p)}
                  className="flex cursor-pointer items-center justify-between px-1 py-3 hover:bg-bg/60"
                >
                  <div>
                    <p className="text-sm font-medium text-ink">
                      {p.nom} {p.prenom}
                    </p>
                    <p className="text-xs text-ink-soft">{p.telephone || 'Téléphone non renseigné'}</p>
                  </div>
                  <Check className="h-4 w-4 text-ink-faint" />
                </li>
              ))}
            </ul>
          )}

          <div className="mt-5 border-t border-border pt-4">
            {!showNewPatientForm ? (
              <Button variant="ghost" onClick={() => setShowNewPatientForm(true)}>
                <UserPlus className="h-4 w-4" />
                Ce patient n'existe pas encore — créer une fiche
              </Button>
            ) : (
              <form onSubmit={handleCreatePatient} className="space-y-3">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-medium text-ink">Nouveau patient</p>
                  <button
                    type="button"
                    onClick={() => setShowNewPatientForm(false)}
                    className="text-ink-soft hover:text-ink"
                  >
                    <X className="h-4 w-4" />
                  </button>
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <Input
                    label="Nom"
                    required
                    value={newPatient.nom}
                    onChange={(e) => setNewPatient({ ...newPatient, nom: e.target.value })}
                  />
                  <Input
                    label="Prénom"
                    required
                    value={newPatient.prenom}
                    onChange={(e) => setNewPatient({ ...newPatient, prenom: e.target.value })}
                  />
                  <Input
                    label="Date de naissance"
                    type="date"
                    value={newPatient.dateNaissance}
                    onChange={(e) => setNewPatient({ ...newPatient, dateNaissance: e.target.value })}
                  />
                  <Select
                    label="Sexe"
                    value={newPatient.sexe}
                    onChange={(e) => setNewPatient({ ...newPatient, sexe: e.target.value })}
                  >
                    <option value="">—</option>
                    <option value="F">F</option>
                    <option value="M">M</option>
                  </Select>
                  <Input
                    label="Téléphone"
                    value={newPatient.telephone}
                    onChange={(e) => setNewPatient({ ...newPatient, telephone: e.target.value })}
                  />
                  <Input
                    label="N° sécurité sociale"
                    value={newPatient.numeroSecuriteSociale}
                    onChange={(e) =>
                      setNewPatient({ ...newPatient, numeroSecuriteSociale: e.target.value })
                    }
                  />
                </div>
                <Input
                  label="Adresse"
                  value={newPatient.adresse}
                  onChange={(e) => setNewPatient({ ...newPatient, adresse: e.target.value })}
                />
                <Button type="submit" className="w-full">
                  Créer la fiche patient
                </Button>
              </form>
            )}
          </div>
        </Card>
      ) : (
        <Card className="p-5">
          <div className="mb-4 flex items-center justify-between rounded-md bg-bg px-3 py-2">
            <div>
              <p className="text-sm font-medium text-ink">
                {selectedPatient.nom} {selectedPatient.prenom}
              </p>
              <p className="text-xs text-ink-soft">Patient sélectionné</p>
            </div>
            <button
              onClick={() => setSelectedPatient(null)}
              className="text-xs font-medium text-accent hover:underline"
            >
              Changer
            </button>
          </div>

          <form onSubmit={handleCreateVisit} className="space-y-4">
            <Textarea
              label="Symptômes / motif d'admission"
              rows={3}
              value={symptomes}
              onChange={(e) => setSymptomes(e.target.value)}
            />

            <div>
              <span className="mb-2 block text-sm font-medium text-ink">Niveau d'urgence</span>
              <div className="grid grid-cols-4 gap-2">
                {URGENCY_LEVELS.map((level) => (
                  <button
                    type="button"
                    key={level}
                    onClick={() => setNiveauUrgence(level)}
                    className={`rounded-md border px-2 py-2 text-xs font-medium transition-colors ${
                      niveauUrgence === level
                        ? 'border-accent bg-accent-soft text-accent-dark'
                        : 'border-border text-ink-soft hover:bg-bg'
                    }`}
                  >
                    {URGENCY_META[level].label}
                  </button>
                ))}
              </div>
            </div>

            {niveauUrgence === 'CRITIQUE' && (
              <div className="flex items-start gap-2 rounded-md bg-critique-soft px-3 py-2 text-xs text-critique">
                <Siren className="mt-0.5 h-4 w-4 shrink-0" />
                Une urgence critique déclenche immédiatement l'assignation d'un médecin et
                d'une salle de déchocage disponibles.
              </div>
            )}

            <Button type="submit" className="w-full" disabled={submitting}>
              {submitting ? 'Enregistrement…' : "Enregistrer l'admission"}
            </Button>
          </form>
        </Card>
      )}
    </div>
  )
}
