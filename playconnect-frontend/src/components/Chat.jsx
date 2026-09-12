import { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from '../api/AuthContext';
import { messagesApi } from '../api/client';

function Chat({ matchId }) {
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState('');
  const [connected, setConnected] = useState(false);
  const clientRef = useRef(null);
  const bottomRef = useRef(null);

  // Load existing history once, then open a live connection for
  // anything sent from here on — same split as the backend's REST
  // (history) vs WebSocket (live) endpoints.
  useEffect(() => {
    messagesApi.getHistory(matchId).then(setMessages).catch(() => {});

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/match/${matchId}`, (frame) => {
          const newMessage = JSON.parse(frame.body);
          setMessages((prev) => [...prev, newMessage]);
        });
      },
      onDisconnect: () => setConnected(false),
    });

    client.activate();
    clientRef.current = client;

    // Deactivating on unmount closes the connection cleanly when the
    // user navigates away, rather than leaving a dangling socket open.
    return () => client.deactivate();
  }, [matchId]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  function handleSend(e) {
    e.preventDefault();
    if (!draft.trim() || !clientRef.current?.connected) return;

    clientRef.current.publish({
      destination: `/app/chat/${matchId}`,
      body: JSON.stringify({ senderId: user.userId, content: draft }),
    });
    setDraft('');
  }

  return (
    <div className="chat-box">
      <div className="chat-header">
        Match Chat {connected ? <span className="chat-status-live">● Live</span> : <span className="chat-status-offline">● Connecting...</span>}
      </div>
      <div className="chat-messages">
        {messages.length === 0 && <p className="empty-state">No messages yet. Say hello!</p>}
        {messages.map((m) => (
          <div key={m.id} className={m.senderId === user.userId ? 'chat-message mine' : 'chat-message'}>
            <span className="chat-sender">{m.senderName}</span>
            <span className="chat-content">{m.content}</span>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>
      <form onSubmit={handleSend} className="chat-input-row">
        <input
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          placeholder="Type a message..."
        />
        <button type="submit" disabled={!connected}>Send</button>
      </form>
    </div>
  );
}

export default Chat;
