import { useEffect, useState, useCallback } from 'react';
import { listerUtilisateurs, approuverUtilisateur, rejeterUtilisateur } from '../api/adminApi';

const STATUTS = [
  { value: '', label: 'Tous' },
  { value: 'PENDING', label: 'En attente' },
  { value: 'APPROVED', label: 'Approuvés' },
  { value: 'REJECTED', label: 'Rejetés' },
];

export default function AdminUsersPage() {
  const [utilisateurs, setUtilisateurs] = useState([]);
  const [filtre, setFiltre] = useState('PENDING');
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState('');
  const [action, setAction] = useState(null);

  const charger = useCallback(async () => {
    setErreur('');
    try {
      const data = await listerUtilisateurs(filtre || undefined);
      setUtilisateurs(data);
    } catch (err) {
      setErreur('Impossible de charger les utilisateurs.');
    } finally {
      setChargement(false);
    }
  }, [filtre]);

  useEffect(() => {
    setChargement(true);
    charger();
  }, [charger]);

  async function handleApprouver(id) {
    setAction(id);
    try {
      await approuverUtilisateur(id);
      await charger();
    } catch (err) {
      setErreur("Erreur lors de l'approbation.");
    } finally {
      setAction(null);
    }
  }

  async function handleRejeter(id) {
    if (!window.confirm('Confirmer le rejet de cette demande de compte ?')) return;
    setAction(id);
    try {
      await rejeterUtilisateur(id);
      await charger();
    } catch (err) {
      setErreur('Erreur lors du rejet.');
    } finally {
      setAction(null);
    }
  }

  return (
    <div className="page-container">
      <h1>Gestion des utilisateurs</h1>
      {erreur && <div className="alert alert-error">{erreur}</div>}

      <div className="form-inline">
        <select value={filtre} onChange={(e) => setFiltre(e.target.value)}>
          {STATUTS.map((s) => <option key={s.value} value={s.value}>{s.label}</option>)}
        </select>
      </div>

      {chargement ? (
        <p>Chargement...</p>
      ) : utilisateurs.length === 0 ? (
        <p className="empty-state">Aucun utilisateur pour ce filtre.</p>
      ) : (
        <table className="table">
          <thead>
            <tr><th>Nom</th><th>Email</th><th>Rôle</th><th>Statut</th><th>Disponible</th><th></th></tr>
          </thead>
          <tbody>
            {utilisateurs.map((u) => (
              <tr key={u.id}>
                <td>{u.nom}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td><span className={`badge badge-statut-utilisateur-${u.statut.toLowerCase()}`}>{u.statut}</span></td>
                <td>{u.disponible ? 'Oui' : 'Non'}</td>
                <td>
                  {u.statut === 'PENDING' && (
                    <div className="form-inline" style={{ margin: 0 }}>
                      <button className="btn btn-success btn-sm" disabled={action === u.id} onClick={() => handleApprouver(u.id)}>Approuver</button>
                      <button className="btn btn-danger btn-sm" disabled={action === u.id} onClick={() => handleRejeter(u.id)}>Rejeter</button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}