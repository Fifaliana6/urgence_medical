import { useEffect, useState, useCallback } from 'react';
import { getExamsEnAttente, saisirResultatExamen } from '../api/examApi';

export default function LaboPage() {
  const [exams, setExams] = useState([]);
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState('');
  const [resultats, setResultats] = useState({});
  const [action, setAction] = useState(null);

  const charger = useCallback(async () => {
    setErreur('');
    try {
      const data = await getExamsEnAttente();
      setExams(data);
    } catch (err) {
      setErreur('Impossible de charger les examens en attente.');
    } finally {
      setChargement(false);
    }
  }, []);

  useEffect(() => {
    charger();
    const interval = setInterval(charger, 8000);
    return () => clearInterval(interval);
  }, [charger]);

  async function handleSaisirResultat(examId) {
    const resultat = resultats[examId];
    if (!resultat || !resultat.trim()) {
      setErreur('Saisissez un résultat avant de valider.');
      return;
    }
    setErreur('');
    setAction(examId);
    try {
      await saisirResultatExamen(examId, resultat);
      setResultats({ ...resultats, [examId]: '' });
      await charger();
    } catch (err) {
      setErreur(err.response?.data?.message || "Erreur lors de l'enregistrement du résultat.");
    } finally {
      setAction(null);
    }
  }

  if (chargement) return <div className="page-container"><p>Chargement...</p></div>;

  return (
    <div className="page-container">
      <h1>Laboratoire — Examens en attente</h1>
      {erreur && <div className="alert alert-error">{erreur}</div>}

      {exams.length === 0 ? (
        <p className="empty-state">Aucun examen en attente.</p>
      ) : (
        exams.map((ex) => (
          <div className="card" key={ex.id}>
            <div className="exam-header">
              <div>
                <strong>{ex.libelle}</strong> — {ex.type}
                <p className="text-muted">Patient : {ex.patientNomComplet} · Demandé par {ex.demandeParNom} le {new Date(ex.dateDemande).toLocaleString('fr-FR')}</p>
              </div>
            </div>
            <div className="form-inline">
              <textarea
                placeholder="Saisir le résultat..."
                rows={2}
                value={resultats[ex.id] || ''}
                onChange={(e) => setResultats({ ...resultats, [ex.id]: e.target.value })}
              />
              <button className="btn btn-primary" disabled={action === ex.id} onClick={() => handleSaisirResultat(ex.id)}>
                {action === ex.id ? 'Envoi...' : 'Valider le résultat'}
              </button>
            </div>
          </div>
        ))
      )}
    </div>
  );
}