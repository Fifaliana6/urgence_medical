const LABELS = {
    CRITIQUE: 'Critique',
    ELEVEE: 'Élevée',
    MOYENNE: 'Moyenne',
    FAIBLE: 'Faible',
  };
  
  export default function UrgencyBadge({ niveau }) {
    return (
      <span className={`badge badge-urgence-${niveau?.toLowerCase()}`}>
        {LABELS[niveau] || niveau}
      </span>
    );
  }