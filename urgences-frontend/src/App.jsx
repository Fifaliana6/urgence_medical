import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import QueuePage from './pages/QueuePage'
import TriagePage from './pages/TriagePage'
import PatientsPage from './pages/PatientsPage'
import VisitDetailPage from './pages/VisitDetailPage'
import ResourcesPage from './pages/ResourcesPage'
import InvoicesPage from './pages/InvoicesPage'
import AuditPage from './pages/AuditPage'

function ProtectedArea() {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <Layout /> : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<ProtectedArea />}>
            <Route path="/" element={<QueuePage />} />
            <Route path="/triage" element={<TriagePage />} />
            <Route path="/patients" element={<PatientsPage />} />
            <Route path="/visits/:id" element={<VisitDetailPage />} />
            <Route path="/resources" element={<ResourcesPage />} />
            <Route path="/invoices" element={<InvoicesPage />} />
            <Route path="/audit" element={<AuditPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
