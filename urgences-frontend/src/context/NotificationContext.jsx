import { createContext, useContext, useEffect, useRef, useState, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { useAuth } from './AuthContext';

const NotificationContext = createContext(null);

export function NotificationProvider({ children }) {
  const { user, token } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const clientRef = useRef(null);

  const addNotification = useCallback((notif) => {
    setNotifications((prev) => [notif, ...prev].slice(0, 50));
  }, []);

  useEffect(() => {
    if (!user || !token) {
      if (clientRef.current) {
        clientRef.current.deactivate();
        clientRef.current = null;
      }
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(`/topic/role/${user.role}`, (message) => {
          addNotification(JSON.parse(message.body));
        });
        client.subscribe(`/topic/user/${user.userId}`, (message) => {
          addNotification(JSON.parse(message.body));
        });
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [user, token, addNotification]);

  const markAllRead = useCallback(() => {
    setNotifications((prev) => prev.map((n) => ({ ...n, lu: true })));
  }, []);

  return (
    <NotificationContext.Provider value={{ notifications, markAllRead }}>
      {children}
    </NotificationContext.Provider>
  );
}

export function useNotifications() {
  return useContext(NotificationContext);
}