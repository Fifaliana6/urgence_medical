import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { listerToutesVisites } from '../api/visitApi';
import UrgencyBadge from '../components/UrgencyBadge';
import StatusBadge from '../components/StatusBadge';

const STATUTS = [
  { value: '', label: 'Tous les statuts' },
  { value: 'EN_ATTENTE', label: 'En attente' },
  { value: 'EN_CONSULTATION', label: 'En consultation' },
  { value: 'EN_EXAMEN', label: 'En examen' },
  { value: 'HOSPITALIZED', label: 'Hospitalisé' },
  { value: 'DISCHARGED', label: 'Sorti / clôturé' },
];

export default function PatientTrackingPage() {
  const [visites, setVisites] = useState([]);
  const [filtre, setFiltre] = useState('');
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState('');
  const navigate = useNavigate();

  const charger = useCallback(async () => {
    setErreur('');
    try {
      const data = await listerToutesVisites(filtre || undefined);
      setVisites(data);
    } catch (err) {
      setErreur('Impossible de charger le suivi des patients.');
    } finally {
      setChargement(false);
    }
  }, [filtre]);

  useEffect(() => {
    setChargement(true);
    charger();
    const interval = setInterval(charger, 15000);
    return () => clearInterval(interval);
  }, [charger]);

  return (
    <div className="page-container">
      <h1>Suivi des patients</h1>
      {erreur && <div className="alert alert-error">{erreur}</div>}

      <div className="form-inline">
        <select value={filtre} onChange={(e) => setFiltre(e.target.value)}>
          {STATUTS.map((s) => <option key={s.value} value={s.value}>{s.label}</option>)}
        </select>
      </div>

      {chargement ? (
        <p>Chargement...</p>
      ) : visites.length === 0 ? (
        <p className="empty-state">Aucune visite pour ce filtre.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Patient</th><th>Urgence</th><th>Statut</th><th>Arrivée</th>
              <th>Médecin</th><th>Lit</th><th></th>
            </tr>
          </thead>
          <tbody>
            {visites.map((v) => (
              <tr key={v.id} className={v.niveauUrgence === 'CRITIQUE' && v.status !== 'DISCHARGED' ? 'row-critique' : ''}>
                <td>{v.patientNomComplet}</td>
                <td><UrgencyBadge niveau={v.niveauUrgence} /></td>
                <td><StatusBadge status={v.status} /></td>
                <td>{new Date(v.heureArrivee).toLocaleString('fr-FR')}</td>
                <td>{v.medecinNom || '—'}</td>
                <td>{v.litAffecte || '—'}</td>
                <td>
                  <button className="btn btn-primary btn-sm" onClick={() => navigate(`/visites/${v.id}`)}>Ouvrir</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}