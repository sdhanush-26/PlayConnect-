# PlayConnect 🏏

A location-based platform that connects players to find, create, and join local sports matches with people of similar skill level.

## Status

🚧 In active development — Day 1 of a 60-day build plan. See [REQUIREMENTS.md](./REQUIREMENTS.md) for full project scope.

## Tech Stack

- **Frontend:** React.js
- **Backend:** Java 17 + Spring Boot
- **Database:** MySQL (Hibernate / Spring Data JPA)
- **Security:** Spring Security + JWT
- **Real-time:** WebSocket (STOMP)
- **Maps:** OpenStreetMap / Leaflet
- **Deployment:** Render/Railway (backend) + Vercel (frontend)

## Architecture

```
React (frontend)
   ↓
REST API
   ↓
Spring Boot
   ↓
Service Layer
   ↓
Repository Layer
   ↓
MySQL
```

## Repository Structure

```
playconnect/
├── playconnect-backend/     # Spring Boot API
├── playconnect-frontend/    # React app
├── docs/                    # ER diagrams, API docs, architecture notes
└── README.md
```

## Core Features (planned)

- 🔐 JWT authentication (register/login)
- 👤 Player profiles (sports, skill level, availability)
- 🏟️ Match creation, discovery, join/leave
- 📍 Nearby players & matches (Haversine distance search)
- 💬 Real-time match chat (WebSocket)
- 🔔 Notifications (invites, join requests, reminders)
- ⭐ Post-match ratings
- 🤝 Player recommendation scoring

## Getting Started

Setup instructions will be added once the backend and frontend scaffolds are created (Day 8 onward).

## License

TBD
