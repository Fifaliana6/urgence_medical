import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerRequest } from '../api/authApi';

const ROLES = [
  { value: 'RECEPTIONIST', label: 'Réceptionniste' },
  { value: 'MEDECIN', label: 'Médecin' },
  { value: 'LABO_IMAGERIE', label: 'Laboratoire / Imagerie' },
];

export default function RegisterPage() {
  const [form, setForm] = useState({ nom: '', email: '', password: '', role: 'RECEPTIONIST' });
  const [erreur, setErreur] = useState('');
  const [succes, setSucces] = useState('');
  const [chargement, setChargement] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setErreur('');
    setSucces('');
    setChargement(true);
    try {
      await registerRequest(form);
      setSucces("Votre demande de compte a été envoyée. Un administrateur doit l'approuver avant que vous puissiez vous connecter.");
      setForm({ nom: '', email: '', password: '', role: 'RECEPTIONIST' });
      setTimeout(() => navigate('/login'), 2500);
    } catch (err) {
      setErreur(err.response?.data?.message || "Erreur lors de l'inscription.");
    } finally {
      setChargement(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Créer un compte</h1>
        <p className="auth-subtitle">Votre compte devra être validé par un administrateur.</p>

        {erreur && <div className="alert alert-error">{erreur}</div>}
        {succes && <div className="alert alert-success">{succes}</div>}

        <div className="form-group">
          <label>Nom complet</label>
          <input required value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        </div>

        <div className="form-group">
          <label>Mot de passe</label>
          <input type="password" required minLength={6} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        </div>

        <div className="form-group">
          <label>Rôle demandé</label>
          <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
            {ROLES.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
          </select>
        </div>

        <button type="submit" className="btn btn-primary btn-block" disabled={chargement}>
          {chargement ? 'Envoi...' : 'Créer le compte'}
        </button>

        <p className="auth-subtitle" style={{ marginTop: '1rem' }}>
          Déjà un compte ? <Link to="/login">Se connecter</Link>
        </p>
      </form>
    </div>
  );
}