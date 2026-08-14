# PlayConnect — Project Requirements Document

**Day 1 Deliverable — Planning & Core Java Phase**

## 1. Overview

**Project Name:** PlayConnect

**One-line pitch:** A location-based platform that connects players to find, create, and join local sports matches with people of similar skill level.

## 2. Target Users

- Casual/amateur players who want to find pickup games nearby
- People new to an area looking to meet sports partners
- Small groups/organizers who want to fill open slots in a match

## 3. Problems Solved

- Hard to find enough players for a casual match on short notice
- No easy way to find people of similar skill level nearby
- Coordinating time/location/headcount happens over scattered WhatsApp groups instead of one place

## 4. Main Features (MVP Scope)

| # | Feature | Notes |
|---|---------|-------|
| 1 | User registration/login | JWT-secured, BCrypt password hashing |
| 2 | Player profile | Sports played, skill level, location, availability |
| 3 | Match creation | Sport, location, date/time, max players |
| 4 | Match discovery | Browse/search matches, join/leave |
| 5 | Nearby players & matches | Geolocation-based, Haversine distance |
| 6 | Real-time chat | Per match, WebSocket/STOMP |
| 7 | Notifications | Join requests, acceptance, reminders |
| 8 | Post-match ratings | 1–5 star, player-to-player |
| 9 | Recommendation score | Sport (30%) + Distance (30%) + Skill (20%) + Availability (20%) |

## 5. Online vs Offline Sports

- **Offline only for MVP** — in-person matches at a physical location.
- Online/virtual matches (e.g. remote chess-style) considered a stretch goal for post-Day-60.

## 6. Core Entities (preview — full ER diagram on Day 7)

`User`, `Sport`, `PlayerSport`, `Match`, `MatchPlayer`, `Message`, `Notification`, `Rating`, `Ground` (location)

## 7. Out of Scope for v1

To keep the 60-day timeline realistic, the following are explicitly excluded:

- Payments / paid matches
- Tournament brackets
- Video/voice calls
- Native push notifications (in-app/browser only)

## 8. Technology Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React.js |
| Backend | Java 17 + Spring Boot |
| Database | MySQL |
| ORM | Hibernate / Spring Data JPA |
| Security | Spring Security + JWT |
| API | REST |
| Real-time | WebSocket |
| Maps/Location | OpenStreetMap / Leaflet |
| Build | Maven |
| Testing | JUnit + Mockito |
| Version Control | Git + GitHub |
| Deployment | Render/Railway (backend) + Vercel (frontend) |

## 9. Success Criteria (Day 60)

- A user can register, build a profile, create a match, get discovered by nearby players, join a match, chat, and rate the match afterward — fully deployed and publicly accessible.
