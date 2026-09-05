import { useEffect, useState, useCallback } from 'react'
import { UserRound, DoorClosed, BedDouble, Plus, Pencil, Trash2, Check, X } from 'lucide-react'
import api from '../api/client'
import Badge from '../components/Badge'
import { Card, PageHeader, Spinner, Button, Input, Select } from '../components/Primitives'
import { DOCTOR_STATUS_META, ROOM_TYPES, ROOM_TYPE_LABELS } from '../lib/constants'

const TABS = [
  { key: 'doctors', label: 'Médecins', icon: UserRound },
  { key: 'rooms', label: 'Salles', icon: DoorClosed },
  { key: 'beds', label: 'Lits', icon: BedDouble },
]

export default function ResourcesPage() {
  const [tab, setTab] = useState('doctors')

  return (
    <div>
      <PageHeader
        title="Ressources"
        description="Médecins, salles et lits disponibles pour la prise en charge. La gestion des stocks de médicaments vit dans un projet indépendant."
      />

      <div className="mb-6 flex gap-1 border-b border-border">
        {TABS.map(({ key, label, icon: Icon }) => (
          <button
            key={key}
            onClick={() => setTab(key)}
            className={`flex items-center gap-1.5 border-b-2 px-3 py-2 text-sm font-medium ${
              tab === key
                ? 'border-accent text-accent-dark'
                : 'border-transparent text-ink-soft hover:text-ink'
            }`}
          >
            <Icon className="h-4 w-4" />
            {label}
          </button>
        ))}
      </div>

      {tab === 'doctors' && <DoctorsTab />}
      {tab === 'rooms' && <RoomsTab />}
      {tab === 'beds' && <BedsTab />}
    </div>
  )
}

// ---------------------------------------------------------------------------
// Doctors
// ---------------------------------------------------------------------------
function DoctorsTab() {
  const [doctors, setDoctors] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState({ nom: '', prenom: '', specialite: '', statut: 'DISPONIBLE', telephone: '' })

  const fetchAll = useCallback(() => {
    api.get('/doctors').then((res) => setDoctors(res.data))
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  function startEdit(d) {
    setEditingId(d.id)
    setForm(d)
    setShowForm(true)
  }

  function startCreate() {
    setEditingId(null)
    setForm({ nom: '', prenom: '', specialite: '', statut: 'DISPONIBLE', telephone: '' })
    setShowForm(true)
  }

  async function submit(e) {
    e.preventDefault()
    if (editingId) {
      await api.put(`/doctors/${editingId}`, form)
    } else {
      await api.post('/doctors', form)
    }
    setShowForm(false)
    fetchAll()
  }

  async function remove(id) {
    await api.delete(`/doctors/${id}`)
    fetchAll()
  }

  if (doctors === null) return <Spinner />

  return (
    <div>
      <div className="mb-3 flex justify-end">
        <Button onClick={startCreate}>
          <Plus className="h-4 w-4" />
          Nouveau médecin
        </Button>
      </div>

      {showForm && (
        <Card className="mb-4 p-4">
          <form onSubmit={submit} className="grid grid-cols-2 gap-3 sm:grid-cols-5 sm:items-end">
            <Input label="Nom" required value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} />
            <Input label="Prénom" required value={form.prenom} onChange={(e) => setForm({ ...form, prenom: e.target.value })} />
            <Input label="Spécialité" value={form.specialite} onChange={(e) => setForm({ ...form, specialite: e.target.value })} />
            <Input label="Téléphone" value={form.telephone} onChange={(e) => setForm({ ...form, telephone: e.target.value })} />
            <Select label="Statut" value={form.statut} onChange={(e) => setForm({ ...form, statut: e.target.value })}>
              {Object.keys(DOCTOR_STATUS_META).map((s) => (
                <option key={s} value={s}>{DOCTOR_STATUS_META[s].label}</option>
              ))}
            </Select>
            <div className="col-span-2 flex gap-2 sm:col-span-5">
              <Button type="submit">{editingId ? 'Enregistrer' : 'Créer'}</Button>
              <Button type="button" variant="ghost" onClick={() => setShowForm(false)}>Annuler</Button>
            </div>
          </form>
        </Card>
      )}

      <Card className="overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-border bg-bg/60 text-xs uppercase tracking-wide text-ink-soft">
            <tr>
              <th className="px-4 py-3 font-medium">Nom</th>
              <th className="px-4 py-3 font-medium">Spécialité</th>
              <th className="px-4 py-3 font-medium">Téléphone</th>
              <th className="px-4 py-3 font-medium">Statut</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {doctors.map((d) => (
              <tr key={d.id} className="hover:bg-bg/60">
                <td className="px-4 py-3 font-medium text-ink">Dr {d.nom} {d.prenom}</td>
                <td className="px-4 py-3 text-ink-soft">{d.specialite || '—'}</td>
                <td className="px-4 py-3 text-ink-soft">{d.telephone || '—'}</td>
                <td className="px-4 py-3"><Badge meta={DOCTOR_STATUS_META[d.statut]} /></td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-1">
                    <Button variant="ghost" onClick={() => startEdit(d)}><Pencil className="h-4 w-4" /></Button>
                    <Button variant="ghost" onClick={() => remove(d.id)}><Trash2 className="h-4 w-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  )
}

// ---------------------------------------------------------------------------
// Rooms
// ---------------------------------------------------------------------------
function RoomsTab() {
  const [rooms, setRooms] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState({ nom: '', type: 'CONSULTATION', disponible: true })

  const fetchAll = useCallback(() => {
    api.get('/rooms').then((res) => setRooms(res.data))
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  function startEdit(r) {
    setEditingId(r.id)
    setForm(r)
    setShowForm(true)
  }

  function startCreate() {
    setEditingId(null)
    setForm({ nom: '', type: 'CONSULTATION', disponible: true })
    setShowForm(true)
  }

  async function submit(e) {
    e.preventDefault()
    if (editingId) {
      await api.put(`/rooms/${editingId}`, form)
    } else {
      await api.post('/rooms', form)
    }
    setShowForm(false)
    fetchAll()
  }

  async function remove(id) {
    await api.delete(`/rooms/${id}`)
    fetchAll()
  }

  if (rooms === null) return <Spinner />

  return (
    <div>
      <div className="mb-3 flex justify-end">
        <Button onClick={startCreate}><Plus className="h-4 w-4" />Nouvelle salle</Button>
      </div>

      {showForm && (
        <Card className="mb-4 p-4">
          <form onSubmit={submit} className="grid grid-cols-2 gap-3 sm:grid-cols-4 sm:items-end">
            <Input label="Nom" required value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} />
            <Select label="Type" value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}>
              {ROOM_TYPES.map((t) => <option key={t} value={t}>{ROOM_TYPE_LABELS[t]}</option>)}
            </Select>
            <label className="flex items-center gap-2 pb-2 text-sm text-ink">
              <input type="checkbox" checked={form.disponible} onChange={(e) => setForm({ ...form, disponible: e.target.checked })} />
              Disponible
            </label>
            <div className="col-span-2 flex gap-2 sm:col-span-1">
              <Button type="submit">{editingId ? 'Enregistrer' : 'Créer'}</Button>
              <Button type="button" variant="ghost" onClick={() => setShowForm(false)}>Annuler</Button>
            </div>
          </form>
        </Card>
      )}

      <Card className="overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-border bg-bg/60 text-xs uppercase tracking-wide text-ink-soft">
            <tr>
              <th className="px-4 py-3 font-medium">Nom</th>
              <th className="px-4 py-3 font-medium">Type</th>
              <th className="px-4 py-3 font-medium">Disponibilité</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {rooms.map((r) => (
              <tr key={r.id} className="hover:bg-bg/60">
                <td className="px-4 py-3 font-medium text-ink">{r.nom}</td>
                <td className="px-4 py-3 text-ink-soft">{ROOM_TYPE_LABELS[r.type]}</td>
                <td className="px-4 py-3">
                  {r.disponible ? (
                    <span className="inline-flex items-center gap-1 text-faible"><Check className="h-4 w-4" />Disponible</span>
                  ) : (
                    <span className="inline-flex items-center gap-1 text-ink-soft"><X className="h-4 w-4" />Occupée</span>
                  )}
                </td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-1">
                    <Button variant="ghost" onClick={() => startEdit(r)}><Pencil className="h-4 w-4" /></Button>
                    <Button variant="ghost" onClick={() => remove(r.id)}><Trash2 className="h-4 w-4" /></Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  )
}

// ---------------------------------------------------------------------------
// Beds
// ---------------------------------------------------------------------------
function BedsTab() {
  const [beds, setBeds] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ numero: '', service: '' })

  const fetchAll = useCallback(() => {
    api.get('/beds').then((res) => setBeds(res.data))
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  async function submit(e) {
    e.preventDefault()
    await api.post('/beds', { ...form, occupe: false })
    setForm({ numero: '', service: '' })
    setShowForm(false)
    fetchAll()
  }

  async function liberer(id) {
    await api.put(`/beds/${id}/liberer`)
    fetchAll()
  }

  if (beds === null) return <Spinner />

  return (
    <div>
      <div className="mb-3 flex justify-end">
        <Button onClick={() => setShowForm((v) => !v)}><Plus className="h-4 w-4" />Nouveau lit</Button>
      </div>

      {showForm && (
        <Card className="mb-4 p-4">
          <form onSubmit={submit} className="flex flex-wrap items-end gap-3">
            <Input label="Numéro" required value={form.numero} onChange={(e) => setForm({ ...form, numero: e.target.value })} />
            <Input label="Service" required placeholder="ex : CARDIOLOGIE" value={form.service} onChange={(e) => setForm({ ...form, service: e.target.value.toUpperCase() })} />
            <Button type="submit">Créer</Button>
          </form>
        </Card>
      )}

      <Card className="overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-border bg-bg/60 text-xs uppercase tracking-wide text-ink-soft">
            <tr>
              <th className="px-4 py-3 font-medium">Numéro</th>
              <th className="px-4 py-3 font-medium">Service</th>
              <th className="px-4 py-3 font-medium">Occupation</th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {beds.map((b) => (
              <tr key={b.id} className="hover:bg-bg/60">
                <td className="px-4 py-3 font-mono text-xs text-ink">{b.numero}</td>
                <td className="px-4 py-3 text-ink-soft">{b.service}</td>
                <td className="px-4 py-3">
                  {b.occupe ? (
                    <span className="inline-flex items-center gap-1 text-elevee"><X className="h-4 w-4" />Occupé</span>
                  ) : (
                    <span className="inline-flex items-center gap-1 text-faible"><Check className="h-4 w-4" />Libre</span>
                  )}
                </td>
                <td className="px-4 py-3 text-right">
                  {b.occupe && (
                    <Button variant="ghost" onClick={() => liberer(b.id)}>Libérer</Button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  )
}
