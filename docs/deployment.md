# Deployment

This document provides step-by-step instructions for deploying the **Tetoca API**, a Spring Boot Multi-tenant SaaS platform. The system relies on isolated schemas, robust testcontainer-backed database setups, and automated Flyway migrations.

For an architectural overview and data flow, see [README.md](../README.md).

---

## Prerequisites

| Requirement | Details |
|:---|:---|
| **Java** | JRE 21+ required for running the application |
| **PostgreSQL** | Version 15+ (Production/Staging database) |
| **Docker** | Required for executing Testcontainers during the CI/CD pipeline |
| **OAuth2 Provider**| Configured Identity Provider (e.g., Keycloak, Auth0, Okta) |

> [!IMPORTANT]
> The database infrastructure must be accessible before the Spring Boot application initializes. Flyway migrations are executed automatically during the application startup process.

---

## Database Migrations (Zero `ddl-auto`)

In production environments, Hibernate's `ddl-auto` is strictly disabled. Database evolution is handled exclusively through **Flyway** via immutable migration scripts.

The project maintains two distinct migration paths to enforce multi-tenancy:
- **Global Path** (`db/migration/global`): Contains schema definitions for shared infrastructure (e.g., tenant registries, common lookup tables).
- **Tenant Path** (`db/migration/tenant`): Contains schema definitions applied dynamically to each isolated tenant schema.

To manually trigger migrations or review pending changes, you can use the Flyway Maven plugin:

```bash
./mvnw flyway:info
./mvnw flyway:migrate
```

---

## Application Configuration

The deployment relies heavily on standard Spring Boot environment variables. You must provide these variables either through your orchestration platform (e.g., Kubernetes ConfigMaps/Secrets) or an `.env` file.

| Variable | Example / Default | Purpose |
|:---|:---|:---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://db:5432/tetoca_db` | Main PostgreSQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database admin user (must have schema creation rights) |
| `SPRING_DATASOURCE_PASSWORD` | `secure_password` | Database admin password |
| `OAUTH2_ISSUER_URI` | `https://auth.example.com/realms/tetoca` | OAuth2 identity provider endpoint |
| `LOGGING_LEVEL_ROOT` | `INFO` | Root logging level (set to DEBUG for deep traces) |
| `SERVER_PORT` | `8080` | Application HTTP port |

---

## CI/CD and Testing

Before deploying, integration tests must pass. The project utilizes **Testcontainers (PostgreSQL)** to spin up a faithful replica of the production environment, ensuring that multiple datasource routing and schema isolation work correctly.

Run the test suite:

```bash
./mvnw clean verify
```

> [!NOTE]
> Ensure the CI/CD runner has Docker access, as Testcontainers will need to pull the PostgreSQL image and start an isolated container for the integration tests.

---

## Running the Application

### 1. Standalone / Bare Metal

Once the PostgreSQL instance is running and configuration is exported to the environment:

```bash
# Package the application
./mvnw clean package -DskipTests

# Run the executable JAR
java -jar target/tetoca-api-3.5.0.jar
```

### 2. Docker / Containerized

Build the Docker image using the Spring Boot Maven Plugin:

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=tetoca/tetoca-api:3.5.0
```

Run the container:

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://<db_host>:5432/tetoca_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e OAUTH2_ISSUER_URI=https://auth.example.com/realms/tetoca \
  tetoca/tetoca-api:3.5.0
```

---

## Observability & Swagger Integration

### Swagger OpenAPI

Once deployed, the OpenAPI documentation is automatically available. It includes explicit inputs for:
- **OAuth2 Authentication**: Authorize requests securely via the interactive UI.
- **Tenant ID**: Every endpoint that accesses tenant data requires a `X-Tenant-ID` header.

Access Swagger UI at:
`http://<SERVER_IP>:8080/swagger-ui.html`

### Logs & MDC

Logs are formatted to inject the `Tenant ID` directly via Logback's Mapped Diagnostic Context (MDC). In production, configure Logback to output JSON (e.g., using `logstash-logback-encoder`) to easily aggregate, search, and filter logs per tenant in platforms like ELK or Datadog.

---

## Troubleshooting

| Problem | Likely Cause | Resolution |
|:---|:---|:---|
| ThreadLocal Data Bleeding | Filter missing `finally` block | Ensure the `TenantContextHolder.clear()` is strictly invoked in the `finally` block of the tenant filter. |
| Schema not found exception | Flyway migration failure | Verify that the tenant exists in the global registry and that Flyway successfully executed `db/migration/tenant` on initialization. |
| Testcontainers timeout | Docker socket unavailable | Verify Docker daemon is running and accessible by the CI user (`/var/run/docker.sock`). |
| Unauthenticated requests in Swagger | Missing OAuth2 setup | Click the "Authorize" button in Swagger UI and provide a valid bearer token before executing endpoints. |
| JPA Entities exposed in API | Controller bypassing DTOs | Strict rule: Ensure all `@RestController` endpoints map requests/responses solely to Java Records or DTOs. |
