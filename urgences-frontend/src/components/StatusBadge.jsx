const LABELS = {
    EN_ATTENTE: 'En attente',
    EN_CONSULTATION: 'En consultation',
    EN_EXAMEN: 'En examen',
    HOSPITALIZED: 'Hospitalisé',
    DISCHARGED: 'Sorti',
  };
  
  export default function StatusBadge({ status }) {
    return (
      <span className={`badge badge-statut-${status?.toLowerCase()}`}>
        {LABELS[status] || status}
      </span>
    );
  }