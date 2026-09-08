import { useEffect, useState } from 'react';
import { getMonProfil, modifierMonProfil, changerMaDisponibilite } from '../api/userApi';

export default function ProfilePage() {
  const [profil, setProfil] = useState(null);
  const [nom, setNom] = useState('');
  const [nouveauMotDePasse, setNouveauMotDePasse] = useState('');
  const [erreur, setErreur] = useState('');
  const [message, setMessage] = useState('');
  const [chargement, setChargement] = useState(true);
  const [action, setAction] = useState(false);

  useEffect(() => {
    getMonProfil()
      .then((data) => {
        setProfil(data);
        setNom(data.nom);
      })
      .catch(() => setErreur('Impossible de charger le profil.'))
      .finally(() => setChargement(false));
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    setErreur('');
    setMessage('');
    setAction(true);
    try {
      const payload = { nom };
      if (nouveauMotDePasse.trim() !== '') {
        payload.nouveauMotDePasse = nouveauMotDePasse;
      }
      const updated = await modifierMonProfil(payload);
      setProfil(updated);
      setNouveauMotDePasse('');
      setMessage('Profil mis à jour.');
    } catch (err) {
      setErreur(err.response?.data?.message || 'Erreur lors de la mise à jour du profil.');
    } finally {
      setAction(false);
    }
  }

  async function toggleDisponibilite() {
    setErreur('');
    setAction(true);
    try {
      const updated = await changerMaDisponibilite(!profil.disponible);
      setProfil(updated);
    } catch (err) {
      setErreur('Erreur lors du changement de disponibilité.');
    } finally {
      setAction(false);
    }
  }

  if (chargement) return <div className="page-container"><p>Chargement...</p></div>;

  return (
    <div className="page-container">
      <h1>Mon profil</h1>
      {erreur && <div className="alert alert-error">{erreur}</div>}
      {message && <div className="alert alert-success">{message}</div>}

      <div className="card">
        <h2>Informations</h2>
        <form className="form-grid" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input value={profil.email} disabled />
          </div>
          <div className="form-group">
            <label>Rôle</label>
            <input value={profil.role} disabled />
          </div>
          <div className="form-group form-group-full">
            <label>Nom complet</label>
            <input required value={nom} onChange={(e) => setNom(e.target.value)} />
          </div>
          <div className="form-group form-group-full">
            <label>Nouveau mot de passe (laisser vide pour ne pas changer)</label>
            <input type="password" minLength={6} value={nouveauMotDePasse} onChange={(e) => setNouveauMotDePasse(e.target.value)} />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={action}>Enregistrer</button>
          </div>
        </form>
      </div>

      {(profil.role === 'MEDECIN' || profil.role === 'LABO_IMAGERIE') && (
        <div className="card">
          <h2>Disponibilité</h2>
          <p className="text-muted">
            {profil.disponible
              ? 'Vous êtes actuellement marqué disponible — vous pouvez recevoir de nouvelles affectations.'
              : 'Vous êtes actuellement marqué indisponible.'}
          </p>
          <button className={`btn ${profil.disponible ? 'btn-outline' : 'btn-success'}`} onClick={toggleDisponibilite} disabled={action}>
            {profil.disponible ? 'Me marquer indisponible' : 'Me marquer disponible'}
          </button>
        </div>
      )}
    </div>
  );
}