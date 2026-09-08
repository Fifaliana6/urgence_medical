import { useState } from 'react';
import { useNotifications } from '../context/NotificationContext';

export default function NotificationBell() {
  const [ouvert, setOuvert] = useState(false);
  const { notifications, markAllRead } = useNotifications() || { notifications: [] };
  const nonLues = notifications.filter((n) => !n.lu).length;

  function toggle() {
    setOuvert((v) => !v);
    if (!ouvert) markAllRead();
  }

  return (
    <div className="notification-bell">
      <button className="btn btn-icon" onClick={toggle}>
        🔔 {nonLues > 0 && <span className="notification-count">{nonLues}</span>}
      </button>
      {ouvert && (
        <div className="notification-panel">
          {notifications.length === 0 && <p className="notification-empty">Aucune notification.</p>}
          {notifications.map((n, idx) => (
            <div key={idx} className="notification-item">
              <p>{n.message}</p>
              <span className="notification-date">
                {n.dateCreation ? new Date(n.dateCreation).toLocaleTimeString('fr-FR') : ''}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}