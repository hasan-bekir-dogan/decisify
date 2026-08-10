# Decisify System Overview Architecture

## 1. Introduction

Decisify is an AI-assisted decision intelligence platform for comparing alternatives using explicit criteria, weighted scoring, supporting documents, simulations, and explainable recommendations.

The project is intentionally **backend-focused**. The client is a **React (Vite) single-page application**, built to a static bundle and served through Nginx. React was chosen over a hand-rolled vanilla-JS client to reflect how frontends are actually built in practice — component composition, client-side routing — without shifting the project's engineering identity away from backend engineering, distributed systems, event-driven communication, infrastructure, and CI/CD. The frontend technology choice does **not** determine whether the system is a microservice architecture — the backend services remain independently deployable regardless of what renders the UI, and the React app talks to the backend through the exact same REST contract any other client would use.

The architecture combines a microservice style with event-driven processing, synchronous REST for user-facing operations, and containerized deployment.


## 2. Initial System Structure

The system is divided into the following core layers: client, edge (Nginx), API Gateway, backend microservices, and a data/infrastructure layer.


## 3. Client Layer

### React Single-Page Application

The client is a React (Vite) SPA, built to a static `dist/` bundle and served by Nginx. React Router handles client-side routing between pages — there is no server-side rendering anywhere in this layer; Spring Boot and FastAPI services never return HTML, only JSON.

Pages (routes):

- `/login`, `/register` — authentication
- `/dashboard` — list and create decision cases
- `/decisions/:id` — define alternatives, criteria, weights; upload supporting documents; review AI-extracted values and accept/edit/reject them
- `/decisions/:id/results` — view rankings, recommendations, AI-generated explanations, and run what-if simulations

Structure:

```text
src/
├── api/client.js            shared Axios/Fetch client + auth-token interceptor
├── context/AuthContext.jsx  logged-in user state, token storage
├── pages/                   one component per route (Login, Register, Dashboard, DecisionBuilder, Results)
└── components/              reusable pieces (DocumentUpload, SimulationPanel, RankingTable, ...)
```

All backend calls go through the shared `api/client.js` module rather than duplicating request logic across components. State management stays intentionally light — React Context and hooks (`useState`/`useEffect`) are sufficient for this scope; no Redux/Zustand. The frontend renders, validates, and presents — it does **not** own business-critical scoring logic.


## 4. Edge Layer — Nginx

Nginx has two responsibilities:

1. Serve the built React `dist/` bundle as static assets, with an SPA fallback (`try_files $uri /index.html;`) so client-side routes like `/decisions/42` resolve correctly on a hard refresh.
2. Reverse proxy `/api/*` requests to the API Gateway.

```text
Browser
 ├── /assets/*      ───────► Nginx static files (JS/CSS bundle)
 ├── /decisions/42  ───────► Nginx → falls back to index.html → React Router takes over
 └── /api/*         ───────► API Gateway
```

Internal service addresses are never exposed to the browser — the frontend only ever talks to Nginx.


## 5. API Gateway Layer

### API Gateway — Spring Boot / Spring Cloud Gateway

The API Gateway is the single entry point into the backend system.

Responsibilities:

- route requests to internal microservices
- validate JWT access tokens
- apply global CORS configuration
- enforce Redis-backed rate limiting
- centralize request logging
- hide internal service URLs from the client

Routes:

```text
/api/auth/**          → Auth Service
/api/decisions/**     → Decision Service
/api/documents/**     → Document Service
/api/ai/**            → GenAI Service
/api/simulations/**   → Simulation Service
```

The gateway does not contain core domain business logic.


## 6. Backend Microservices

### Auth Service — Spring Boot, Spring Security

Responsibilities:

- registration and login
- password hashing (BCrypt)
- JWT generation and refresh
- user identity (`GET /auth/me`)

Passwords are never stored in plaintext.


### Decision Service — Spring Boot

The core domain service of the platform.

Responsibilities:

- decisions, alternatives, criteria, weights
- criterion values and normalization
- weighted scoring and rankings
- recommendation persistence
- decision history

```text
User
 └── Decision
      ├── Alternatives
      ├── Criteria
      ├── CriterionValues
      └── Recommendation
```

Scoring formula:

```text
Alternative Score = Σ(normalized criterion score × criterion weight)
```

Normalization accounts for differing units and directions (e.g. salary — higher is better; commute time — lower is better), so each criterion carries a `higherIsBetter` flag.


### Document Service — Spring Boot

Responsibilities:

- file upload and validation
- MinIO storage
- metadata persistence in PostgreSQL
- decision/document association
- Kafka event publication (`document.uploaded`)

AI processing never blocks the original upload request — the document is stored and an event is emitted so extraction happens asynchronously.


### GenAI Service — Python, FastAPI

Responsibilities:

- consume document-processing events from Kafka
- retrieve files from MinIO
- extract decision-relevant factors from documents
- chunk documents and create embeddings
- store embeddings in ChromaDB
- interact with an LLM to produce structured extraction
- generate explanations for recommendations
- optionally support RAG-based question answering

Example extraction output:

```json
{
  "salary": {"value": 72000, "currency": "EUR", "confidence": 0.97},
  "vacationDays": {"value": 30, "confidence": 0.95},
  "remoteWork": {"value": "HYBRID", "confidence": 0.91}
}
```

AI output is never automatically trusted — the user accepts, edits, or rejects extracted values before they affect a recommendation.


### Simulation Service — Python, FastAPI

Runs analytical "what-if" scenarios without permanently modifying the original decision, and returns original vs. simulated rankings. The MVP performs deterministic what-if analysis; sensitivity analysis and Monte Carlo simulation are later extensions.


## 7. Data and Infrastructure Layer

### PostgreSQL — data ownership per service

A single PostgreSQL server hosts separate databases/schemas per service so no service directly modifies another service's tables:

```text
PostgreSQL Server
 ├── auth_db       → Auth Service      (users)
 ├── decision_db   → Decision Service  (decisions, alternatives, criteria,
 │                                      criterion_values, recommendations,
 │                                      simulation_scenarios, simulation_results)
 └── document_db   → Document Service  (documents, ai_extractions)
```

### MinIO

S3-compatible object storage for uploaded binary files (PDF/DOCX/TXT). PostgreSQL stores only metadata: `document_id`, `decision_id`, `original_filename`, `content_type`, `object_key`, `size`, `status`, `created_at`.

### ChromaDB

Vector database for document embeddings and semantic chunks used in retrieval/RAG. ChromaDB never replaces PostgreSQL for core transactional data.

### Apache Kafka

Asynchronous event-driven communication, used only where it has a real benefit (long-running document/AI processing). Normal CRUD stays REST-based.

Initial events:

```text
document.uploaded
document.processing.started
document.processing.completed
document.processing.failed
```

Flow:

```text
Document Service → Kafka → GenAI Service → Kafka → Decision Service
```

### Redis

- API Gateway rate limiting (initial, justified use case)
- short-lived caching of frequently requested computed results
- temporary state where needed


## 8. Main Application Flow

1. User registers or logs in.
2. Frontend sends requests to Nginx, which proxies `/api/*` to the API Gateway.
3. The API Gateway validates the JWT and routes the request.
4. The user creates a decision case, adds alternatives, and defines criteria with weights.
5. The user enters values manually and/or uploads supporting documents.
6. The Document Service stores files in MinIO and publishes `document.uploaded` to Kafka.
7. The GenAI Service consumes the event, retrieves the file, extracts decision factors, and stores embeddings in ChromaDB.
8. The user reviews AI-extracted values and confirms, edits, or rejects them.
9. The Decision Service normalizes values and calculates weighted scores.
10. The user views the ranking and recommendation.
11. The user optionally runs what-if simulations via the Simulation Service.
12. The GenAI Service generates an explanation for the recommendation.
13. The decision is saved and can be revisited later (decision history).


## 9. Architectural Rules

1. Frontend never connects directly to internal microservices — only through Nginx and the API Gateway.
2. Business logic does not belong in the API Gateway.
3. Business-critical scoring is performed on the backend.
4. AI output is not automatically trusted.
5. Long-running document/AI processing is asynchronous.
6. Kafka is used only when asynchronous communication is justified.
7. Each service owns its domain data.
8. MinIO stores files; PostgreSQL stores structured metadata.
9. ChromaDB stores vectors, not core transactional data.
10. Infrastructure complexity is added incrementally.


## 10. Why This Architecture Is Suitable

Responsibilities are separated clearly: authentication is isolated, decision logic is centralized, document handling is independent, AI processing is asynchronous, and simulations are separated from core business logic. Storage systems are selected based on data shape (relational vs. object vs. vector). The GenAI and Simulation services can scale independently since they perform the most computationally expensive work.


## 11. Technology Stack

| Layer | Technology |
|---|---|
| Frontend | React (Vite), React Router, Axios/Fetch, CSS3 |
| Edge | Nginx |
| API Gateway | Spring Boot / Spring Cloud Gateway |
| Auth Service | Spring Boot, Spring Security, JWT, BCrypt |
| Decision Service | Spring Boot |
| Document Service | Spring Boot |
| GenAI Service | Python, FastAPI, LLM integration |
| Simulation Service | Python, FastAPI |
| Database | PostgreSQL (per-service databases) |
| Object Storage | MinIO |
| Vector Database | ChromaDB |
| Messaging | Apache Kafka |
| Cache / Rate Limiting | Redis |
| Deployment | Docker, Docker Compose, Kubernetes later |


## 12. Planned Extensions

- Kubernetes deployment (Deployments, Services, ConfigMaps, Secrets, Ingress, health/readiness probes)
- Prometheus and Grafana monitoring, Spring Boot Actuator, structured logs
- OpenAPI documentation
- advanced sensitivity/Monte Carlo simulation
- Playwright E2E coverage for critical frontend flows
- CD pipeline (versioned images, registry push, automated deploy) once CI is reliable


## 13. What the Project Demonstrates

- **Backend Engineering** — Java, Spring Boot, Spring Security, REST, domain modeling, PostgreSQL, testing.
- **Distributed Systems** — microservices, API Gateway, service boundaries, Kafka, asynchronous processing, event-driven architecture.
- **Infrastructure** — Docker, Docker Compose, Nginx, Redis, MinIO, GitHub Actions CI/CD, Kubernetes, Prometheus/Grafana.
- **AI Engineering** — Python, FastAPI, LLM integration, structured extraction, embeddings, RAG, vector storage, explainable recommendations.
- **Frontend** — React, component architecture, client-side routing, REST integration.

The project's primary engineering identity remains backend and distributed systems, while the lightweight frontend makes it a complete, demoable end-to-end product.
