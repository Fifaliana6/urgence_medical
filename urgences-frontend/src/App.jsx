import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import PrivateRoute from './components/PrivateRoute';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ReceptionPage from './pages/ReceptionPage';
import WaitingListPage from './pages/WaitingListPage';
import PatientTrackingPage from './pages/PatientTrackingPage';
import VisitDetailPage from './pages/VisitDetailPage';
import LaboPage from './pages/LaboPage';
import ProfilePage from './pages/ProfilePage';
import AdminUsersPage from './pages/AdminUsersPage';
import AdminAuditPage from './pages/AdminAuditPage';
import AdminResourcesPage from './pages/AdminResourcesPage';

function Home() {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'RECEPTIONIST') return <Navigate to="/accueil" replace />;
  if (user.role === 'LABO_IMAGERIE') return <Navigate to="/labo" replace />;
  if (user.role === 'ADMIN') return <Navigate to="/suivi" replace />;
  return <Navigate to="/attente" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <BrowserRouter>
          <Navbar />
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/" element={<PrivateRoute><Home /></PrivateRoute>} />

            {/* Accueil (créer patient/visite) : réceptionniste uniquement — c'est son action */}
            <Route path="/accueil" element={<PrivateRoute rolesAutorises={['RECEPTIONIST']}><ReceptionPage /></PrivateRoute>} />

            <Route path="/attente" element={<PrivateRoute rolesAutorises={['ADMIN', 'RECEPTIONIST', 'MEDECIN']}><WaitingListPage /></PrivateRoute>} />
            <Route path="/suivi" element={<PrivateRoute rolesAutorises={['ADMIN', 'RECEPTIONIST', 'MEDECIN']}><PatientTrackingPage /></PrivateRoute>} />
            <Route path="/visites/:id" element={<PrivateRoute rolesAutorises={['ADMIN', 'MEDECIN', 'RECEPTIONIST', 'LABO_IMAGERIE']}><VisitDetailPage /></PrivateRoute>} />

            {/* Laboratoire (saisir résultats) : labo uniquement — c'est son action */}
            <Route path="/labo" element={<PrivateRoute rolesAutorises={['LABO_IMAGERIE']}><LaboPage /></PrivateRoute>} />

            {/* Salles & Lits : lecture pour admin/réception/médecin, création réservée à l'admin (gérée dans la page) */}
            <Route path="/ressources" element={<PrivateRoute rolesAutorises={['ADMIN', 'RECEPTIONIST', 'MEDECIN']}><AdminResourcesPage /></PrivateRoute>} />

            <Route path="/profil" element={<PrivateRoute><ProfilePage /></PrivateRoute>} />
            <Route path="/admin/utilisateurs" element={<PrivateRoute rolesAutorises={['ADMIN']}><AdminUsersPage /></PrivateRoute>} />
            <Route path="/admin/audit" element={<PrivateRoute rolesAutorises={['ADMIN']}><AdminAuditPage /></PrivateRoute>} />
          </Routes>
        </BrowserRouter>
      </NotificationProvider>
    </AuthProvider>
  );
}