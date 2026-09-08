import { useState } from 'react';
import { rechercherPatients, creerPatient } from '../api/patientApi';
import { creerVisite } from '../api/visitApi';
import { useNavigate } from 'react-router-dom';

const NIVEAUX = ['CRITIQUE', 'ELEVEE', 'MOYENNE', 'FAIBLE'];

export default function ReceptionPage() {
  const [terme, setTerme] = useState('');
  const [resultats, setResultats] = useState([]);
  const [patientSelectionne, setPatientSelectionne] = useState(null);
  const [nouveauPatient, setNouveauPatient] = useState({ nom: '', prenom: '', dateNaissance: '', telephone: '', adresse: '' });
  const [modeCreation, setModeCreation] = useState(false);
  const [symptomes, setSymptomes] = useState('');
  const [niveauUrgence, setNiveauUrgence] = useState('MOYENNE');
  const [message, setMessage] = useState('');
  const [erreur, setErreur] = useState('');
  const [chargement, setChargement] = useState(false);
  const navigate = useNavigate();

  async function handleRecherche(e) {
    e.preventDefault();
    setErreur('');
    try {
      const data = await rechercherPatients(terme);
      setResultats(data);
    } catch (err) {
      setErreur('Erreur lors de la recherche du patient.');
    }
  }

  async function handleCreerPatient(e) {
    e.preventDefault();
    setErreur('');
    try {
      const patient = await creerPatient(nouveauPatient);
      setPatientSelectionne(patient);
      setModeCreation(false);
      setResultats([]);
    } catch (err) {
      setErreur('Erreur lors de la création du patient. Vérifiez les champs.');
    }
  }

  async function handleCreerVisite(e) {
    e.preventDefault();
    setErreur('');
    setMessage('');
    if (!patientSelectionne) {
      setErreur("Sélectionnez ou créez d'abord un patient.");
      return;
    }
    setChargement(true);
    try {
      const visite = await creerVisite({
        patientId: patientSelectionne.id,
        symptomes,
        niveauUrgence,
      });
      setMessage(`Visite #${visite.id} enregistrée pour ${patientSelectionne.prenom} ${patientSelectionne.nom}.`);
      setPatientSelectionne(null);
      setSymptomes('');
      setNiveauUrgence('MOYENNE');
      setTerme('');
      setTimeout(() => navigate('/attente'), 1200);
    } catch (err) {
      setErreur(err.response?.data?.message || "Erreur lors de l'enregistrement de la visite.");
    } finally {
      setChargement(false);
    }
  }

  return (
    <div className="page-container">
      <h1>Accueil et Triage</h1>

      {erreur && <div className="alert alert-error">{erreur}</div>}
      {message && <div className="alert alert-success">{message}</div>}

      <div className="card">
        <h2>1. Identifier le patient</h2>

        {!modeCreation ? (
          <>
            <form className="form-inline" onSubmit={handleRecherche}>
              <input
                type="text"
                placeholder="Rechercher par nom ou prénom..."
                value={terme}
                onChange={(e) => setTerme(e.target.value)}
              />
              <button type="submit" className="btn btn-secondary">Rechercher</button>
              <button type="button" className="btn btn-outline" onClick={() => setModeCreation(true)}>
                + Nouveau patient
              </button>
            </form>

            {resultats.length > 0 && (
              <ul className="result-list">
                {resultats.map((p) => (
                  <li key={p.id}>
                    <span>{p.prenom} {p.nom} {p.dateNaissance ? `— né(e) le ${p.dateNaissance}` : ''}</span>
                    <button className="btn btn-primary btn-sm" onClick={() => { setPatientSelectionne(p); setResultats([]); }}>
                      Sélectionner
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </>
        ) : (
          <form className="form-grid" onSubmit={handleCreerPatient}>
            <div className="form-group">
              <label>Nom *</label>
              <input required value={nouveauPatient.nom} onChange={(e) => setNouveauPatient({ ...nouveauPatient, nom: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Prénom *</label>
              <input required value={nouveauPatient.prenom} onChange={(e) => setNouveauPatient({ ...nouveauPatient, prenom: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Date de naissance</label>
              <input type="date" value={nouveauPatient.dateNaissance} onChange={(e) => setNouveauPatient({ ...nouveauPatient, dateNaissance: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Téléphone</label>
              <input value={nouveauPatient.telephone} onChange={(e) => setNouveauPatient({ ...nouveauPatient, telephone: e.target.value })} />
            </div>
            <div className="form-group form-group-full">
              <label>Adresse</label>
              <input value={nouveauPatient.adresse} onChange={(e) => setNouveauPatient({ ...nouveauPatient, adresse: e.target.value })} />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary">Créer le patient</button>
              <button type="button" className="btn btn-outline" onClick={() => setModeCreation(false)}>Annuler</button>
            </div>
          </form>
        )}

        {patientSelectionne && (
          <div className="patient-selected">
            ✅ Patient sélectionné : <strong>{patientSelectionne.prenom} {patientSelectionne.nom}</strong>
          </div>
        )}
      </div>

      <div className="card">
        <h2>2. Triage</h2>
        <form className="form-grid" onSubmit={handleCreerVisite}>
          <div className="form-group form-group-full">
            <label>Symptômes *</label>
            <textarea required rows={3} value={symptomes} onChange={(e) => setSymptomes(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Niveau d'urgence *</label>
            <select value={niveauUrgence} onChange={(e) => setNiveauUrgence(e.target.value)}>
              {NIVEAUX.map((n) => <option key={n} value={n}>{n}</option>)}
            </select>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={chargement || !patientSelectionne}>
              {chargement ? 'Enregistrement...' : 'Enregistrer la visite'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}