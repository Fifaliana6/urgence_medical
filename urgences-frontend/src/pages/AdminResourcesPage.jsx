import { useEffect, useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { listerSalles, listerLits, creerSalle, creerLit, changerDisponibiliteSalle } from '../api/resourceApi';

const TYPES_SALLE = ['CONSULTATION', 'DECHOCAGE', 'SOIN', 'IMAGERIE'];
const SERVICES_MEDICAUX = ['CARDIOLOGIE', 'REANIMATION', 'MEDECINE_GENERALE', 'PEDIATRIE', 'CHIRURGIE'];

export default function AdminResourcesPage() {
  const { user } = useAuth();
  const peutGerer = user?.role === 'RECEPTIONIST';

  const [salles, setSalles] = useState([]);
  const [lits, setLits] = useState([]);
  const [nouvelleSalle, setNouvelleSalle] = useState({ nom: '', type: 'CONSULTATION' });
  const [nouveauLit, setNouveauLit] = useState({ numero: '', service: 'CARDIOLOGIE' });
  const [erreur, setErreur] = useState('');
  const [chargement, setChargement] = useState(true);
  const [action, setAction] = useState(false);

  const charger = useCallback(async () => {
    setErreur('');
    try {
      const [s, l] = await Promise.all([listerSalles(), listerLits()]);
      setSalles(s);
      setLits(l);
    } catch (err) {
      setErreur('Impossible de charger les ressources.');
    } finally {
      setChargement(false);
    }
  }, []);

  useEffect(() => { charger(); }, [charger]);

  async function handleCreerSalle(e) {
    e.preventDefault();
    setAction(true);
    try {
      await creerSalle(nouvelleSalle);
      setNouvelleSalle({ nom: '', type: 'CONSULTATION' });
      await charger();
    } catch (err) {
      setErreur('Erreur lors de la création de la salle.');
    } finally {
      setAction(false);
    }
  }

  async function handleCreerLit(e) {
    e.preventDefault();
    setAction(true);
    try {
      await creerLit(nouveauLit);
      setNouveauLit({ numero: '', service: 'CARDIOLOGIE' });
      await charger();
    } catch (err) {
      setErreur('Erreur lors de la création du lit.');
    } finally {
      setAction(false);
    }
  }

  async function toggleSalle(salle) {
    setAction(true);
    try {
      await changerDisponibiliteSalle(salle.id, !salle.disponible);
      await charger();
    } catch (err) {
      setErreur('Erreur lors de la mise à jour de la salle.');
    } finally {
      setAction(false);
    }
  }

  if (chargement) return <div className="page-container"><p>Chargement...</p></div>;

  return (
    <div className="page-container">
      <h1>Salles & Lits</h1>
      {erreur && <div className="alert alert-error">{erreur}</div>}
      {!peutGerer && (
        <p className="text-muted">Vue en lecture seule — seul le réceptionniste peut ajouter une salle/un lit ou changer une disponibilité.</p>
      )}

      <div className="card">
        <h2>Salles</h2>
        <table className="table table-compact">
          <thead><tr><th>Nom</th><th>Type</th><th>Disponible</th>{peutGerer && <th></th>}</tr></thead>
          <tbody>
            {salles.map((s) => (
              <tr key={s.id}>
                <td>{s.nom}</td>
                <td>{s.type}</td>
                <td>{s.disponible ? 'Oui' : 'Non'}</td>
                {peutGerer && (
                  <td>
                    <button className="btn btn-outline btn-sm" disabled={action} onClick={() => toggleSalle(s)}>
                      {s.disponible ? 'Marquer indisponible' : 'Marquer disponible'}
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
        {peutGerer && (
          <form className="form-inline" onSubmit={handleCreerSalle} style={{ marginTop: '1rem' }}>
            <input placeholder="Nom de la salle" required value={nouvelleSalle.nom} onChange={(e) => setNouvelleSalle({ ...nouvelleSalle, nom: e.target.value })} />
            <select value={nouvelleSalle.type} onChange={(e) => setNouvelleSalle({ ...nouvelleSalle, type: e.target.value })}>
              {TYPES_SALLE.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
            <button type="submit" className="btn btn-primary btn-sm" disabled={action}>Ajouter la salle</button>
          </form>
        )}
      </div>

      <div className="card">
        <h2>Lits</h2>
        <table className="table table-compact">
          <thead><tr><th>Numéro</th><th>Service</th><th>Occupé</th></tr></thead>
          <tbody>
            {lits.map((l) => (
              <tr key={l.id}>
                <td>{l.numero}</td>
                <td>{l.service}</td>
                <td>{l.occupe ? 'Oui' : 'Non'}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <p className="text-muted">L'occupation d'un lit est gérée automatiquement par le système lors d'une hospitalisation / fin de séjour.</p>
        {peutGerer && (
          <form className="form-inline" onSubmit={handleCreerLit} style={{ marginTop: '1rem' }}>
            <input placeholder="Numéro du lit" required value={nouveauLit.numero} onChange={(e) => setNouveauLit({ ...nouveauLit, numero: e.target.value })} />
            <select value={nouveauLit.service} onChange={(e) => setNouveauLit({ ...nouveauLit, service: e.target.value })}>
              {SERVICES_MEDICAUX.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
            <button type="submit" className="btn btn-primary btn-sm" disabled={action}>Ajouter le lit</button>
          </form>
        )}
      </div>
    </div>
  );
}