import { useEffect, useState, useCallback } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import {
  ArrowLeft,
  Stethoscope,
  Pill,
  FlaskConical,
  Plus,
  DoorOpen,
  BedDouble,
  Receipt,
  CheckCircle2,
} from 'lucide-react'
import api from '../api/client'
import Modal from '../components/Modal'
import Badge from '../components/Badge'
import {
  Card,
  PageHeader,
  Spinner,
  ErrorBanner,
  Button,
  Input,
  Select,
  Textarea,
} from '../components/Primitives'
import {
  URGENCY_META,
  VISIT_STATUS_META,
  EXAM_STATUS_META,
  formatDateTime,
} from '../lib/constants'

export default function VisitDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [visit, setVisit] = useState(null)
  const [consultations, setConsultations] = useState([])
  const [doctors, setDoctors] = useState([])
  const [invoice, setInvoice] = useState(null)
  const [error, setError] = useState(null)
  const [showDecision, setShowDecision] = useState(false)

  const refresh = useCallback(async () => {
    try {
      const [visitRes, consultRes] = await Promise.all([
        api.get(`/visits/${id}`),
        api.get(`/consultations/visite/${id}`),
      ])
      setVisit(visitRes.data)
      setConsultations(consultRes.data)
      if (['DISCHARGED', 'HOSPITALIZED'].includes(visitRes.data.statut)) {
        try {
          const { data } = await api.get(`/invoices/visite/${id}`)
          setInvoice(data)
        } catch {
          setInvoice(null)
        }
      }
    } catch {
      setError('Impossible de charger cette visite.')
    }
  }, [id])

  useEffect(() => {
    refresh()
    api.get('/doctors').then((res) => setDoctors(res.data)).catch(() => {})
  }, [refresh])

  if (error) return <ErrorBanner message={error} />
  if (!visit) return <Spinner />

  const isClosed = visit.statut === 'DISCHARGED' || visit.statut === 'HOSPITALIZED'

  return (
    <div className="mx-auto max-w-3xl">
      <button
        onClick={() => navigate('/')}
        className="mb-4 inline-flex items-center gap-1.5 text-sm text-ink-soft hover:text-ink"
      >
        <ArrowLeft className="h-4 w-4" />
        Retour à la file d'attente
      </button>

      <PageHeader
        title={`${visit.patient?.nom} ${visit.patient?.prenom}`}
        description={`Visite #${visit.id} · arrivée le ${formatDateTime(visit.dateArrivee)}`}
        actions={
          !isClosed && (
            <Button onClick={() => setShowDecision(true)}>
              <CheckCircle2 className="h-4 w-4" />
              Décision de sortie
            </Button>
          )
        }
      />

      {/* Visit summary */}
      <Card className="mb-6 grid grid-cols-2 gap-4 p-5 sm:grid-cols-4">
        <SummaryItem label="Urgence">
          <Badge meta={URGENCY_META[visit.niveauUrgence]} withDot />
        </SummaryItem>
        <SummaryItem label="Statut">
          <Badge meta={VISIT_STATUS_META[visit.statut]} />
        </SummaryItem>
        <SummaryItem label="Médecin">
          {visit.medecin ? `Dr ${visit.medecin.nom}` : '—'}
        </SummaryItem>
        <SummaryItem label={visit.lit ? 'Lit' : 'Salle'}>
          {visit.lit ? `${visit.lit.numero} (${visit.lit.service})` : visit.salle ? visit.salle.nom : '—'}
        </SummaryItem>
      </Card>

      {visit.symptomes && (
        <Card className="mb-6 p-5">
          <p className="mb-1 text-xs font-medium uppercase tracking-wide text-ink-soft">
            Motif d'admission
          </p>
          <p className="text-sm text-ink">{visit.symptomes}</p>
        </Card>
      )}

      {invoice && (
        <Card className="mb-6 flex items-center justify-between p-5">
          <div className="flex items-center gap-3">
            <Receipt className="h-5 w-5 text-accent" />
            <div>
              <p className="text-sm font-medium text-ink">Facture générée</p>
              <p className="text-xs text-ink-soft">
                Statut : {invoice.statut === 'PAYEE' ? 'Payée' : 'En attente de paiement'}
              </p>
            </div>
          </div>
          <Link to="/invoices" state={{ visitId: visit.id }} className="text-sm font-medium text-accent hover:underline">
            Voir la facture
          </Link>
        </Card>
      )}

      {/* Consultations */}
      <div className="mb-3 flex items-center gap-2">
        <Stethoscope className="h-4 w-4 text-ink-soft" />
        <h2 className="text-sm font-semibold text-ink">Consultations</h2>
      </div>

      <div className="space-y-4">
        {consultations.map((c) => (
          <ConsultationCard key={c.id} consultation={c} onChanged={refresh} disabled={isClosed} />
        ))}

        {!isClosed && (
          <NewConsultationForm visitId={visit.id} doctors={doctors} onCreated={refresh} />
        )}
      </div>

      {showDecision && (
        <DecisionModal
          visitId={visit.id}
          onClose={() => setShowDecision(false)}
          onDecided={() => {
            setShowDecision(false)
            refresh()
          }}
        />
      )}
    </div>
  )
}

function SummaryItem({ label, children }) {
  return (
    <div>
      <p className="mb-1 text-xs font-medium uppercase tracking-wide text-ink-soft">{label}</p>
      <div className="text-sm text-ink">{children}</div>
    </div>
  )
}

function ConsultationCard({ consultation, onChanged, disabled }) {
  const [showPrescriptionForm, setShowPrescriptionForm] = useState(false)
  const [showExamForm, setShowExamForm] = useState(false)

  return (
    <Card className="p-5">
      <div className="mb-3 flex items-start justify-between">
        <div>
          <p className="text-xs text-ink-soft">
            Dr {consultation.medecin?.nom} · {formatDateTime(consultation.dateConsultation)}
          </p>
          {consultation.diagnostic && (
            <p className="mt-1 text-sm font-medium text-ink">{consultation.diagnostic}</p>
          )}
          {consultation.planTraitement && (
            <p className="mt-1 text-sm text-ink-soft">{consultation.planTraitement}</p>
          )}
        </div>
      </div>

      {/* Prescriptions */}
      <div className="mt-4 border-t border-border pt-4">
        <div className="mb-2 flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wide text-ink-soft">
            <Pill className="h-3.5 w-3.5" />
            Prescriptions
          </div>
          {!disabled && (
            <button
              onClick={() => setShowPrescriptionForm((v) => !v)}
              className="text-xs font-medium text-accent hover:underline"
            >
              + Ajouter
            </button>
          )}
        </div>
        {consultation.prescriptions?.length > 0 ? (
          <ul className="space-y-1">
            {consultation.prescriptions.map((p) => (
              <li key={p.id} className="text-sm text-ink">
                {p.medicament}
                {p.dosage && <span className="text-ink-soft"> · {p.dosage}</span>}
                {p.duree && <span className="text-ink-soft"> · {p.duree}</span>}
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-sm text-ink-faint">Aucune prescription.</p>
        )}
        {showPrescriptionForm && (
          <PrescriptionForm
            consultationId={consultation.id}
            onAdded={() => {
              setShowPrescriptionForm(false)
              onChanged()
            }}
          />
        )}
      </div>

      {/* Exams */}
      <div className="mt-4 border-t border-border pt-4">
        <div className="mb-2 flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wide text-ink-soft">
            <FlaskConical className="h-3.5 w-3.5" />
            Examens
          </div>
          {!disabled && (
            <button
              onClick={() => setShowExamForm((v) => !v)}
              className="text-xs font-medium text-accent hover:underline"
            >
              + Ajouter
            </button>
          )}
        </div>
        {consultation.examens?.length > 0 ? (
          <ul className="space-y-2">
            {consultation.examens.map((exam) => (
              <ExamRow key={exam.id} exam={exam} onChanged={onChanged} disabled={disabled} />
            ))}
          </ul>
        ) : (
          <p className="text-sm text-ink-faint">Aucun examen demandé.</p>
        )}
        {showExamForm && (
          <ExamForm
            consultationId={consultation.id}
            onAdded={() => {
              setShowExamForm(false)
              onChanged()
            }}
          />
        )}
      </div>
    </Card>
  )
}

function ExamRow({ exam, onChanged, disabled }) {
  const [editing, setEditing] = useState(false)
  const [resultat, setResultat] = useState('')
  const [saving, setSaving] = useState(false)
  const hasResult = exam.statut === 'RESULTAT_DISPONIBLE'

  async function submit(e) {
    e.preventDefault()
    setSaving(true)
    try {
      await api.put(`/exams/${exam.id}/resultat`, resultat, {
        headers: { 'Content-Type': 'text/plain' },
      })
      setEditing(false)
      onChanged()
    } finally {
      setSaving(false)
    }
  }

  return (
    <li className="rounded-md bg-bg/60 p-2.5">
      <div className="flex items-center justify-between">
        <span className="text-sm text-ink">{exam.type}</span>
        <div className="flex items-center gap-2">
          <Badge meta={EXAM_STATUS_META[exam.statut]} />
          {!hasResult && !disabled && !editing && (
            <button
              onClick={() => setEditing(true)}
              className="text-xs font-medium text-accent hover:underline"
            >
              Saisir résultat
            </button>
          )}
        </div>
      </div>
      {hasResult && exam.resultat && (
        <p className="mt-1.5 text-sm text-ink-soft">{exam.resultat}</p>
      )}
      {editing && (
        <form onSubmit={submit} className="mt-2 flex gap-2">
          <Input
            autoFocus
            placeholder="Résultat de l'examen…"
            value={resultat}
            onChange={(e) => setResultat(e.target.value)}
            className="flex-1"
          />
          <Button type="submit" disabled={saving || !resultat.trim()}>
            Valider
          </Button>
        </form>
      )}
    </li>
  )
}

function NewConsultationForm({ visitId, doctors, onCreated }) {
  const [medecinId, setMedecinId] = useState('')
  const [diagnostic, setDiagnostic] = useState('')
  const [planTraitement, setPlanTraitement] = useState('')
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await api.post('/consultations', {
        visitId: Number(visitId),
        medecinId: Number(medecinId),
        diagnostic,
        planTraitement,
      })
      setDiagnostic('')
      setPlanTraitement('')
      onCreated()
    } catch {
      setError('La création de la consultation a échoué.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card className="border-dashed p-5">
      <p className="mb-3 flex items-center gap-1.5 text-sm font-medium text-ink">
        <Plus className="h-4 w-4" />
        Nouvelle consultation
      </p>
      <ErrorBanner message={error} />
      <form onSubmit={submit} className="space-y-3">
        <Select label="Médecin" required value={medecinId} onChange={(e) => setMedecinId(e.target.value)}>
          <option value="">Sélectionner un médecin…</option>
          {doctors.map((d) => (
            <option key={d.id} value={d.id}>
              Dr {d.nom} {d.prenom} — {d.specialite}
            </option>
          ))}
        </Select>
        <Textarea
          label="Diagnostic"
          rows={2}
          value={diagnostic}
          onChange={(e) => setDiagnostic(e.target.value)}
        />
        <Textarea
          label="Plan de traitement"
          rows={2}
          value={planTraitement}
          onChange={(e) => setPlanTraitement(e.target.value)}
        />
        <Button type="submit" disabled={submitting || !medecinId}>
          Enregistrer la consultation
        </Button>
      </form>
    </Card>
  )
}

function PrescriptionForm({ consultationId, onAdded }) {
  const [form, setForm] = useState({ medicament: '', dosage: '', duree: '', instructions: '' })
  const [submitting, setSubmitting] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setSubmitting(true)
    try {
      await api.post(`/consultations/${consultationId}/prescriptions`, form)
      onAdded()
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={submit} className="mt-3 space-y-2 rounded-md bg-bg/60 p-3">
      <div className="grid grid-cols-3 gap-2">
        <Input
          placeholder="Médicament"
          required
          value={form.medicament}
          onChange={(e) => setForm({ ...form, medicament: e.target.value })}
        />
        <Input
          placeholder="Dosage"
          value={form.dosage}
          onChange={(e) => setForm({ ...form, dosage: e.target.value })}
        />
        <Input
          placeholder="Durée"
          value={form.duree}
          onChange={(e) => setForm({ ...form, duree: e.target.value })}
        />
      </div>
      <Button type="submit" variant="secondary" disabled={submitting || !form.medicament.trim()}>
        Ajouter la prescription
      </Button>
    </form>
  )
}

function ExamForm({ consultationId, onAdded }) {
  const [type, setType] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setSubmitting(true)
    try {
      await api.post(`/consultations/${consultationId}/exams`, { type })
      onAdded()
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={submit} className="mt-3 flex gap-2 rounded-md bg-bg/60 p-3">
      <Input
        placeholder="Type d'examen (ex : Bilan sanguin)"
        required
        value={type}
        onChange={(e) => setType(e.target.value)}
        className="flex-1"
      />
      <Button type="submit" variant="secondary" disabled={submitting || !type.trim()}>
        Demander
      </Button>
    </form>
  )
}

function DecisionModal({ visitId, onClose, onDecided }) {
  const [statutFinal, setStatutFinal] = useState('DISCHARGED')
  const [serviceHospitalisation, setServiceHospitalisation] = useState('')
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await api.put(`/visits/${visitId}/decision`, {
        statutFinal,
        serviceHospitalisation: statutFinal === 'HOSPITALIZED' ? serviceHospitalisation : null,
      })
      onDecided()
    } catch (err) {
      setError(err.response?.data?.erreur || 'La décision n\u2019a pas pu être enregistrée.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal title="Décision de sortie" onClose={onClose}>
      <ErrorBanner message={error} />
      <form onSubmit={submit} className="space-y-4">
        <div className="grid grid-cols-2 gap-2">
          <button
            type="button"
            onClick={() => setStatutFinal('DISCHARGED')}
            className={`flex flex-col items-center gap-1.5 rounded-md border px-3 py-3 text-sm font-medium ${
              statutFinal === 'DISCHARGED'
                ? 'border-accent bg-accent-soft text-accent-dark'
                : 'border-border text-ink-soft hover:bg-bg'
            }`}
          >
            <DoorOpen className="h-4 w-4" />
            Sortie
          </button>
          <button
            type="button"
            onClick={() => setStatutFinal('HOSPITALIZED')}
            className={`flex flex-col items-center gap-1.5 rounded-md border px-3 py-3 text-sm font-medium ${
              statutFinal === 'HOSPITALIZED'
                ? 'border-accent bg-accent-soft text-accent-dark'
                : 'border-border text-ink-soft hover:bg-bg'
            }`}
          >
            <BedDouble className="h-4 w-4" />
            Hospitalisation
          </button>
        </div>

        {statutFinal === 'HOSPITALIZED' && (
          <Input
            label="Service d'hospitalisation"
            placeholder="ex : CARDIOLOGIE, REANIMATION…"
            required
            value={serviceHospitalisation}
            onChange={(e) => setServiceHospitalisation(e.target.value.toUpperCase())}
          />
        )}

        <p className="text-xs text-ink-soft">
          La facture sera générée automatiquement à partir des consultations, prescriptions et
          examens de cette visite.
        </p>

        <Button type="submit" className="w-full" disabled={submitting}>
          Confirmer
        </Button>
      </form>
    </Modal>
  )
}
