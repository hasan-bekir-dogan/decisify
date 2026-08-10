## 1. Overview

**Decisify** is a decision intelligence platform that helps users make complex, data-driven decisions by combining structured inputs, unstructured data, and AI-powered reasoning. The system aggregates multiple data sources, extracts decision factors, evaluates trade-offs, runs scenario simulations, and produces explainable recommendations.

See [problem-statement.md](./problem-statement.md) for the full motivation, target users, and example scenarios.


## 2. Core Features

### 2.1 Decision Creation
- Create decision cases (e.g. "Job A vs Job B")
- Define two or more alternatives
- Define criteria (salary, risk, growth, etc.) with weights and a `higherIsBetter` direction

### 2.2 Data Ingestion
- Enter criterion values manually
- Upload supporting documents (PDF/DOCX/TXT)
- Extract structured signals from documents using GenAI

### 2.3 AI Factor Extraction
- Extract entities (salary, location, benefits, …) with a confidence score per value
- Normalize into comparable metrics
- User must accept, edit, or reject each extracted value before it affects scoring

### 2.4 Decision Engine
- Normalize criterion values (unit- and direction-aware)
- Score alternatives: `Alternative Score = Σ(normalized criterion score × criterion weight)`
- Produce ranked outcomes and persist the recommendation

### 2.5 Simulation Engine
- Run "what-if" scenarios without mutating the original decision
- Return original vs. simulated rankings
- MVP: deterministic what-if analysis. Later: sensitivity analysis, Monte Carlo simulation

### 2.6 Explainable AI
- Generate reasoning behind a recommendation
- Surface trade-offs and confidence levels

### 2.7 Decision History
- Store past decisions and track changes over time


## 3. Domain Entities

```text
users                  (auth_db)

decisions              (decision_db)
alternatives           (decision_db)
criteria               (decision_db)
criterion_values       (decision_db)
recommendations        (decision_db)
simulation_scenarios   (decision_db)
simulation_results     (decision_db)

documents              (document_db)
ai_extractions         (document_db)
```

Each service owns its schema; a service never writes to another service's tables. See the [class diagram](../uml/class-diagram.png) for attributes and relationships.


## 4. API Surface

### Gateway routing

```text
/api/auth/**          → Auth Service
/api/decisions/**     → Decision Service
/api/documents/**     → Document Service
/api/ai/**            → GenAI Service
/api/simulations/**   → Simulation Service
```

### Auth Service

```text
POST /auth/register
POST /auth/login
POST /auth/refresh
GET  /auth/me
```

### Decision Service

```text
POST   /decisions
GET    /decisions
GET    /decisions/{id}
PUT    /decisions/{id}
DELETE /decisions/{id}
POST   /decisions/{id}/alternatives
POST   /decisions/{id}/criteria
POST   /decisions/{id}/values
POST   /decisions/{id}/calculate
GET    /decisions/{id}/recommendation
```

### Document Service

```text
POST /documents                    (upload, associates with a decision)
GET  /documents/{id}
```

### GenAI Service

```text
GET /ai/extractions/{documentId}   (poll/read extraction result)
POST /ai/explanations              (generate explanation for a recommendation)
```

### Simulation Service

```text
POST /simulations                  (run a what-if scenario for a decision)
GET  /simulations/{id}
```


## 5. Non-Functional Requirements

- **Scalability**: microservices architecture; GenAI and Simulation services scale independently as the most compute-heavy components
- **Performance**: long-running document/AI processing is asynchronous (Kafka) and never blocks the HTTP upload request
- **Security**: JWT-based authentication, BCrypt password hashing, passwords never stored in plaintext, internal services hidden behind the Gateway
- **Observability**: structured logs, Spring Boot Actuator, Prometheus/Grafana (post-MVP)
- **Reliability**: idempotent Kafka consumers for event processing
- **Data ownership**: each service owns its own database; no cross-service table writes


## 6. Technology Stack

### Backend
- Java (Spring Boot) → API Gateway, Auth, Decision, Document services
- Python (FastAPI) → GenAI, Simulation services

### Frontend
- React (Vite), React Router, Axios/Fetch, CSS3 (component-scoped stylesheets), built to a static bundle and served by Nginx

### Infrastructure
- PostgreSQL → relational data (per-service databases)
- MinIO → file storage
- ChromaDB → embeddings / vector search
- Redis → caching / rate limiting
- Apache Kafka → event-driven communication
- Docker / Docker Compose → local and deployable images; Kubernetes as a later milestone

### AI
- LLM integration (cloud and/or local models)
- Structured extraction, embeddings, optional RAG


## 7. Repository Structure

```text
decisify/
├── frontend/
│   ├── index.html
│   ├── vite.config.js
│   ├── package.json
│   ├── public/
│   │   └── assets/
│   └── src/
│       ├── main.jsx
│       ├── App.jsx
│       ├── api/
│       │   └── client.js
│       ├── context/
│       │   └── AuthContext.jsx
│       ├── pages/
│       │   ├── Login.jsx
│       │   ├── Register.jsx
│       │   ├── Dashboard.jsx
│       │   ├── DecisionBuilder.jsx
│       │   └── Results.jsx
│       ├── components/
│       │   ├── DocumentUpload.jsx
│       │   └── SimulationPanel.jsx
│       └── styles/
│           ├── global.css
│           └── components.css
├── services/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── decision-service/
│   ├── document-service/
│   ├── genai-service/
│   └── simulation-service/
├── infrastructure/
│   ├── docker-compose.yml
│   ├── nginx/nginx.conf
│   ├── kafka/
│   ├── postgres/
│   ├── redis/
│   ├── minio/
│   └── monitoring/
├── docs/
│   ├── spec/
│   ├── architecture/
│   ├── uml/
│   ├── api/
│   └── decisions/
├── .github/workflows/
├── .env.example
├── .gitignore
└── README.md
```


## 8. Communication Rules

### Synchronous REST
Used when an immediate result is required: Frontend → Gateway, Gateway → Auth Service, Gateway → Decision Service, Gateway → Simulation Service.

### Asynchronous Kafka
Used for background workflows: document processing, AI extraction, other long-running work, completion/failure notifications.

Kafka events:

```text
document.uploaded
document.processing.started
document.processing.completed
document.processing.failed
```


## 9. Authentication Flow

```text
Browser → Nginx → API Gateway → Auth Service
  (validate credentials, issue JWT)

Browser → Nginx → API Gateway (validate JWT) → Internal Service
```

For production, prefer a secure cookie/token strategy over storing long-lived credentials in `localStorage`.


## 10. Testing Strategy

### Java
- JUnit 5, Mockito, Spring Boot Test, Testcontainers where useful
- Focus: domain logic, scoring, normalization, controllers, repositories, auth, Kafka integration

### Python
- pytest, FastAPI testing utilities
- Focus: simulations, extraction parsing, API endpoints, failure modes

### Frontend
- Vitest + React Testing Library for component/unit tests; Playwright can provide browser-level E2E coverage for critical flows later


## 11. CI/CD

### Continuous Integration
Every pull request eventually runs: checkout → compile Java services → run Java tests → run Python tests → run frontend checks → build Docker images. Broken CI blocks merging.

### Continuous Delivery/Deployment (later)
Merge to main → CI passes → build versioned images → push to registry → deploy. Build reliable CI before complicated CD.


## 12. Docker

Deployable images: `frontend`, `api-gateway`, `auth-service`, `decision-service`, `document-service`, `genai-service`, `simulation-service`.

Infrastructure containers: `postgres`, `redis`, `kafka`, `minio`, `chromadb`.

Local target: `docker compose up`.


## 13. Development Phases

1. **Foundation** — repository, documentation, Git workflow, Docker foundations, CI skeleton
2. **Core Decision Domain** — create decision → alternatives → criteria → weights → values → score → ranking (no AI yet)
3. **Authentication** — registration, login, authorization, protected APIs
4. **Frontend Integration** — React (Vite) SPA, component-based, Axios/Fetch-based API integration, client-side routing
5. **Documents** — upload, MinIO, metadata, Kafka
6. **GenAI** — Kafka consumer, extraction, structured output, user confirmation, embeddings/RAG where justified
7. **Simulation** — what-if scenarios, recalculation, comparison, later sensitivity analysis
8. **Infrastructure** — Redis, complete Docker Compose, health checks, configuration/secrets
9. **Quality** — unit/integration tests, CI improvements, error handling, security review
10. **Deployment** — registry, deployment pipeline, monitoring, Kubernetes if justified

Recommended implementation order: Decision Service → PostgreSQL → scoring algorithm → basic HTML/JS client → Auth Service → API Gateway → Document Service → MinIO → Kafka → GenAI Service → Simulation Service → Redis → Dockerize → CI → Monitoring → CD → Kubernetes. Avoid standing up six empty services on day one — keep the project executable while the architecture grows incrementally.


## 14. Architectural Rules

1. Frontend never connects directly to internal microservices.
2. Business logic does not belong in the API Gateway.
3. Business-critical scoring is performed on the backend.
4. AI output is not automatically trusted.
5. Long-running document/AI processing is asynchronous.
6. Kafka is used only when asynchronous communication is justified.
7. Each service owns its domain data.
8. MinIO stores files; PostgreSQL stores structured metadata.
9. ChromaDB stores vectors, not core transactional data.
10. Infrastructure complexity is added incrementally.

For every technology in Decisify you should be able to answer: *what problem does it solve, why was it selected, how does it interact with the rest of the system, and what trade-offs does it introduce?* If a technology can't be justified that way, it doesn't belong in the stack merely for the CV.


## 15. Related Documents

- [System Overview](../architecture/system-overview.md)
- [Problem Statement](./problem-statement.md)
- [Class Diagram](../uml/class-diagram.png)
- [Component Diagram](../uml/component-diagram.png)
- [Use Case Diagram](../uml/use-case-diagram.png)
