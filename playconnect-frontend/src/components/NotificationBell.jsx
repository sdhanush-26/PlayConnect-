import { useState, useEffect, useRef } from 'react';
import { useAuth } from '../api/AuthContext';
import { notificationsApi } from '../api/client';

function NotificationBell() {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  async function loadUnreadCount() {
    try {
      const { count } = await notificationsApi.getUnreadCount(user.userId);
      setUnreadCount(count);
    } catch {
      // Silently ignore — a failed badge count shouldn't break the rest
      // of the navbar for the user.
    }
  }

  useEffect(() => {
    if (!user) return;
    loadUnreadCount();
    // Simple polling rather than a dedicated WebSocket topic for
    // notifications — chat (Day 53) already proved the real-time pattern;
    // notifications updating within 15s is an acceptable tradeoff for
    // not adding a second live connection just for a badge count.
    const interval = setInterval(loadUnreadCount, 15000);
    return () => clearInterval(interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user]);

  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  async function handleOpen() {
    setOpen((prev) => !prev);
    if (!open) {
      const data = await notificationsApi.getForUser(user.userId);
      setNotifications(data);
    }
  }

  async function handleMarkAllRead() {
    await notificationsApi.markAllAsRead(user.userId);
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    setUnreadCount(0);
  }

  if (!user) return null;

  return (
    <div className="notification-bell" ref={dropdownRef}>
      <button className="bell-button" onClick={handleOpen}>
        🔔
        {unreadCount > 0 && <span className="bell-badge">{unreadCount}</span>}
      </button>

      {open && (
        <div className="notification-dropdown">
          <div className="notification-dropdown-header">
            <span>Notifications</span>
            {unreadCount > 0 && (
              <button onClick={handleMarkAllRead} className="mark-all-read">Mark all read</button>
            )}
          </div>
          {notifications.length === 0 ? (
            <p className="empty-state">No notifications yet.</p>
          ) : (
            <ul className="notification-list">
              {notifications.map((n) => (
                <li key={n.id} className={n.read ? 'notification-item' : 'notification-item unread'}>
                  <p>{n.message}</p>
                  <span className="notification-time">
                    {new Date(n.createdAt).toLocaleString()}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

export default NotificationBell;
