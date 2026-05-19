<div align="center">

# 🏎️ Garage Premium — Backend

**REST API for managing a premium and sports car rental fleet.**

Stateless backend powering an admin panel and a public catalog. Built with Spring Boot 4 on Java 25, secured with JWT, and integrated with Cloudinary for media storage.

[![Java](https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-CDN-3448C5?logo=cloudinary&logoColor=white)](https://cloudinary.com)
[![Swagger](https://img.shields.io/badge/OpenAPI-Swagger%20UI-85EA2D?logo=swagger&logoColor=black)](https://swagger.io)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

[Frontend repo](https://github.com/BryanStrk/alquiler-coches-frontend) · [Features](#-features) · [Architecture](#-architecture) · [Getting started](#-getting-started)

</div>

---

## 📑 Table of contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project structure](#-project-structure)
- [Domain model](#-domain-model)
- [API endpoints](#-api-endpoints)
- [Authentication & authorization](#-authentication--authorization)
- [Media upload pipeline](#-media-upload-pipeline)
- [Error handling](#-error-handling)
- [Getting started](#-getting-started)
- [Environment variables](#-environment-variables)
- [Database setup](#-database-setup)
- [Running the application](#-running-the-application)
- [API documentation (Swagger)](#-api-documentation-swagger)
- [Seeded data](#-seeded-data)
- [Conventions](#-conventions)
- [Architectural decisions (ADRs)](#-architectural-decisions-adrs)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## 📖 Overview

**Garage Premium API** is the server-side component of a full-stack car rental application. It exposes a stateless REST interface consumed by the [frontend repo](https://github.com/BryanStrk/alquiler-coches-frontend) and handles all business logic, persistence, authentication and third-party integrations.

The project was built as the final assignment of the **FP Superior DAW** vocational program (2025–2026) and is used as reference material for teaching backend architecture in the **Desarrollo Web en Entorno Servidor (DWES)** module.

Two design principles drive every decision:

1. **The client never holds secrets.** Cloudinary credentials, JWT signing keys and database passwords are read from environment variables and live only on the server.
2. **Layered architecture with clear boundaries.** Controllers don't talk to repositories. Services don't return entities. DTOs are immutable records.

---

## ✨ Features

- 🚗 **Car CRUD** with multi-image support and availability state machine.
- 🔐 **JWT-based authentication** with stateless sessions and BCrypt-hashed passwords.
- 🛡️ **Role-based authorization** (`ADMIN`, `CLIENT`) enforced declaratively with `@PreAuthorize`.
- ☁️ **Cloudinary integration** for image upload, optimization and deletion, mediated server-side.
- 🌐 **CORS configuration** scoped to the frontend dev/prod origins.
- 📖 **OpenAPI 3 / Swagger UI** auto-generated documentation at `/swagger-ui.html`.
- 🚨 **Centralized exception handling** with `ProblemDetail` (RFC 7807) responses.
- 🌱 **Seed data** loaded on startup for local development (admin + client users, 6 sample cars).
- 🛂 **Null-safety** with JSpecify annotations (`@NullMarked`) at the package level.
- 🧪 **Bean Validation** on all request DTOs (`@NotBlank`, `@Positive`, `@Pattern`, etc.).

---

## 🛠️ Tech stack

| Layer              | Technology                                                          |
|--------------------|---------------------------------------------------------------------|
| Language           | [Java 25](https://openjdk.org/projects/jdk/25/)                     |
| Framework          | [Spring Boot 4.0](https://spring.io/projects/spring-boot)           |
| Persistence        | [Spring Data JPA](https://spring.io/projects/spring-data-jpa) over [Hibernate](https://hibernate.org) |
| Database           | [MySQL 8](https://www.mysql.com)                                    |
| Security           | [Spring Security](https://spring.io/projects/spring-security) + [JJWT 0.12](https://github.com/jwtk/jjwt) |
| Media              | [Cloudinary Java SDK](https://cloudinary.com/documentation/java_integration) (`cloudinary-http5`) |
| API documentation  | [springdoc-openapi](https://springdoc.org)                          |
| Validation         | [Jakarta Bean Validation](https://beanvalidation.org)               |
| Null-safety        | [JSpecify](https://jspecify.dev)                                    |
| Boilerplate        | [Lombok](https://projectlombok.org)                                 |
| Build              | [Maven](https://maven.apache.org)                                   |

---

## 🏛️ Architecture

The project follows a **strict layered architecture** with one-way dependencies:

```
┌──────────────────────────────────────────────────────────────┐
│                       HTTP Client                            │
└─────────────────────────────┬────────────────────────────────┘
                              │
                              ▼
                  ┌──────────────────────┐
                  │     Controller       │  ← @RestController, DTOs in/out
                  │   (REST endpoints)   │
                  └──────────┬───────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │       Service        │  ← Business logic, transactions
                  │   (interface impl)   │
                  └──────────┬───────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │     Repository       │  ← Spring Data JPA
                  │   (data access)      │
                  └──────────┬───────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │      Database        │  ← MySQL via Hibernate
                  └──────────────────────┘
```

**Key rules:**

- Controllers receive and return **DTOs**, never entities.
- Services depend on **interfaces**, not concrete repositories or other services.
- Entities live only inside services and repositories — they never cross HTTP boundaries.
- Exceptions are thrown as **custom domain exceptions** and translated to HTTP responses by `GlobalExceptionHandler`.

---

## 📂 Project structure

```
alquiler-coches-app/
├── src/main/java/com/alquiler/coches/
│   ├── AlquilerCochesApplication.java   # Spring Boot entry point
│   │
│   ├── bootstrap/
│   │   └── DataInitializer.java         # CommandLineRunner: seeds users & cars on startup
│   │
│   ├── config/
│   │   ├── CloudinaryConfig.java        # @Bean Cloudinary with externalized credentials
│   │   ├── CorsConfig.java              # WebMvcConfigurer with allowed origin
│   │   ├── JwtAuthenticationFilter.java # OncePerRequestFilter: extracts and validates JWT
│   │   ├── JwtUtil.java                 # Token generation, parsing, expiration checks
│   │   ├── OpenApiConfig.java           # Swagger metadata + Bearer security scheme
│   │   └── SecurityConfig.java          # SecurityFilterChain (stateless, role-based)
│   │
│   ├── controller/
│   │   ├── AuthController.java          # POST /api/auth/login, /register
│   │   ├── CocheController.java         # CRUD on /api/coches
│   │   └── MediaController.java         # POST/DELETE on /api/media
│   │
│   ├── dto/
│   │   ├── AuthResponseDTO.java         # { token, role, username }
│   │   ├── CocheRequestDTO.java         # Validated input for create/update
│   │   ├── CocheResponseDTO.java        # Sanitized output (no internal fields)
│   │   ├── DeleteResponse.java          # { deleted: boolean, publicId }
│   │   ├── ErrorResponse.java           # Used by GlobalExceptionHandler
│   │   ├── UploadResponse.java          # { publicId, url, format, bytes }
│   │   ├── UsuarioLoginDTO.java         # Login payload
│   │   └── UsuarioRegisterDTO.java      # Registration payload
│   │
│   ├── entity/
│   │   ├── Coche.java                   # JPA @Entity, @ElementCollection for imageUrls
│   │   ├── EstadoCoche.java             # Enum: DISPONIBLE, ALQUILADO, MANTENIMIENTO
│   │   ├── Role.java                    # Enum: ADMIN, CLIENT
│   │   ├── TipoCombustible.java         # Enum: GASOLINA, DIESEL, ELECTRICO, HIBRIDO
│   │   └── Usuario.java                 # JPA @Entity with BCrypt-hashed password
│   │
│   ├── exception/
│   │   ├── CocheNotFoundException.java
│   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice -> ProblemDetail
│   │   ├── MediaStorageException.java   # Wraps Cloudinary SDK failures
│   │   └── UsuarioNotFoundException.java
│   │
│   ├── repository/
│   │   ├── CocheRepository.java         # JpaRepository<Coche, Long> + custom queries
│   │   └── UsuarioRepository.java       # JpaRepository<Usuario, Long>
│   │
│   └── service/
│       ├── AuthService.java             # Login/register, JWT issuance
│       ├── CloudinaryService.java       # Interface
│       ├── CloudinaryServiceImpl.java   # Upload/delete with UUID public_id, folder isolation
│       ├── CocheService.java            # Interface
│       └── CocheServiceImpl.java        # Business logic, mapping entity <-> DTO
│
├── src/main/resources/
│   └── application.properties           # Externalized config (uses ${VAR} placeholders)
│
├── src/test/java/com/alquiler/coches/
│   └── AlquilerCochesApplicationTests.java
│
├── .gitignore
├── pom.xml                              # Maven dependencies, build plugins
└── README.md
```

---

## 🧬 Domain model

```
┌─────────────────────────────────┐
│            Usuario              │
├─────────────────────────────────┤
│ id: Long                        │
│ username: String (unique)       │
│ password: String (BCrypt hash)  │
│ email: String                   │
│ role: Role (ADMIN | CLIENT)     │
└─────────────────────────────────┘

┌─────────────────────────────────┐         ┌─────────────────────────┐
│             Coche               │  1   N  │     coche_imagenes      │
├─────────────────────────────────┤────────│ (@ElementCollection)    │
│ id: Long                        │         ├─────────────────────────┤
│ marca: String                   │         │ coche_id: Long (FK)     │
│ modelo: String                  │         │ image_url: String       │
│ matricula: String (unique)      │         └─────────────────────────┘
│ anyo: Integer                   │
│ precioPorDia: BigDecimal        │
│ kilometros: Integer             │
│ combustible: TipoCombustible    │
│ estado: EstadoCoche             │
│ descripcion: String             │
│ imageUrls: List<String>         │
└─────────────────────────────────┘
```

`imageUrls` uses `@ElementCollection` instead of a separate `Imagen` entity because images are simple URLs with no own behavior or queries. This keeps the schema small and avoids JOIN overhead on the common path.

---

## 🌐 API endpoints

All endpoints are documented with OpenAPI 3 and browsable via Swagger UI at `/swagger-ui.html`.

### Authentication

| Method | Path                  | Auth   | Description                          |
|--------|-----------------------|--------|--------------------------------------|
| POST   | `/api/auth/login`     | Public | Returns `{ token, role, username }`  |
| POST   | `/api/auth/register`  | Public | Creates a `CLIENT` user              |

### Cars

| Method | Path                          | Auth   | Description                             |
|--------|-------------------------------|--------|-----------------------------------------|
| GET    | `/api/coches`                 | Public | List all cars                           |
| GET    | `/api/coches/disponibles`     | Public | List cars with `estado = DISPONIBLE`    |
| GET    | `/api/coches/{id}`            | Public | Get a car by id                         |
| POST   | `/api/coches`                 | ADMIN  | Create a car (multipart with images)    |
| PUT    | `/api/coches/{id}`            | ADMIN  | Update a car                            |
| DELETE | `/api/coches/{id}`            | ADMIN  | Delete a car                            |
| POST   | `/api/coches/{id}/imagenes`   | ADMIN  | Add images to an existing car          |

### Media

| Method | Path                          | Auth   | Description                                  |
|--------|-------------------------------|--------|----------------------------------------------|
| POST   | `/api/media/upload`           | ADMIN  | Upload a single image, returns `{ publicId, url }` |
| DELETE | `/api/media/{publicId}`       | ADMIN  | Delete an asset from Cloudinary              |

---

## 🔐 Authentication & authorization

### Flow

```
1. Client sends username + password to POST /api/auth/login
2. AuthService validates credentials against BCrypt-hashed password in DB
3. JwtUtil signs a token (HS256) with claims: username, role, expiration
4. Token is returned to the client in the response body
5. Client stores it (localStorage / state) and sends it on subsequent requests as:
       Authorization: Bearer <token>
6. JwtAuthenticationFilter intercepts every request:
   - Extracts the token from the header
   - Validates signature and expiration
   - Loads the user's role and sets the SecurityContext
7. Method-level @PreAuthorize("hasRole('ADMIN')") guards admin endpoints
```

### Session policy

Sessions are **fully stateless** (`SessionCreationPolicy.STATELESS`). The server holds no session state — every request is authenticated independently from the JWT. This makes horizontal scaling trivial: any instance can serve any request.

### Why JWT (HS256) and not OAuth2?

For a single-tenant app with one frontend, OAuth2 + Authorization Server adds infrastructure without value. HS256 with a shared secret is enough, simpler to operate, and easy to swap for RS256 later if a third-party integration ever needs to verify tokens without the secret.

---

## 📤 Media upload pipeline

The frontend **never holds Cloudinary credentials**. All uploads flow through the backend:

```
┌─────────┐  1. POST /api/media/upload     ┌──────────────┐
│ Browser │  multipart/form-data + JWT     │   Backend    │
└─────────┘ ─────────────────────────────▶ │              │
                                           │  2. Validate │
                                           │     JWT &    │
                                           │     ADMIN    │
                                           │              │
                                           │  3. Generate │
                                           │     UUID as  │
                                           │     publicId │
                                           │              │
                                           │  4. Upload   │
                                           │     via SDK  │
                                           └──────┬───────┘
                                                  │
                                                  ▼
                                           ┌────────────┐
                                           │ Cloudinary │
                                           │  + secret  │
                                           └─────┬──────┘
                                                 │
                                                 │ { secure_url, public_id }
                                                 ▼
                                           ┌──────────────┐
                                           │   Backend    │
                                           │  responds:   │
                                           │  { publicId, │
                                           │    url, ... }│
                                           └──────────────┘
```

Each upload is stored in the `alquiler-coches/` folder on Cloudinary with `resource_type=auto`. Filenames are random UUIDs to prevent collisions and make leaked links unguessable.

The `CocheController.createCoche` endpoint also accepts multipart uploads and orchestrates the full flow: upload images first, then persist the car with the resulting URLs. If persistence fails after upload, the controller deletes the uploaded assets to avoid orphans (compensating action pattern).

---

## 🚨 Error handling

A single `@RestControllerAdvice` (`GlobalExceptionHandler`) translates every exception into a `ProblemDetail` response following [RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807).

| Exception                          | HTTP status | Title                          |
|------------------------------------|-------------|--------------------------------|
| `CocheNotFoundException`           | 404         | `Resource not found`           |
| `UsuarioNotFoundException`         | 404         | `User not found`               |
| `MediaStorageException`            | 500         | `Media storage error`          |
| `MethodArgumentNotValidException`  | 400         | `Validation failed`            |
| `BadCredentialsException`          | 401         | `Invalid credentials`          |
| `AccessDeniedException`            | 403         | `Forbidden`                    |
| `MaxUploadSizeExceededException`   | 413         | `Payload too large`            |
| Anything else                      | 500         | `Internal server error`        |

Sample error response:

```json
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "detail": "matricula: must match Spanish 2000+ plate format",
  "instance": "/api/coches",
  "timestamp": "2026-05-19T11:32:18Z"
}
```

The structure is identical across all errors, which dramatically simplifies error handling on the client side.

---

## 🚀 Getting started

### Prerequisites

- **JDK 25** (OpenJDK or Temurin)
- **Maven 3.9+**
- **MySQL 8.0+** running locally on `localhost:3306`
- **Cloudinary account** (free tier works) for image storage

### Clone and build

```bash
git clone https://github.com/BryanStrk/alquiler-coches-app.git
cd alquiler-coches-app
mvn clean install
```

---

## 🔧 Environment variables

The application reads its configuration from environment variables. Set them via your IDE's run configuration, a `.env` file (gitignored), or directly in the shell.

| Variable                  | Required | Description                                  |
|---------------------------|----------|----------------------------------------------|
| `DB_URL`                  | yes      | JDBC URL (e.g. `jdbc:mysql://localhost:3306/alquiler_coches`) |
| `DB_USERNAME`             | yes      | MySQL user                                   |
| `DB_PASSWORD`             | yes      | MySQL password                               |
| `JWT_SECRET`              | yes      | Signing key for HS256 (≥ 256 bits)           |
| `JWT_EXPIRATION_MS`       | no       | Token TTL in milliseconds (default 24h)      |
| `CLOUDINARY_CLOUD_NAME`   | yes      | Cloudinary cloud name                        |
| `CLOUDINARY_API_KEY`      | yes      | Cloudinary API key                           |
| `CLOUDINARY_API_SECRET`   | yes      | Cloudinary API secret                        |

> `application.properties` references these as `${VAR}` placeholders **without defaults**, so the app **fails fast on startup** if any required variable is missing. This is intentional: silently running with empty credentials is worse than not running at all.

---

## 💾 Database setup

Create the database manually before the first run:

```sql
CREATE DATABASE alquiler_coches CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Hibernate's `ddl-auto=update` will create and migrate the schema automatically. For production, switch to `validate` and manage schema with [Flyway](https://flywaydb.org) or [Liquibase](https://www.liquibase.org).

---

## ▶️ Running the application

### From the command line

```bash
mvn spring-boot:run
```

### From IntelliJ IDEA

1. Open the project (Maven auto-import).
2. Edit run configuration → add environment variables (see [above](#-environment-variables)).
3. Run `AlquilerCochesApplication`.

The API listens on `http://localhost:8080`.

---

## 📖 API documentation (Swagger)

Once the app is running:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

The Swagger UI includes a Bearer auth button — paste a token from `/api/auth/login` to test protected endpoints interactively.

---

## 🌱 Seeded data

On first startup, `DataInitializer` populates the database with development data:

### Users

| Username  | Password     | Role     |
|-----------|--------------|----------|
| `admin`   | `admin123`   | `ADMIN`  |
| `cliente` | `cliente123` | `CLIENT` |

### Cars

Six premium and sport cars covering all combinations of `TipoCombustible` and `EstadoCoche`:

- Tesla Model 3 (Eléctrico, Disponible)
- BMW Serie 3 320d (Diésel, Disponible)
- Toyota Corolla Hybrid (Híbrido, Disponible)
- Audi A4 (Gasolina, Alquilado)
- Renault Clio (Gasolina, Disponible)
- Porsche 911 Carrera (Gasolina, Mantenimiento)

This makes it possible to exercise filters, badges and status logic immediately after cloning the repo.

---

## 📐 Conventions

### Package layout

Strict horizontal layering: `controller` / `service` / `repository` / `entity` / `dto` / `exception` / `config` / `bootstrap`. No vertical (feature-based) packages — the project is small enough that horizontal slicing reads more naturally.

### DTOs

All DTOs are **Java records** (immutable, no boilerplate). Validation annotations sit directly on the record components:

```java
public record CocheRequestDTO(
    @NotBlank String marca,
    @NotBlank String modelo,
    @Pattern(regexp = "^\\d{4}\\s?[BCDFGHJKLMNPRSTVWXYZ]{3}$") String matricula,
    @Positive BigDecimal precioPorDia
) {}
```

### Service interfaces

Every service has an `XxxService` interface and an `XxxServiceImpl` implementation. This keeps controllers loosely coupled and makes unit testing trivial with mocks.

### Lombok usage

- `@RequiredArgsConstructor` for constructor injection.
- `@Getter` / `@Setter` on JPA entities (sparingly).
- No `@Data` on entities (avoid accidental `equals`/`hashCode` over mutable JPA fields).

### Commits

The repository follows [Conventional Commits](https://www.conventionalcommits.org/) with scopes:

```
feat(media): implement Cloudinary upload/delete service
fix(exception): restore cause-aware constructor in MediaStorageException
refactor(dto): move ErrorResponse from exception to dto package
chore(deps): add cloudinary-http5 dependency
docs(entity): add explanatory comments to Coche.imageUrls
```

---

## 🧭 Architectural decisions (ADRs)

### 1. JWT with HS256 instead of session cookies

Stateless tokens scale horizontally without sticky sessions or distributed cache. HS256 is enough for a single-issuer / single-verifier setup; the upgrade path to RS256 is straightforward if a third-party ever needs to verify tokens.

### 2. Cloudinary mediated through the backend, never direct browser uploads

A signed upload preset exposed to the browser would mean trusting the client to apply size limits, format whitelists and quota checks. Routing uploads through the backend keeps all those policies centralized, lets us add virus scanning later, and protects the API secret.

### 3. `@ElementCollection<String>` for car images instead of a dedicated entity

Images carry no behavior of their own and are never queried in isolation. A separate `Imagen` entity would add a join table, an entity class, a repository and a service for no benefit. `@ElementCollection` keeps the schema small and the code obvious.

### 4. UUID-based `public_id` on Cloudinary

Sequential or filename-based public IDs leak information (number of uploads, original filenames) and can collide. Random UUIDs are unguessable and impossible to enumerate.

### 5. Fail-fast on missing environment variables

`${VAR}` without a default forces the application to fail on startup if a variable is missing. This is preferable to silently starting with an empty string and discovering the issue when a user tries to log in or upload.

### 6. `ProblemDetail` (RFC 7807) for all error responses

Custom error formats fragment the contract between client and server. `ProblemDetail` is the standardized, language-agnostic shape — Spring 6 ships it out of the box, and the frontend handles every error path with the same parser.

### 7. Compensating action on failed car creation

If image upload succeeds but the database write fails, the controller deletes the orphan Cloudinary assets. This avoids paying storage for images that no entity references, a common source of cost creep in apps that upload first and persist later.

---

## 🗺️ Roadmap

- [ ] Booking system with date-range overlap detection
- [ ] Pagination + sorting on `GET /api/coches`
- [ ] Soft delete + restore for cars
- [ ] Audit log of admin actions (who edited what, when)
- [ ] Rate limiting on auth endpoints (Bucket4j)
- [ ] Refresh tokens with rotation
- [ ] Flyway migrations + `ddl-auto=validate` in production
- [ ] Docker Compose for local stack (app + MySQL)
- [ ] CI pipeline (GitHub Actions): test + build + container image
- [ ] Integration tests with Testcontainers
- [ ] Observability: structured logging, OpenTelemetry, health checks


## 👤 Author

**Bryan Paico Albines**

[![GitHub](https://img.shields.io/badge/GitHub-BryanStrk-181717?logo=github)](https://github.com/BryanStrk)

---

<div align="center">

⭐ If you find this project useful, consider giving it a star.

</div>
