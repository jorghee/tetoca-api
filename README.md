![TeToca Application Cover](./.github/tetoca_cover.png)

# <samp>Tetoca API<samp>

A **Spring Boot Multi-Tenant SaaS platform** providing a scalable and isolated RESTful API architecture. It is built to guarantee robust data separation across tenants, maintain high maturity in REST integrations, and ensure enterprise-grade observability and zero-downtime database evolutions.

Built on **Spring Boot 3.5.0**, the system relies on Schema-based Multi-Tenancy, strict DTO boundaries, and immutable Flyway migrations to serve multiple organizations securely and efficiently.

## Table of Contents

- [The Problem](#the-problem)
- [Architecture Overview](#architecture-overview)
- [Data Flow](#data-flow)
- [Components](#components)
- [Multi-Tenancy Strategy](#multi-tenancy-strategy)
- [Observability & Logging](#observability--logging)
- [Quick Start](#quick-start)
- [Documentation](#documentation)

## The Problem

SaaS applications face the constant challenge of ensuring absolute data isolation among different clients (tenants) without sacrificing performance or maintainability. Exposing persistence entities directly in APIs tightly couples the internal database schema to public contracts, leading to brittle integrations.

Tetoca API solves this by enforcing **strict multi-tenant schema isolation**, mandating the use of **Data Transfer Objects (DTOs)** for all client interactions, and managing automated, immutable schema updates seamlessly.

## Architecture Overview

The system strictly decouples the web layer from persistence, utilizing a dynamic routing datasource that resolves the tenant context on every request.

```mermaid
graph TD
    subgraph "Client Layer"
        APP["Web/Mobile Client"]
        SWG["OpenAPI / Swagger UI\n(OAuth2 + Tenant ID)"]
    end

    subgraph "Web Layer (Spring Boot)"
        FLT["Tenant Filter\n(Context & MDC Injection)"]
        CTRL["REST Controllers\n(DTOs only)"]
    end

    subgraph "Business & Data Layer"
        SVC["Service Layer"]
        REPO["JPA Repositories"]
        RDT["Routing DataSource"]
    end

    subgraph "Persistence"
        PG[("PostgreSQL")]
        GSC["Global Schema"]
        TSC1["Tenant A Schema"]
        TSC2["Tenant B Schema"]
        PG --> GSC
        PG --> TSC1
        PG --> TSC2
    end

    APP -->|HTTP/REST| FLT
    SWG -->|HTTP/REST| FLT
    FLT --> CTRL
    CTRL --> SVC
    SVC --> REPO
    REPO --> RDT
    RDT --> PG
```

**Client Layer** interacts with the system using standard OAuth2 protocols, explicitly passing the Tenant ID (typically via HTTP headers). 
**Web Layer** captures the context, sets up the `TenantContextHolder`, and populates MDC for logging. Only DTOs are serialized/deserialized here.
**Data Layer** dynamically routes queries to the correct isolated database schema based on the resolved context.

## Data Flow

The following diagram demonstrates a typical authenticated request lifecycle interacting with the isolated tenant data:

<details>
<summary><b>Click to expand the Data Flow sequence diagram</b></summary>
<br>

```mermaid
sequenceDiagram
    participant C as Client
    participant F as TenantFilter
    participant M as MDC (Logback)
    participant R as RestController
    participant S as Service
    participant D as RoutingDataSource
    participant DB as PostgreSQL

    C->>F: Request with OAuth2 Token + X-Tenant-ID
    F->>F: Resolve Tenant ID
    F->>M: Inject Tenant ID into MDC
    F->>F: Set TenantContextHolder
    F->>R: Forward Request (JSON)
    R->>R: Map JSON to Request DTO
    R->>S: Call Business Logic
    S->>D: Access Repository
    D->>D: Read TenantContextHolder
    D->>DB: Route query to Tenant Schema
    DB-->>S: Return JPA Entities
    S->>R: Return Domain Result
    R->>R: Map Entities to Response DTO
    R-->>C: HTTP 200 OK (JSON)
    
    Note over F: finally block executes
    F->>F: Clear TenantContextHolder (Prevent Leaks)
    F->>M: Clear MDC
```

</details>

## Components

| Component | Technology | Responsibility |
|:---|:---|:---|
| **API Gateway / Filter** | Spring Web | Intercepts requests, validates OAuth2 tokens, and sets `TenantContextHolder`. |
| **REST Controllers** | Spring MVC | Exposes OpenAPI documented endpoints using strict DTO parameters and responses. |
| **Service Layer** | Spring Service | Contains business logic; strictly separated from web and persistence contexts. |
| **Migration Engine** | Flyway | Manages immutable schema definitions (`db/migration/global` and `db/migration/tenant`). |
| **Routing DataSource** | Spring Data JPA | Dynamically selects the active PostgreSQL schema per tenant context. |
| **Testing Engine** | Testcontainers | Spins up isolated PostgreSQL instances for accurate integration testing. |

## Multi-Tenancy Strategy

Tetoca API enforces **Schema Isolation** (a database per tenant or schema per tenant model). 

1. **Context Management**: The `TenantContextHolder` stores the active tenant context. It is strictly cleared in a `finally` block of the web filter to prevent ThreadLocal leaks during Tomcat/Netty thread reuse.
2. **Immutability (Zero ddl-auto)**: Hibernate's `ddl-auto` is disabled in production. Database alterations are strictly performed via Flyway scripts.
3. **Data Access**: JPA Entities (`@Entity`) are strictly contained within the data layer and never exposed in REST controllers.

## Observability & Logging

Every log generated within a tenant's transactional boundary includes the `Tenant ID`. We utilize SLF4J and Logback's **Mapped Diagnostic Context (MDC)**. This ensures that every thread trace is automatically tagged, allowing centralized logging platforms (like ELK or Datadog) to filter and alert per tenant securely.

## Quick Start

### Prerequisites

- Java 21+
- Maven
- Docker and Docker Compose (for Testcontainers)

### Local Development

Compile the project and run tests (Testcontainers will automatically spin up PostgreSQL for database integration tests):

```bash
./mvnw clean install
```

Run the application:

```bash
./mvnw spring-boot:run
```

Access the OpenAPI (Swagger) UI at: `http://localhost:8080/swagger-ui.html`. The documentation explicitly supports OAuth2 authentication and the injection of the Tenant ID header.

## Documentation

| Document | Description |
|:---|:---|
| [Deployment](docs/deployment.md) | Infrastructure setup, migrations, and environment configuration |

