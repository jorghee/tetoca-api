# Multi-Tenant Data Flow

This document details the lifecycle of a request flowing through the `tetoca-api` multi-tenant architecture. It explains how authentication, context resolution, and database routing are executed securely and reliably.

---

## Request Lifecycle

The system utilizes an interception and thread-local context pattern to route multi-tenant requests.

```mermaid
sequenceDiagram
    participant Client
    participant Controller as REST Controller
    participant AuthFilter as JwtAuthenticationFilter
    participant TenantFilter
    participant Context as TenantContextHolder
    participant Service as Business Service
    participant Repo as Spring Data JPA
    participant RoutingDS as TenantRoutingDataSource
    participant DB as PostgreSQL (Tenant Schema)

    Client->>AuthFilter: HTTP GET /api/v1/tenant/t-123/users\nAuthorization: Bearer <JWT>
    
    activate AuthFilter
    AuthFilter->>AuthFilter: Validate JWT signature & expiration
    AuthFilter->>AuthFilter: Set Spring SecurityContext
    AuthFilter->>TenantFilter: chain.doFilter()
    deactivate AuthFilter
    
    activate TenantFilter
    TenantFilter->>TenantFilter: Extract "t-123" from URL path
    TenantFilter->>Context: setTenantId("t-123")
    
    activate Context
    Context-->>TenantFilter: ThreadLocal populated
    deactivate Context
    
    TenantFilter->>Controller: chain.doFilter()
    
    activate Controller
    Controller->>Service: getUsers()
    
    activate Service
    Service->>Repo: findAll()
    
    activate Repo
    Repo->>RoutingDS: getConnection()
    
    activate RoutingDS
    RoutingDS->>Context: determineCurrentLookupKey()
    
    activate Context
    Context-->>RoutingDS: Returns "t-123"
    deactivate Context
    
    RoutingDS->>RoutingDS: Resolve DataSource for "t-123"
    RoutingDS-->>Repo: Returns connection to DB t-123
    deactivate RoutingDS
    
    Repo->>DB: Execute SQL SELECT
    
    activate DB
    DB-->>Repo: ResultSet
    deactivate DB
    
    Repo-->>Service: List<UserEntity>
    deactivate Repo
    
    Service->>Service: Map Entities to DTOs
    Service-->>Controller: List<UserResponseDTO>
    deactivate Service
    
    Controller-->>TenantFilter: ResponseEntity (200 OK)
    deactivate Controller
    
    TenantFilter->>Context: clear() (in finally block)
    Note over TenantFilter,Context: CRITICAL: Prevents ThreadLocal leaks
    
    TenantFilter-->>Client: HTTP 200 OK + JSON
    deactivate TenantFilter
```

---

## Key Mechanisms

### Authentication (JwtAuthenticationFilter)

Before tenant routing occurs, the request must be authenticated. The `JwtAuthenticationFilter` validates the token provided in the `Authorization` header. It establishes the identity of the user but does not set the database routing context.

### Context Extraction (TenantFilter)

The `TenantFilter` operates after authentication. For any request matching the `/api/v1/tenant/{tenantId}/**` pattern:
1. It extracts the `{tenantId}` variable.
2. It ensures the authenticated user (from `SecurityContext`) has the authorization to access this tenant.
3. It sets the `TenantContextHolder`.

### Data Source Routing (TenantRoutingDataSource)

When Spring Data JPA requires a database connection, it delegates to the `DataSource` bean. In `tetoca-api`, this bean is the `TenantRoutingDataSource` (extending `AbstractRoutingDataSource`).
It overrides `determineCurrentLookupKey()`, which retrieves the active ID from `TenantContextHolder`. It then fetches the corresponding JDBC connection pool for that specific database.

### Context Clearing

Due to thread reuse in modern web servers (Tomcat/Netty), if a `ThreadLocal` is not cleared, a subsequent request processed by the same thread might accidentally inherit the previous request's tenant context. This causes catastrophic data bleeding. 
Therefore, `TenantContextHolder.clear()` is strictly called in a `finally` block within `TenantFilter`, guaranteeing execution even if the request throws an unhandled exception.

---

## New Tenant Provisioning Flow

When a new customer signs up, a sequence of events orchestrates the creation of their isolated environment.

```mermaid
sequenceDiagram
    participant Admin
    participant Controller as CompanyController
    participant Svc as CompanyManagementService
    participant GlobalRepo as InstanceRepository
    participant ProvSvc as DatabaseProvisioningService
    participant Flyway
    participant GlobalDB as Global DB
    participant TenantDB as New Tenant DB

    Admin->>Controller: POST /api/v1/global/companies (TenantReq)
    activate Controller
    
    Controller->>Svc: registerCompany()
    activate Svc
    
    Svc->>GlobalRepo: existsByTenantIdIgnoreCase?
    activate GlobalRepo
    GlobalRepo-->>Svc: false
    deactivate GlobalRepo
    
    Svc->>GlobalRepo: save(Instance)
    activate GlobalRepo
    GlobalRepo->>GlobalDB: INSERT INTO instances
    GlobalDB-->>GlobalRepo: OK
    GlobalRepo-->>Svc: Instance Entity
    deactivate GlobalRepo
    
    Svc->>ProvSvc: publish ProvisioningEvent(tenantId)
    activate ProvSvc
    
    ProvSvc->>ProvSvc: createSchemaForTenant()
    ProvSvc->>TenantDB: CREATE DATABASE / SCHEMA
    
    ProvSvc->>Flyway: migrate(db/migration/tenant)
    activate Flyway
    Flyway->>TenantDB: Execute Tenant DDLs
    TenantDB-->>Flyway: Success
    Flyway-->>ProvSvc: Migrations applied
    deactivate Flyway
    
    ProvSvc-->>Svc: Provisioning Complete
    deactivate ProvSvc
    
    Svc-->>Controller: CompanyResponseDTO
    deactivate Svc
    
    Controller-->>Admin: HTTP 201 Created
    deactivate Controller
```
