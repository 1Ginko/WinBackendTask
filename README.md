# Win Backend Task

A small backend system built with Java 21 and Spring Boot.
It provides user registration and JWT authentication,
delegates text transformation to a separate internal service,
and stores processing history in PostgreSQL.

## Architecture

The system consists of three services:

- `auth-api` — registration, login, JWT validation, processing orchestration, and persistence.
- `data-api` — internal text transformation service protected by `X-Internal-Token`.
- `postgres` — stores users and processing history.

Request flow:

```text
Client
  |
  | Bearer JWT
  v
auth-api --------> data-api
  |                X-Internal-Token
  |
  v
PostgreSQL
```

## Technology Stack

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring Security
- JWT with HMAC SHA-256
- BCrypt password hashing
- Spring Data JPA and Hibernate
- PostgreSQL
- Flyway
- Gradle with Kotlin DSL
- Docker and Docker Compose

## Requirements

For Docker-based startup:

- Docker Desktop
- Docker Compose

For running the applications directly:

- Java 21

Gradle does not need to be installed separately because both applications include the Gradle Wrapper.

## Configuration

Create a local environment file from the provided template:

```bash
cp .env.example .env
```

Then replace the placeholder values in `.env`.

| Variable | Description |
|---|---|
| `POSTGRES_URL` | JDBC URL used when running `auth-api` directly |
| `POSTGRES_DB` | PostgreSQL database name |
| `POSTGRES_USER` | PostgreSQL user |
| `POSTGRES_PASSWORD` | PostgreSQL password |
| `DATA_API_URL` | URL used when running `auth-api` directly |
| `INTERNAL_TOKEN` | Shared token used for communication between the APIs |
| `JWT_SECRET` | Secret used to sign and validate JWTs; must contain at least 32 bytes |

## Running

Start the complete system:

```bash
docker compose up -d --build
```

Services are available at:

- `auth-api`: `http://localhost:8080`
- `data-api`: `http://localhost:8081`
- PostgreSQL: `localhost:5433`

Stop the system:

```bash
docker compose down
```

PostgreSQL data is preserved in the Docker volume.

## API Endpoints

| Service | Method | Path | Authorization |
|---|---|---|---|
| `auth-api` | `POST` | `/api/auth/register` | Public |
| `auth-api` | `POST` | `/api/auth/login` | Public |
| `auth-api` | `POST` | `/api/process` | Bearer JWT |
| `data-api` | `POST` | `/api/transform` | `X-Internal-Token` |

## API Usage

### Register

```bash
curl -i \
  -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "pass"
  }'
```

Successful response:

```text
HTTP/1.1 201 Created
```

### Login

```bash
curl -i \
  -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "pass"
  }'
```

Successful response:

```json
{
  "token": "<JWT>"
}
```

### Process Text

Replace `<JWT>` with the token returned by the login endpoint.

```bash
curl -i \
  -X POST http://localhost:8080/api/process \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "hello"
  }'
```

Successful response:

```json
{
  "result": "OLLEH"
}
```

## Tests

Run the `auth-api` tests:

```bash
./auth-api/gradlew -p auth-api test
```

Run the `data-api` tests:

```bash
./data-api/gradlew -p data-api test
```