# Decisify

Decisify is an AI-assisted decision intelligence platform designed to help users make structured and explainable decisions.

Users can create decision cases, define alternatives and weighted criteria, attach supporting documents, review AI-extracted information, calculate rankings, and run what-if simulations before reaching a final decision.

The system is designed around a microservice architecture with separate services for authentication, decision management, document processing, GenAI capabilities, and simulations.

## Core Capabilities

- Create and manage decision cases
- Define alternatives and evaluation criteria
- Assign weights to decision criteria
- Calculate weighted scores and rankings
- Upload supporting documents
- Extract structured information from documents using AI
- Confirm, edit, or reject AI-extracted values
- Generate explainable recommendations
- Run what-if simulations
- Review previous decisions and results

## Architecture

Decisify uses a microservice-based architecture with both synchronous API communication and asynchronous event-driven processing.

The high-level request flow is:

```text
React SPA
    |
    | HTTPS
    v
  Nginx
    |
    | /api/*
    v
API Gateway
(Spring Cloud Gateway)
    |
    +--------------------+
    |         |          |
    v         v          v
 Auth     Decision    Document
Service    Service     Service
                      |
                      v
                    Kafka
                      |
                      v
                 GenAI Service

Decision Service <--> Simulation Service
```

The main backend services are:

- **API Gateway** — Routes client requests and handles cross-cutting concerns.
- **Auth Service** — Handles registration, authentication, and authorization.
- **Decision Service** — Manages decisions, alternatives, criteria, scoring, and recommendations.
- **Document Service** — Handles supporting documents, metadata, and document-processing workflows.
- **GenAI Service** — Performs AI-assisted extraction and explanation generation.
- **Simulation Service** — Executes what-if scenarios and evaluates alternative outcomes.

Infrastructure components include PostgreSQL, Apache Kafka, MinIO, ChromaDB, Redis, and Nginx.

For a more detailed description, see the [System Overview](docs/architecture/system-overview.md).

## Tech Stack

| Area | Technology |
|---|---|
| Frontend | React |
| Edge / Reverse Proxy | Nginx |
| API Gateway | Spring Cloud Gateway |
| Backend Services | Java, Spring Boot |
| AI Services | Python, FastAPI |
| Relational Database | PostgreSQL |
| Event Streaming | Apache Kafka |
| Object Storage | MinIO |
| Vector Store | ChromaDB |
| Cache / Rate Limiting | Redis |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |

## Data Model

The core domain revolves around a `Decision`.

A decision contains alternatives and weighted criteria. Criterion values are used to score alternatives, while supporting documents can provide additional information through AI-assisted extraction.

The main domain concepts include:

- User
- Decision
- Alternative
- Criterion
- CriterionValue
- Recommendation
- Document
- AIExtraction
- SimulationScenario
- SimulationResult

## Architecture Diagrams

Detailed UML and architecture diagrams are available in the project documentation:

- [Component Diagram](docs/architecture/component-diagram.png)
- [Class Diagram](docs/architecture/class-diagram.png)
- [Use Case Diagram](docs/architecture/use-case-diagram.png)
- [System Overview](docs/architecture/system-overview.md)

## Documentation

Additional project documentation:

- [Problem Statement](docs/spec/problem-statement.md)
- [System Specification](docs/spec/spec.md)
- [System Overview](docs/architecture/system-overview.md)

## Local Development

The complete application will be runnable locally using Docker Compose.

```bash
docker compose up
```

> The local infrastructure is currently under development. Detailed setup instructions will be added as the required infrastructure components are implemented.

## Project Structure

```text
decisify/
├── frontend/
├── services/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── decision-service/
│   ├── document-service/
│   ├── genai-service/
│   └── simulation-service/
├── infrastructure/
├── docs/
├── .github/
│   └── workflows/
└── README.md
```

## Project Status

Decisify is currently under active development.
