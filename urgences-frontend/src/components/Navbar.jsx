import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import NotificationBell from './NotificationBell';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">🏥 Urgences</div>
      <div className="navbar-links">
        {user.role === 'RECEPTIONIST' && (
          <Link to="/accueil">Accueil</Link>
        )}
        {(user.role === 'ADMIN' || user.role === 'MEDECIN' || user.role === 'RECEPTIONIST') && (
          <>
            <Link to="/attente">Liste d'attente</Link>
            <Link to="/suivi">Suivi des patients</Link>
          </>
        )}
        {user.role === 'LABO_IMAGERIE' && (
          <Link to="/labo">Laboratoire / Imagerie</Link>
        )}
        {(user.role === 'ADMIN' || user.role === 'RECEPTIONIST' || user.role === 'MEDECIN') && (
          <Link to="/ressources">Salles & Lits</Link>
        )}
        {user.role === 'ADMIN' && (
          <>
            <Link to="/admin/utilisateurs">Utilisateurs</Link>
            <Link to="/admin/audit">Audit</Link>
          </>
        )}
        <Link to="/profil">Mon profil</Link>
      </div>
      <div className="navbar-user">
        <NotificationBell />
        <span className="navbar-username">{user.nom} · {user.role}</span>
        <button className="btn btn-outline" onClick={handleLogout}>Déconnexion</button>
      </div>
    </nav>
  );
}