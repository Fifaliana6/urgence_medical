import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getListeAttente } from '../api/visitApi';
import UrgencyBadge from '../components/UrgencyBadge';
import StatusBadge from '../components/StatusBadge';

export default function WaitingListPage() {
  const [visites, setVisites] = useState([]);
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState('');
  const navigate = useNavigate();

  const charger = useCallback(async () => {
    setErreur('');
    try {
      const data = await getListeAttente();
      setVisites(data);
    } catch (err) {
      setErreur("Impossible de charger la liste d'attente.");
    } finally {
      setChargement(false);
    }
  }, []);

  useEffect(() => {
    charger();
    const interval = setInterval(charger, 10000);
    return () => clearInterval(interval);
  }, [charger]);

  if (chargement) return <div className="page-container"><p>Chargement...</p></div>;

  return (
    <div className="page-container">
      <h1>Liste d'attente</h1>
      {erreur && <div className="alert alert-error">{erreur}</div>}

      {visites.length === 0 ? (
        <p className="empty-state">Aucun patient en attente actuellement.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Patient</th>
              <th>Urgence</th>
              <th>Statut</th>
              <th>Arrivée</th>
              <th>Médecin</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {visites.map((v) => (
              <tr key={v.id} className={v.niveauUrgence === 'CRITIQUE' ? 'row-critique' : ''}>
                <td>{v.patientNomComplet}</td>
                <td><UrgencyBadge niveau={v.niveauUrgence} /></td>
                <td><StatusBadge status={v.status} /></td>
                <td>{new Date(v.heureArrivee).toLocaleTimeString('fr-FR')}</td>
                <td>{v.medecinNom || '—'}</td>
                <td>
                  <button className="btn btn-primary btn-sm" onClick={() => navigate(`/visites/${v.id}`)}>
                    Ouvrir le dossier
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}