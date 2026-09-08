import { useEffect, useState, useCallback } from 'react';
import { listerAudit } from '../api/adminApi';

export default function AdminAuditPage() {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState('');

  const charger = useCallback(async () => {
    setErreur('');
    try {
      const data = await listerAudit(page, 20);
      setLogs(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setErreur("Impossible de charger le journal d'audit.");
    } finally {
      setChargement(false);
    }
  }, [page]);

  useEffect(() => {
    setChargement(true);
    charger();
  }, [charger]);

  return (
    <div className="page-container">
      <h1>Journal d'audit</h1>
      {erreur && <div className="alert alert-error">{erreur}</div>}

      {chargement ? (
        <p>Chargement...</p>
      ) : logs.length === 0 ? (
        <p className="empty-state">Aucune action enregistrée.</p>
      ) : (
        <>
          <table className="table table-compact">
            <thead>
              <tr><th>Date</th><th>Utilisateur</th><th>Rôle</th><th>Action</th><th>Détails</th></tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td>{new Date(log.dateAction).toLocaleString('fr-FR')}</td>
                  <td>{log.utilisateurNom}</td>
                  <td>{log.utilisateurRole}</td>
                  <td>{log.action}</td>
                  <td>{log.details}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="form-inline" style={{ marginTop: '1rem' }}>
            <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>Précédent</button>
            <span className="text-muted">Page {page + 1} / {totalPages}</span>
            <button className="btn btn-outline btn-sm" disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)}>Suivant</button>
          </div>
        </>
      )}
    </div>
  );
}