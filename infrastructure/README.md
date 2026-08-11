# Decisify Infrastructure

This directory contains the shared local infrastructure used by Decisify services during development.

The stack provides PostgreSQL, Redis, Apache Kafka, MinIO, and ChromaDB. Application services are intentionally not included yet.

## Start

Run from the repository root:

```bash
docker compose -f infrastructure/docker-compose.yml up -d
```

If you want Compose to read the repository-level `.env` explicitly:

```bash
docker compose --env-file .env -f infrastructure/docker-compose.yml up -d
```

Check status:

```bash
docker compose -f infrastructure/docker-compose.yml ps
```

## Stop

```bash
docker compose -f infrastructure/docker-compose.yml down
```

To also remove all local infrastructure data:

```bash
docker compose -f infrastructure/docker-compose.yml down -v
```

> `down -v` permanently deletes the named Docker volumes.

## Endpoints

| Service | Host endpoint | Docker-network endpoint |
|---|---|---|
| PostgreSQL | `localhost:5432` | `postgres:5432` |
| Redis | `localhost:6379` | `redis:6379` |
| Kafka | `localhost:29092` | `kafka:9092` |
| MinIO API | `http://localhost:9000` | `http://minio:9000` |
| MinIO Console | `http://localhost:9001` | `http://minio:9001` |
| ChromaDB | `http://localhost:8000` | `http://chromadb:8000` |

Use host endpoints when a service runs directly on your machine. Use Docker-network endpoints when it runs inside the shared Docker network.

## PostgreSQL databases

The local PostgreSQL instance creates three databases:

- `auth_db`
- `decision_db`
- `document_db`

They are initialized by `postgres/init/01-create-databases.sql`.

## Kafka

Kafka runs as a single-node KRaft broker/controller, so ZooKeeper is not required.

Host clients use `localhost:29092`.

Containerized clients on `decisify-network` use `kafka:9092`.

## Shared network

All infrastructure containers join the named bridge network:

```text
decisify-network
```
