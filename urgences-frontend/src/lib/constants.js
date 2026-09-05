// These values mirror the Java enums exactly (UrgencyLevel, VisitStatus,
// DoctorStatus, RoomType, ExamStatus, InvoiceStatus) -- keep in sync with
// com.hopital.urgences.model.*

export const URGENCY_LEVELS = ['CRITIQUE', 'ELEVEE', 'MOYENNE', 'FAIBLE']

export const URGENCY_META = {
  CRITIQUE: { label: 'Critique', fg: 'text-critique', bg: 'bg-critique-soft', dot: 'bg-critique' },
  ELEVEE: { label: 'Élevée', fg: 'text-elevee', bg: 'bg-elevee-soft', dot: 'bg-elevee' },
  MOYENNE: { label: 'Moyenne', fg: 'text-moyenne', bg: 'bg-moyenne-soft', dot: 'bg-moyenne' },
  FAIBLE: { label: 'Faible', fg: 'text-faible', bg: 'bg-faible-soft', dot: 'bg-faible' },
}

export const VISIT_STATUS_META = {
  EN_ATTENTE: { label: 'En attente', fg: 'text-ink-soft', bg: 'bg-ink-soft/10' },
  EN_CONSULTATION: { label: 'En consultation', fg: 'text-accent-dark', bg: 'bg-accent-soft' },
  EN_EXAMEN: { label: "En examen", fg: 'text-elevee', bg: 'bg-elevee-soft' },
  DECIDE: { label: 'Décidé', fg: 'text-ink-soft', bg: 'bg-ink-soft/10' },
  DISCHARGED: { label: 'Sorti(e)', fg: 'text-faible', bg: 'bg-faible-soft' },
  HOSPITALIZED: { label: 'Hospitalisé(e)', fg: 'text-accent-dark', bg: 'bg-accent-soft' },
}

export const DOCTOR_STATUS_META = {
  DISPONIBLE: { label: 'Disponible', fg: 'text-faible', bg: 'bg-faible-soft' },
  OCCUPE: { label: 'Occupé', fg: 'text-elevee', bg: 'bg-elevee-soft' },
  HORS_SERVICE: { label: 'Hors service', fg: 'text-ink-soft', bg: 'bg-ink-soft/10' },
}

export const ROOM_TYPES = ['DECHOCAGE', 'CONSULTATION', 'EXAMEN']
export const ROOM_TYPE_LABELS = {
  DECHOCAGE: 'Déchocage',
  CONSULTATION: 'Consultation',
  EXAMEN: 'Examen',
}

export const EXAM_STATUS_META = {
  PRESCRIT: { label: 'Prescrit', fg: 'text-ink-soft', bg: 'bg-ink-soft/10' },
  EN_ATTENTE: { label: 'En attente', fg: 'text-elevee', bg: 'bg-elevee-soft' },
  REALISE: { label: 'Réalisé', fg: 'text-accent-dark', bg: 'bg-accent-soft' },
  RESULTAT_DISPONIBLE: { label: 'Résultat disponible', fg: 'text-faible', bg: 'bg-faible-soft' },
}

export const INVOICE_STATUS_META = {
  EN_ATTENTE: { label: 'En attente', fg: 'text-elevee', bg: 'bg-elevee-soft' },
  PAYEE: { label: 'Payée', fg: 'text-faible', bg: 'bg-faible-soft' },
  IMPAYEE: { label: 'Impayée', fg: 'text-critique', bg: 'bg-critique-soft' },
}

export function formatDateTime(value) {
  if (!value) return '—'
  return new Date(value).toLocaleString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function formatCurrency(value) {
  if (value === null || value === undefined) return '—'
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(value)
}
