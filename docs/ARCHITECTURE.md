# PlayConnect — System Architecture

**Day 2 Deliverable — Planning & Core Java Phase**

## Layered architecture

```
React (Frontend)
      ↓  HTTP requests (JSON over REST)
REST API (Spring Boot Controllers)
      ↓
Service Layer (business logic)
      ↓
Repository Layer (Spring Data JPA)
      ↓
MySQL (Database)
```

## Layer responsibilities

| Layer | Responsibility | Should NOT contain |
|-------|----------------|---------------------|
| **Controller** | Handle HTTP requests/responses, map JSON to DTOs, set status codes | Business logic, direct DB access |
| **Service** | Business rules (e.g. "match can't exceed maxPlayers", "no duplicate joins") | HTTP-specific code |
| **Repository** | Database access via Spring Data JPA interfaces | Business logic |
| **Entity** | Java classes mapped 1:1 to database tables | Business logic |

## Why this separation matters

- Swapping MySQL for another database only touches the Repository layer.
- Rebuilding the frontend in a different framework only requires the REST API contract to stay stable — the backend is untouched.
- Business rules live in one place (Service), making them easy to test independently of HTTP or the database.

## Client-server communication

- Frontend and backend are fully decoupled — React never talks to MySQL directly.
- All communication happens over REST endpoints returning JSON.
- Planned real-time features (chat, notifications) will use WebSocket (STOMP) as a separate channel alongside REST.
